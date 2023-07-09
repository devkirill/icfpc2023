package icfpc.y2023

import icfpc.y2023.db.model.Solution
import icfpc.y2023.model.Pillar
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

class SeparateAlgorithmTestsV3 {
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

        val musicians = mutableMapOf<Int, MutableSet<Int>>()
        problem.musicians.forEachIndexed { i, m ->
            val set = musicians[m] ?: mutableSetOf()
            set += i
            musicians[m] = set
        }

        val pillars = problem.pillars.map { Pillar(it.center[0], it.center[1], it.radius) }.toMutableList()
        val result = mutableMapOf<Int, Point>()

        while (mPoints.size < problem.musicians.size && cells.isNotEmpty()) {
            val list = cells.map { mPos ->
                val (score, instr) = musicians.map { (instr, musicians) ->
                    val score = scoring.calcWithPillars(problem, instr, mPos, pillars)
                    score to instr
                }.maxByOrNull { it.first }!!
                Triple(mPos, score, instr)
            }

            val (cell, score, instr) = list.maxBy { (_, l, _) -> l }
            if (score <= 0) {
                continue
            }
            cells -= cell
            musicians[instr]!!.let { set ->
                val index = set.first()
                println("found best place for $instr on $cell")
                mPoints += cell
                result[index] = cell
                set.remove(index)
                if (set.isEmpty()) {
                    musicians.remove(instr)
                }
            }

        }
        for (m in musicians.)

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
