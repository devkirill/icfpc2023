package icfpc.y2023

import icfpc.y2023.db.repository.SolutionRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
@ConditionalOnProperty(name = ["scheduler.scores"], matchIfMissing = false)
class UpdateScoresService(
    val calcMetric: CalcMetric,
    val solutionRepository: SolutionRepository
) {
    @Scheduled(fixedRateString = "2000")
    fun update() {
        println("update")
        solutionRepository.findAllByScoreIsNull().forEach {
            it.score = calcMetric.calcMetric(it)
            solutionRepository.save(it)
        }
    }
}
