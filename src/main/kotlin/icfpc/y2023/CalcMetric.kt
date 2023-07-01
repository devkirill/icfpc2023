package icfpc.y2023

import icfpc.y2023.db.model.Solution
import org.springframework.stereotype.Service

@Service
class CalcMetric {
    fun calcMetric(solution: Solution): Long {
        return solution.solution.length.toLong()
    }
}
