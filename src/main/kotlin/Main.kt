//import icfpc.y2023.model.Point
//import icfpc.y2023.model.Solve
//import icfpc.y2023.model.Task
//import icfpc.y2023.service.CalcScoringService
//import icfpc.y2023.utils.readUrl
//import icfpc.y2023.utils.send
//import java.net.URL
//import java.util.*
//import java.util.stream.Collectors
//import kotlin.math.abs
//import kotlin.math.min
//import kotlin.random.Random
//import kotlin.random.nextInt
//
//val domain = "http://localhost:8888"
//
//val calc = CalcScoringService()
//
//fun main(args: Array<String>) {
////    val problems = (1..55).map { id ->
////        val problem = Task.parse(readUrl("http://localhost:8888/problem/$id"))
////        println("$id\t${problem.musicians.size}\t${problem.attendees.size}\t${problem.attendees.size*problem.musicians.size*problem.musicians.size}")
////
////    }
//
//    val problemCount = readUrl("${borders.domain}/problems").count { it == '}' }
//
//
//    fun testCalc(id: Int) {
//        val problem = Task.parse(readUrl("${borders.domain}/problem/$id"))
////        if (problem.musicians.size > 20) {
////            return
////        }
//
//        val begin = Date()
//        val best =
//            (1..100).toList().stream()
//                .parallel()
//                .map { learn(problem) }
//                .map { calc.calc(problem, it) to it }
//                .collect(Collectors.toList())
//                .maxByOrNull { it.first }!!
////        if (best.first <= 0) {
////            println("not found solution $id at ${Date().time - begin.time}ms")
////            return
////        }
//        if (best.second.placements.size < problem.musicians.size) {
//            println("too many placements #$id ${best.second.placements.size} < ${problem.musicians.size}")
//            throw IllegalStateException("too many placements #$id")
//        }
//
////        val out = genRandom(problem)
////        calc.calc(problem, out)
//        URL("${borders.domain}/add/$id?calc=true").send(best.second)
//        println("calc $id at ${Date().time - begin.time}ms")
//    }
//
//    repeat(2) {
//        (1..problemCount).shuffled().forEach {
//            testCalc(it)
//        }
//    }
//
////    testCalc(42)
////    testCalc(43)
////    testCalc(55)
////    testCalc(18)
////    testCalc(19)
////    testCalc(1)
//}
//
//fun learn(problem: Task): Solve {
//    val size = problem.musicians.size
//    val cells = (0..(problem.stage_width.toInt() - 20) step 10).flatMap { x->
//        (0..(problem.stage_height.toInt() - 20) step 10).map { y ->
//            Point(
//                x.toDouble() + problem.stage_bottom_left[0] + 10.0,
//                y.toDouble() + problem.stage_bottom_left[1] + 10.0
//            )
//        }
//    }
//
//    fun genRandom(): List<Point> {
//        return cells.shuffled().take(size)
//    }
//
//    fun mutate(solve: List<Point>): List<Point> {
//        when (Random.nextInt(1..2)) {
//            1 -> {
//                val i = Random.nextInt(1 until size)
//                val j = Random.nextInt(0 until i)
//                val res = solve.toMutableList()
//                val t = res[i]
//                res[i] = res[j]
//                res[j] = t
//                return res
//            }
//            2 -> {
//                val i = Random.nextInt(0 until size)
//                val c = cells.shuffled().first { it !in solve }
//                val res= solve.toMutableList()
//                res[i] = c
//                return res
//            }
//            else -> {
//                throw IllegalStateException("not supported")
//            }
//        }
//    }
//
//    (1..10).map {genRandom()}.sortedBy { it }
//
//
//}
//
////
////
//////fun mutate(problem: Task)
