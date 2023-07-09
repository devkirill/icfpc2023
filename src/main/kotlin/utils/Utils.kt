package utils

import icfpc.y2023.model.Point
import icfpc.y2023.model.Task
import icfpc.y2023.utils.readUrl

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
    val coefX =
        (problem.stage_bottom_left[0] + problem.stage_width - 20) / (cells.maxOfOrNull { it.x }!! - cells.minOfOrNull { it.x }!!)
    val coefY =
        (problem.stage_bottom_left[1] + problem.stage_height - 20) / (cells.maxOfOrNull { it.y }!! - cells.minOfOrNull { it.y }!!)
    return cells.map {
        val x = if (coefX.isNaN()) {
            it.x
        } else {
            (it.x - (problem.stage_bottom_left[0] + 10.0)) * coefX + (problem.stage_bottom_left[0] + 10.0)
        }
        val y = if (coefY.isNaN()) {
            it.y
        } else {
            (it.y - (problem.stage_bottom_left[1] + 10.0)) * coefY + (problem.stage_bottom_left[1] + 10.0)
        }
        Point(x, y)
    }
}

//fun getborder
