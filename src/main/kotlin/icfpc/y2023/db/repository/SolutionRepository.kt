package icfpc.y2023.db.repository

import icfpc.y2023.db.model.Problem
import icfpc.y2023.db.model.Solution
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SolutionRepository : JpaRepository<Solution, Int> {
    fun findFirstByProblemAndScoreIsNotNullOrderByScoreDescIdAsc(problem: Problem): Solution?
    fun findAllByScoreIsNull(): List<Solution>
    fun findAllByProblemAndScoreIsNotNullOrderByScoreDescIdAsc(problem: Problem, pageable: Pageable): List<Solution>

    @Query("select id from Solution where score is null")
    fun findNotCalculated(): List<Int>
}

fun SolutionRepository.findBest(problem: Problem) = findFirstByProblemAndScoreIsNotNullOrderByScoreDescIdAsc(problem)

fun SolutionRepository.findBest(problem: Problem, limit: Int) =
    findAllByProblemAndScoreIsNotNullOrderByScoreDescIdAsc(problem, Pageable.ofSize(limit))
