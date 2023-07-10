package icfpc.y2023

import icfpc.y2023.model.Pillar
import icfpc.y2023.model.Point
import icfpc.y2023.model.Solve
import icfpc.y2023.model.Task
import icfpc.y2023.service.CalcScoringService
import icfpc.y2023.utils.readUrl
import icfpc.y2023.utils.send
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import utils.*
import java.net.URL
import java.util.*
import kotlin.math.abs
import kotlin.math.max

class SeparateAlgorithmTestsV3_2 {
    val scoring = CalcScoringService()

    companion object {
        @JvmStatic
        fun ids(): List<Int> {
            return (1..getProblemsCount()).toList().sortedByDescending { abs(it - 18) }//.shuffled()
        }
    }

    @ParameterizedTest
    @MethodSource("ids")
    fun test2(id: Int) {
        val problem = Task.parse(readUrl("$domain/problem/$id"))
        val solution = getPlaceFor(problem)
        val score = scoring.calc(problem, solution)
        URL("$domain/add/$id?calc=true").send(solution)
        println("score for $id: $score")
    }

    fun getPlaceFor(problem: Task): Solve {
        val sourceCells = getCells(problem).shuffled()
        val scale = calcScale(problem)
        var dist = 1
        val borderCells = sourceCells.filterBorder(problem, dist = dist)
        val cells = borderCells.toMutableSet()
        println("cellssize ${cells.size}")
//        println("cells ${cells}")
        val mPoints = mutableSetOf<Point>()

        val musicians = mutableMapOf<Int, MutableSet<Int>>()
        problem.musicians.forEachIndexed { i, m ->
            val set = musicians[m] ?: mutableSetOf()
            set += i
            musicians[m] = set
        }

        val pillars = problem.pillars.map { Pillar(it.center[0], it.center[1], it.radius) }.toMutableList()
        val result = mutableMapOf<Int, Point>()

        val data = borderCells.pmap { mPos ->
            val otherPillars =
                borderCells.filter { it != mPos }.map { Pillar(it.x, it.y, 5.0) } +
                        extraPillars(problem, borderCells.filter { it != mPos })
            musicians.keys.map { instr ->
                val score = scoring.calcWithPillars(problem, instr, mPos, pillars + otherPillars)
                score to instr
            }.map { Triple(mPos, it.first, it.second) }
        }.flatten()

        while (mPoints.size < problem.musicians.size && cells.isNotEmpty()) {
            val (cell, score, instr) = data.filter { it.first !in mPoints && it.third in musicians }
                .maxByOrNull { (_, l, _) -> l } ?: break
            if (score <= 0) {
                break
            }
            cells -= cell
            musicians[instr]!!.let { set ->
                val index = set.first()
                println("found best place for $instr on $cell at $score")
                mPoints += cell
                result[index] = cell
                set.remove(index)
                if (set.isEmpty()) {
                    musicians.remove(instr)
                }
            }
        }

        musicians.values.flatten().let { notFilled ->
            if (notFilled.isNotEmpty()) {
                println("not filled ${notFilled.size} cells")
                val scores = scoring.calcInfluencesWithoutVolume(
                    problem,
                    (0 until problem.musicians.size).map { result[it] ?: Point(-1000000000.0, -1000000000.0) })
                    .mapIndexed { i, it -> i to it }.sortedByDescending { it.second }
                val map = result.entries.associate { it.value to scores[it.key].second }
                val notUsed =
                    LinkedList(sourceCells.filter { it !in mPoints }
                        .sortedByDescending { p -> mPoints.sumOf { map[it]!!.toDouble() / (p dist it) } })
                notFilled.forEach {
                    result[it] = notUsed.removeFirst()
                }
            }
        }

        return addVolume(problem, result.entries.sortedBy { it.key }.map { it.value })
    }

    fun calcScale(problem: Task): Double {
        val cells = getCells(problem, false)
        val coef = getCoef(problem, cells)
        return max(coef.x, coef.y)
    }

    fun addVolume(problem: Task, placement: List<Point>): Solve {
        val influenceCoeff = scoring.calcInfluencesWithoutVolume(problem, placement)
            .map { if (it > 0) 10.0 else 0.0 }
        return Solve(placement, influenceCoeff)
    }

    fun extraPillars(problem: Task, cells: List<Point>): List<Pillar> {
        val l = cells.mapIndexed { i, a -> a to cells.filterIndexed { j, b -> i < j && a dist b < 12 } }
            .flatMap { (a, l) -> l.map { a to it } }
        return l.flatMap { (a, b) ->
            val c = (a + b) / 2.0
            val n = (b - a)
            val n1 = Point(-n.y, n.x).norm() * 5.0 + c
            val n2 = Point(n.y, -n.x).norm() * 5.0 + c
            val list = mutableListOf<Point>()
            if (scoring.inBorders(problem, n1)) list += n1
            if (scoring.inBorders(problem, n2)) list += n2
            list
        }.map { Pillar(it.x, it.y, 5.001) }
    }
}
