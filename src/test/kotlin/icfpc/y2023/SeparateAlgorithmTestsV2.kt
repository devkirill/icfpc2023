package icfpc.y2023

import icfpc.y2023.model.Point
import icfpc.y2023.model.Solve
import icfpc.y2023.model.Task
import icfpc.y2023.service.CalcScoringService
import icfpc.y2023.utils.readUrl
import icfpc.y2023.utils.send
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import utils.domain
import utils.filterBorder
import utils.getCells
import utils.getProblemsCount
import java.net.URL

class SeparateAlgorithmTestsV2 {
    val scoring = CalcScoringService()

    companion object {
        @JvmStatic
        fun ids(): List<Int> {
            return (1..getProblemsCount()).toList().shuffled()
//            return listOf(88)
        }
    }

    @ParameterizedTest
    @MethodSource("ids")
    fun test2(id: Int) {
        val problem = Task.parse(readUrl("$domain/problem/$id"))
        val solution = getPlaceFor(problem)
        var score = scoring.calc(problem, solution)
        URL("$domain/add/$id?calc=true").send(solution)
        println("score for $id: $score")
    }


    fun getPlaceFor(problem: Task): Solve {
        val sourceCells = getCells(problem).shuffled()
        var dist = 1
        val cells = filterBorder(problem, sourceCells, dist = dist).toMutableSet()
        println("cellssize ${cells.size}")
        println("cells ${cells}")
        val mPoints = mutableSetOf<Point>()
        for (m in problem.musicians) {
            if (cells.isEmpty()) {
                dist++
                cells += filterBorder(problem, sourceCells, dist = dist)
                    .filter { it !in mPoints }
            }
            val cell = cells.maxBy { mPos ->
                problem.attendees.map({ scoring.calcSimpleInfluence(m, mPos, it) }).sum()
            }
            println("found best place for $m on $cell")
            cells -= cell
            mPoints += cell
        }
        return Solve(mPoints.toList())
    }
}
