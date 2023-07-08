package icfpc.y2023.scheduled

import icfpc.y2023.db.repository.ProblemContentRepository
import icfpc.y2023.db.repository.SolutionRepository
import icfpc.y2023.service.CalcScoringService
import jakarta.transaction.Transactional
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Service
@EnableScheduling
@ConditionalOnProperty(name = ["service.calc"], matchIfMissing = false)
class UpdateScoresService(
    val calcMetric: CalcScoringService,
    val solutionRepository: SolutionRepository,
    val problemContentRepository: ProblemContentRepository
) {
    @Scheduled(fixedRateString = "2000")
    fun update() {
        val begin = Date()
        var first = true
        var res = true
        while (res && (Date().time - begin.time < 2000)) {
            res = calc()
            if (!res && first) {
                return
            }
            first = false
        }
        println("score update end at ${Date().time - begin.time}ms")
    }

    @Transactional
    fun calc(): Boolean {
        val id = solutionRepository.findNotCalculated().shuffled().firstOrNull() ?: return false
        val begin = Date()
        val solution = solutionRepository.getReferenceById(id)
        val contentId = solution.problemId
        solution.score =
            calcMetric.calc(problemContentRepository.getReferenceById(contentId).content, solution.contents)
        solutionRepository.save(solution)
        println("calc ${solution.id}[${solution.problem.id}] at ${Date().time - begin.time}ms")
        return true
    }
}
