package utils

import icfpc.y2023.model.Point
import icfpc.y2023.model.Task
import icfpc.y2023.utils.readUrl
import java.util.stream.Collectors
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToLong

val domain = "http://localhost:8080"

fun getProblemsCount() = readUrl("${domain}/problems").count { it == '}' }

fun getCells(problem: Task, scale: Boolean = true): List<Point> {
    val cells = (0..(problem.stage_width.toInt() - 20) step 10).flatMap { x ->
        (0..(problem.stage_height.toInt() - 20) step 10).map { y ->
            Point(
                x.toDouble() + problem.stage_bottom_left[0] + 10.0,
                y.toDouble() + problem.stage_bottom_left[1] + 10.0
            )
        }
    }
    if (!scale) {
        return cells
    }
    val coef = getCoef(problem, cells)
    return cells.map {
        val x = if (coef.x.isNaN()) {
            it.x
        } else {
            (((it.x - (problem.stage_bottom_left[0] + 10.0)) * coef.x + (problem.stage_bottom_left[0] + 10.0)) * 1000).roundToLong() / 1000.0
        }
        val y = if (coef.y.isNaN()) {
            it.y
        } else {
            (((it.y - (problem.stage_bottom_left[1] + 10.0)) * coef.y + (problem.stage_bottom_left[1] + 10.0)) * 1000).roundToLong() / 1000.0
        }
        Point(x, y)
    }
}

fun getCoef(problem: Task, cells: List<Point>): Point {
    val coefX =
        (problem.stage_width - 20) / (cells.maxOfOrNull { it.x }!! - cells.minOfOrNull { it.x }!!)
    val coefY =
        (problem.stage_height - 20) / (cells.maxOfOrNull { it.y }!! - cells.minOfOrNull { it.y }!!)
    val x = if (coefX.isNaN()) {
        1.0
    } else {
        coefX
    }
    val y = if (coefY.isNaN()) {
        1.0
    } else {
        coefY
    }
    return Point(x, y)
}

fun List<Point>.filterBorder(problem: Task, dist: Int = 1): List<Point> {
    return filter {
        val a = min(
            abs(it.x - problem.stage_bottom_left[0]),
            abs(problem.stage_bottom_left[0] + problem.stage_width - it.x)
        )
        val b = min(
            abs(it.y - problem.stage_bottom_left[1]),
            abs(problem.stage_bottom_left[1] + problem.stage_height - it.y)
        )
        min(a, b) < (dist + 1) * 10.0
    }
}

fun <T, R> Collection<T>.pmap(mapper: (T) -> R): List<R> {
    return this.stream().parallel().map { mapper(it) }.collect(Collectors.toList())
}
