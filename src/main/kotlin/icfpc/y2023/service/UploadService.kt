package icfpc.y2023.service

import com.fasterxml.jackson.databind.ObjectMapper
import icfpc.y2023.db.model.Solution
import icfpc.y2023.db.repository.ProblemRepository
import icfpc.y2023.db.repository.SolutionRepository
import icfpc.y2023.db.repository.findBest
import icfpc.y2023.utils.toJson
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.min


@Service
class UploadService(
    val problemRepository: ProblemRepository,
    val solutionRepository: SolutionRepository,
    @Value("\${token}")
    val token: String,
) {
    @Transactional
    fun uploadBests() {
        for (problem in problemRepository.findAll()) {
            try {
                val solution = solutionRepository.findBest(problem) ?: continue
                if (solution.score == null || solution.score!! <= 0) {
                    continue
                }
                if (problem.lastSendedId == solution.id) {
                    continue
                }
                if (problem.bestScore == null || solution.score!! > problem.bestScore!!) {
                    upload(solution)
                    return
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    @Transactional
    fun upload(solution: Solution) {
        if (token.isBlank()) {
            throw IllegalStateException("not found token")
        }

        val json = ObjectMapper().writeValueAsString(
            mapOf(
                "problem_id" to solution.problem.id,
                "contents" to solution.contents.toJson()
            )
        )

        val url = URL("https://api.icfpcontest.com/submission")
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        con.setRequestMethod("POST")
        con.setRequestProperty("Content-Type", "application/json")
        con.setRequestProperty("Authorization", "Bearer $token")
        con.setDoOutput(true)
        con.outputStream.use { os ->
            val input = json.toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }
        println("upload ${solution.problem.id}(${solution.score}) - ${solution.contents}")
        if (con.responseCode !in 200..201) {
            println(con.responseCode)
            println(con.responseMessage)
            return
        }
//        BufferedReader(
//            InputStreamReader(con.inputStream, "utf-8")
//        ).use { br ->
//            val response = StringBuilder()
//            var responseLine: String? = null
//            while (br.readLine().also { responseLine = it } != null) {
//                response.append(responseLine!!.trim { it <= ' ' })
//            }
//            println(response.toString())
//        }

        solution.problem.apply {
            lastSendedId = solution.id
            bestScore = if (bestScore != null) min(bestScore!!, solution.score!!) else solution.score
        }.let {
            problemRepository.save(it)
        }
    }
}
