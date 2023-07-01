package icfpc.y2023.db.repository

import icfpc.y2023.db.model.Problem
import icfpc.y2023.db.model.Solution
import org.springframework.data.jpa.repository.JpaRepository

interface SolutionRepository : JpaRepository<Solution, Long> {
    fun findFirstByProblemAndScoreIsNotNullOrderByScoreAscIdAsc(problem: Problem): Solution
    fun findAllByScoreIsNull(): List<Solution>
}

fun SolutionRepository.findBest(problem: Problem) = findFirstByProblemAndScoreIsNotNullOrderByScoreAscIdAsc(problem)