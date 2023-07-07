//import icfpc.y2023.model.Point
//import icfpc.y2023.model.Solve
//import icfpc.y2023.model.Task
//import icfpc.y2023.utils.readUrl
//import icfpc.y2023.utils.send
//import java.net.URL
//import kotlin.random.Random
//
//fun main(args: Array<String>) {
////    val problems = (1..45).map { id ->
////        val problem = Task.parse(readUrl("http://localhost:8888/problem/$id"))
////        println("$id\t${problem.musicians?.size}\t${problem.attendees?.size}")
////
////    }
//
//    (42..43).map { id ->
//        val problem = Task.parse(readUrl("http://localhost:8080/problem/$id"))
//        val out = genRandom(problem)
//        URL("http://localhost:8080/add/$id").send(out)
//    }
//}
//
//fun genRandom(problem: Task): Solve {
//    return Solve(problem.musicians.map {
//        Point(
//            Random.nextInt(problem.stageWidth.toInt()).toDouble() + problem.stageBottomLeft[0],
//            Random.nextInt(problem.stageHeight.toInt()).toDouble() + problem.stageBottomLeft[1]
//        )
//    })
//}
