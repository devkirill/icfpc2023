package icfpc.y2023.utils

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

fun readUrl(urlString: String): String {
    var reader: BufferedReader? = null
    var result = "{}"
    try {
        val url = URL(urlString)
        reader = BufferedReader(InputStreamReader(url.openStream()))
        val buffer = StringBuffer()
        var read: Int
        val chars = CharArray(1024)
        while (reader.read(chars).also { read = it } != -1) buffer.append(chars, 0, read)
        result = buffer.toString()
    } finally {
        reader?.close()
    }
    return result
}

fun <A> URL.send(obj: A) {
    val json = ObjectMapper().writeValueAsString(obj)
    val con: HttpURLConnection = this.openConnection() as HttpURLConnection
    con.setRequestMethod("POST")
    con.setRequestProperty("Content-Type", "application/json")
    con.setDoOutput(true)
    con.outputStream.use { os ->
        val input = json.toByteArray(charset("utf-8"))
        os.write(input, 0, input.size)
    }
    if (con.responseCode != 200) {
        throw IllegalStateException("${con.responseCode} - ${con.responseMessage}")
    }
//    println(con.responseMessage)
}
