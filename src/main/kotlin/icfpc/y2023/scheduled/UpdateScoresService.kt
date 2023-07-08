package icfpc.y2023.scheduled

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
    val solutionRepository: SolutionRepository
) {
    @Scheduled(fixedRateString = "2000")
    @Transactional
    fun update() {
        solutionRepository.findAllByScoreIsNull().shuffled().forEach {
            val begin = Date()
            it.score = calcMetric.calc(it)
            solutionRepository.save(it)
            println("calc ${it.id}[${it.problem.id}] at ${Date().time - begin.time}ms")
        }
    }
}
