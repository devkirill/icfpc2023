package icfpc.y2023.utils

import java.io.BufferedReader
import java.io.InputStreamReader
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
