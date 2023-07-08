package icfpc.y2023.scheduled

import icfpc.y2023.service.UploadService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
@ConditionalOnProperty(name = ["service.upload"], matchIfMissing = false)
class UploadBestSolutionsService(
    val uploadService: UploadService
) {
    @Scheduled(fixedRateString = "30000")
    fun update() {
        uploadService.uploadBests()
    }
}
