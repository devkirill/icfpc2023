package icfpc.y2023.scheduled

import icfpc.y2023.db.repository.SolutionRepository
import icfpc.y2023.service.CalcScoringService
import jakarta.transaction.Transactional
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
@ConditionalOnProperty(name = ["service.calc"], matchIfMissing = false)
class UpdateScoresService(
    val calcMetric: CalcScoringService,
    val solutionRepository: SolutionRepository
) {
    @Scheduled(fixedRateString = "2000")
    @Transactional
    fun update() {
        solutionRepository.findAllByScoreIsNull().shuffled().forEach {
            it.score = calcMetric.calc(it)
            solutionRepository.save(it)
        }
    }
}
