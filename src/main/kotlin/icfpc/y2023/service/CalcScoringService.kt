package icfpc.y2023.service

import icfpc.y2023.model.*
import org.springframework.stereotype.Service
import kotlin.math.ceil
import kotlin.math.sqrt

@Service
class CalcScoringService {
    companion object {
        val EPS = 1e-9
        val R = 5.0
    }


    fun calcSimpleInfluence(instr: Int, mPos: Point, att: Attendee): Long {
        val attPoint = Point(att.x, att.y)
        val d = (attPoint - mPos).sqrSize()
        return if (d < EPS) {
            0L
        } else {
            ceil(1_000_000.0 * att.tastes[instr] / d).toLong()
        }
    }

    fun calcInfluencesWithoutVolume(problem: Task, placement: List<Point>): List<Long> {
        val influences = problem.musicians.map { 0L }.toMutableList()
        for (att in problem.attendees) { 
            val attPoint = Point(att.x, att.y)
            val pillars = problem.pillars.map { Pillar(it.center[0], it.center[1], it.radius) }.toMutableList()
            val musicianInfluencesForAtt = placement
                .mapIndexed { ind, mPos -> Triple(ind, problem.musicians[ind], mPos) }
                .sortedBy { it.third dist attPoint }
                .map { (ind, instr, mPos) ->
                    val d = (attPoint - mPos).sqrSize()
                    if (d < EPS || intersect(pillars, mPos, attPoint)) {
                        Pair(ind, 0L)
                    } else {
                        pillars += Pillar(mPos.x, mPos.y, R)
                        Pair(ind, ceil(1_000_000.0 * att.tastes[instr] / d).toLong())
                    }
                }
                .toList()
            for ((musInd, influence) in musicianInfluencesForAtt) {
                influences[musInd] += influence
            }
        }
        return influences;

    }

    fun calcWithPillars(problem: Task, instr: Int, mPos: Point, pillars: List<Pillar>): Long {
        var result = 0L
        for (att in problem.attendees) {
            val attPoint = Point(att.x, att.y)
            val d = (attPoint - mPos).sqrSize()
            if (d > EPS && !intersect(pillars, mPos, attPoint)) {
                result += ceil(1_000_000.0 * att.tastes[instr] / d).toLong()
            }
        }
        return result
    }

    fun calc(problem: Task, solve: Solve): Long {
        val list = solve.placements.mapIndexed { i, p -> i to p }
        if (list.any { (ia, a) -> list.any { (ib, b) -> ia != ib && a dist b < 2 * R } }) {
            println("intersect")
            return Long.MIN_VALUE
        }
        if (problem.stage_bottom_left[0] + 10.0 > solve.placements.minOfOrNull { it.x }!! ||
            problem.stage_bottom_left[1] + 10.0 > solve.placements.minOfOrNull { it.y }!! ||
            problem.stage_bottom_left[0] + problem.stage_width - 10.0 < solve.placements.maxOfOrNull { it.x }!! ||
            problem.stage_bottom_left[1] + problem.stage_height - 10.0 < solve.placements.maxOfOrNull { it.y }!!
        ) {
            println("stage")
            return Long.MIN_VALUE
        }

        val influences = calcInfluencesWithoutVolume(problem, solve.placements)

        return influences
            .zip(solve.volumes ?: influences.map { 1.0 })
            .map { (inf, vol) -> ceil(inf * vol).toLong() }
            .sum()
    }

    fun intersect(musicians: List<Pillar>, a: Point, b: Point): Boolean {
        return musicians.any { intersect(a, b, it) }
    }

    fun intersect(p1: Point, p2: Point, p: Pillar): Boolean {
        val m = Point(p.x, p.y)
        val r = p.r
        val n = p2 - p1
        val mp = p1 + n * (n scalar (m - p1)) / n.sqrSize()
        if ((m dist mp) > r - EPS) return false
        if ((p1 dist mp) + (p2 dist mp) < (p1 dist p2) + EPS) return true
        if (p1 dist m < r + EPS || p2 dist m < r + EPS) return true
        return false
    }

    fun dist(a: Point, b: Point): Double {
        return sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
    }

    fun dist(a: Attendee, b: Point): Double {
        return sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
    }
}

// https://ru.wikipedia.org/wiki/%D0%A0%D0%B0%D1%81%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D0%B5_%D0%BE%D1%82_%D1%82%D0%BE%D1%87%D0%BA%D0%B8_%D0%B4%D0%BE_%D0%BF%D1%80%D1%8F%D0%BC%D0%BE%D0%B9_%D0%BD%D0%B0_%D0%BF%D0%BB%D0%BE%D1%81%D0%BA%D0%BE%D1%81%D1%82%D0%B8
