package icfpc.y2023.db.repository

import icfpc.y2023.db.model.Problem
import org.springframework.data.jpa.repository.JpaRepository

interface ProblemRepository : JpaRepository<Problem, Long>
