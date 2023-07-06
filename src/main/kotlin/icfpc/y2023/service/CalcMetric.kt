package icfpc.y2023.service

import icfpc.y2023.db.model.Solution
import org.springframework.stereotype.Service

@Service
class CalcMetric {
    fun calcMetric(solution: Solution): Long {
        return solution.contents.length.toLong()
    }
}
