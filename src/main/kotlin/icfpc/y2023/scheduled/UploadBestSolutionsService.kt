package icfpc.y2023.scheduled

import icfpc.y2023.db.repository.ProblemRepository
import icfpc.y2023.db.repository.SolutionRepository
import icfpc.y2023.db.repository.findBest
import icfpc.y2023.service.UploadService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
@ConditionalOnProperty(name = ["service.upload"], matchIfMissing = false)
class UploadBestSolutionsService(
    val problemRepository: ProblemRepository,
    val solutionRepository: SolutionRepository,
    val uploadService: UploadService
) {
    @Scheduled(fixedRateString = "30000")
    fun update() {
        problemRepository.findAll().sortedBy { it.id }.forEach { problem ->
            try {
                val solution = solutionRepository.findBest(problem) ?: return@forEach
                if (problem.bestScore == null || solution.score!! < problem.bestScore!!) {
                    uploadService.upload(solution)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
