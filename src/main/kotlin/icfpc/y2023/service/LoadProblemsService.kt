package icfpc.y2023.service

import icfpc.y2023.db.model.Problem
import icfpc.y2023.db.model.ProblemContent
import icfpc.y2023.db.repository.ProblemContentRepository
import icfpc.y2023.db.repository.ProblemRepository
import icfpc.y2023.model.Task
import icfpc.y2023.utils.minimizeJson
import icfpc.y2023.utils.readUrl
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class LoadProblemsService(
    val problemRepository: ProblemRepository,
    val problemContentRepository: ProblemContentRepository
) {
    fun getProblemsCount(): Int {
        val html = readUrl("https://www.icfpcontest.com/problems")
        val results = Regex("\"numberOfProblems\":(\\d+)\\b").find(html)
        return results?.groups?.get(1)?.value?.toInt() ?: 0
    }

    @Transactional
    fun loadProblems() {
        (1..getProblemsCount()).map {
            // https://api.icfpcontest.com/problem?problem_id=1
            val json = readUrl("https://cdn.icfpcontest.com/problems/$it.json").let {
                minimizeJson(it)
            }
            val task = Task.parse(json)
            problemRepository.findById(it).ifPresentOrElse({ problem ->
                val contentId = problem.problemId
                val content = problemContentRepository.getReferenceById(contentId)
                if (content.content != task) {
                    problem.problemId = problemContentRepository.save(ProblemContent(content = task)).id!!
                    problemRepository.save(problem)
                }
            }, {
                val contentId = problemContentRepository.save(ProblemContent(content = task)).id!!
                problemRepository.save(Problem(id = it, problemId = contentId))
            })
        }
    }
}
