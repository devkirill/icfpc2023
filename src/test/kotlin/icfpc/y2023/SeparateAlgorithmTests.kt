package icfpc.y2023

import icfpc.y2023.model.Pillar
import icfpc.y2023.model.Point
import icfpc.y2023.model.Task
import icfpc.y2023.model.Solve
import icfpc.y2023.service.CalcScoringService
import icfpc.y2023.utils.readUrl
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.params.provider.MethodSource
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import utils.getCells
import icfpc.y2023.utils.send
import kotlin.math.*

class SeparateAlgorithmTests {
    val scoring = CalcScoringService()
    companion object {
        @JvmStatic
        fun ids(): List<Int> {
            return (1..90).toList()
        }
    }

    @ParameterizedTest
    @MethodSource("ids")
    fun test2(id: Int) {
        val domain = "http://localhost:8080"
        val problem = Task.parse(readUrl("$domain/problem/$id"))
        val solution = getPlaceFor(problem)
        var score = scoring.calc(problem, solution)
        URL("$domain/add/$id?calc=true").send(solution)
        println("score for $id: $score")
    }


    fun getPlaceFor(problem: Task): Solve {
        val cells = getCells(problem).toMutableSet()
        println("cellssize ${cells.size}")
        println("cells ${cells}")
        val mPoints = mutableListOf<Point>()
        for (m in problem.musicians) {
            val cell = cells.maxBy { mPos ->
                problem.attendees.map({ scoring.calcSimpleInfluence(m, mPos, it)}).sum()
            }
            println("found best place for $m on $cell")
            cells -= cell
            mPoints += cell
        }
        return Solve(mPoints)
    }
}
