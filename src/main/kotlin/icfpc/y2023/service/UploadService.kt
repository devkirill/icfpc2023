package icfpc.y2023.service

import com.fasterxml.jackson.databind.ObjectMapper
import icfpc.y2023.db.model.Solution
import icfpc.y2023.db.repository.ProblemRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.min


@Service
class UploadService(
    val problemRepository: ProblemRepository,
    @Value("\${token}")
    val token: String,
) {
    @Transactional
    fun upload(solution: Solution) {
        if (token.isBlank()) {
            throw IllegalStateException("not found token")
        }

        val json = ObjectMapper().writeValueAsString(
            mapOf(
                "problem_id" to solution.problem.id,
                "contents" to solution.contents
            )
        )
        println("upload ${solution.problem.id}(${solution.score}) - ${solution.contents}")

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
        println(con.responseCode)
        println(con.responseMessage)
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
