package icfpc.y2023.service

import icfpc.y2023.db.model.Problem
import icfpc.y2023.db.repository.ProblemRepository
import icfpc.y2023.model.Task
import icfpc.y2023.utils.minimizeJson
import icfpc.y2023.utils.readUrl
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class LoadProblemsService(val problemRepository: ProblemRepository) {
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
                if (problem.problem != task) {
                    problem.problem = task
                    problemRepository.save(problem)
                }
            }, {
                problemRepository.save(Problem(id = it, problem = task))
            })
        }
    }
}
