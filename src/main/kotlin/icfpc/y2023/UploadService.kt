package icfpc.y2023

import icfpc.y2023.db.model.Solution
import icfpc.y2023.db.repository.ProblemRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class UploadService(
    val problemRepository: ProblemRepository,
) {
    @Transactional
    fun upload(solution: Solution) {
        println("upload ${solution.problem.id}(${solution.score}) - ${solution.solution}")
        solution.problem.apply {
            lastSendedId = solution.id
            bestScore = if (bestScore != null) min(bestScore!!, solution.score!!) else solution.score
        }.let {
            problemRepository.save(it)
        }
    }
}
