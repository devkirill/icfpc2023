package icfpc.y2023.service

import icfpc.y2023.db.model.Solution
import icfpc.y2023.model.Attendees
import icfpc.y2023.model.Point
import org.springframework.stereotype.Service
import kotlin.math.ceil
import kotlin.math.sqrt

@Service
class CalcScoringService {
    companion object {
        val EPS = 1e-9
        val R = 5.0
    }

    fun calc(solution: Solution): Long {
        val problem = solution.problem.problem
        val solve = solution.contents

        val lines = problem.attendees.map { att ->
            val l = problem.musicians.mapIndexed { ind, instr ->
                val mPos = solve.placements[ind]
                val attPoint = Point(att.x, att.y)
                val d = (attPoint - mPos).sqrSize()
                if (d < EPS || intersect(
                        solve.placements.filterIndexed { i, _ -> i != ind },
                        mPos,
                        attPoint
                    )
                ) {
                    0L
                } else {
                    ceil(1_000_000.0 * att.tastes[instr] / d).toLong()
                }
            }
            l.sum() to l
        }

//        println(lines.joinToString("\n") { it.second.joinToString("\t") })

        return lines.sumOf { it.first }
    }

    fun intersect(musicians: List<Point>, a: Point, b: Point): Boolean {
        return musicians.any { intersect(a, b, it) }
    }

    fun intersect(p1: Point, p2: Point, m: Point): Boolean {
        val n = p2 - p1
        val mp = p1 + n * (n scalar (m - p1)) / n.sqrSize()
        if ((m dist mp) > R - EPS) return false
        if ((p1 dist mp) + (p2 dist mp) <= (p1 dist p2) + EPS) return true
        if (p1 dist m < R + EPS || p2 dist m < R + EPS) return true
        return false
    }

    fun dist(a: Point, b: Point): Double {
        return sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
    }

    fun dist(a: Attendees, b: Point): Double {
        return sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
    }
}

// https://ru.wikipedia.org/wiki/%D0%A0%D0%B0%D1%81%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D0%B5_%D0%BE%D1%82_%D1%82%D0%BE%D1%87%D0%BA%D0%B8_%D0%B4%D0%BE_%D0%BF%D1%80%D1%8F%D0%BC%D0%BE%D0%B9_%D0%BD%D0%B0_%D0%BF%D0%BB%D0%BE%D1%81%D0%BA%D0%BE%D1%81%D1%82%D0%B8
