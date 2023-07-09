package icfpc.y2023

import icfpc.y2023.model.Point
import icfpc.y2023.model.Solve
import icfpc.y2023.model.Task
import icfpc.y2023.service.CalcScoringService
import icfpc.y2023.utils.readUrl
import icfpc.y2023.utils.send
import icfpc.y2023.db.model.Solution
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
        }
    }

    // @ParameterizedTest
    // @MethodSource("ids")
    fun test2(id: Int) {
        val problem = Task.parse(readUrl("$domain/problem/$id"))
        val solution = getPlaceFor(problem)
        var score = scoring.calc(problem, solution)
        URL("$domain/add/$id?calc=true").send(solution)
        println("score for $id: $score")
    }

    @ParameterizedTest
    @MethodSource("ids")
    fun enchancer(id: Int) {
        val problem = Task.parse(readUrl("$domain/problem/$id"))
        val oldSolution = Solution.parse(readUrl("$domain/best/$id"))
        val solved = oldSolution.contents
        val properSolution = addVolume(problem, solved.placements)

        var scoreNew = scoring.calc(problem, properSolution)
        var scoreOld = scoring.calc(problem, solved)
        println("scores for $id: new $scoreNew, old $scoreOld, stored ${oldSolution.score}")
        if (scoreNew > scoreOld || scoreOld != oldSolution.score) {
            URL("$domain/add/$id?calc=true").send(properSolution)
            println("problem $id updated")
        }
    }


    fun getPlaceFor(problem: Task): Solve {
        val sourceCells = getCells(problem).shuffled()
        var dist = 1
        val cells = sourceCells.filterBorder(problem, dist = dist).toMutableSet()
        println("cellssize ${cells.size}")
        println("cells ${cells}")
        val mPoints = mutableSetOf<Point>()
        for (m in problem.musicians) {
            if (cells.isEmpty()) {
                dist++
                cells += sourceCells.filterBorder(problem, dist = dist)
                    .filter { it !in mPoints }
            }
            val cell = cells.maxBy { mPos ->
                problem.attendees.map({ scoring.calcSimpleInfluence(m, mPos, it) }).sum()
            }
            println("found best place for $m on $cell")
            cells -= cell
            mPoints += cell
        }
        val placement = mPoints.toList()
        return addVolume(problem, placement)
    }

    fun addVolume(problem: Task, placement: List<Point>): Solve {
        val influenceCoeff = scoring.calcInfluencesWithoutVolume(problem, placement)
            .map { if (it > 0) 10.0 else 0.0 }
        return Solve(placement, influenceCoeff)
    }
}
