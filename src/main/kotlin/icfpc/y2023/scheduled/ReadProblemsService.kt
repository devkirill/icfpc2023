package icfpc.y2023.scheduled

import icfpc.y2023.service.LoadProblemsService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
@ConditionalOnProperty(name = ["service.read"], matchIfMissing = false)
class ReadProblemsService(
    val loadProblemsService: LoadProblemsService
) {
    @Scheduled(fixedRateString = "55000")
    fun update() {
        loadProblemsService.loadProblems()
    }
}
