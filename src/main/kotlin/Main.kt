import icfpc.y2023.model.Point
import icfpc.y2023.model.Solve
import icfpc.y2023.model.Task
import icfpc.y2023.utils.readUrl
import icfpc.y2023.utils.send
import java.net.URL
import java.util.*
import kotlin.random.Random

val domain = "http://localhost:8888"

fun main(args: Array<String>) {
//    val problems = (1..55).map { id ->
//        val problem = Task.parse(readUrl("http://localhost:8888/problem/$id"))
//        println("$id\t${problem.musicians.size}\t${problem.attendees.size}\t${problem.attendees.size*problem.musicians.size*problem.musicians.size}")
//
//    }

    fun ccc(id: Int) {
        val problem = Task.parse(readUrl("$domain/problem/$id"))
        val out = genRandom(problem)
        val begin = Date()
        URL("$domain/add/$id?calc=true").send(out)
        println("calc $id at ${Date().time - begin.time}ms")
    }

    ccc(18)
}

fun genRandom(problem: Task): Solve {
    return Solve(problem.musicians.map {
        Point(
            Random.nextInt(problem.stage_width.toInt()).toDouble() + problem.stage_bottom_left[0],
            Random.nextInt(problem.stage_height.toInt()).toDouble() + problem.stage_bottom_left[1]
        )
    })
}
