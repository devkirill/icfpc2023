package icfpc.y2023.db.repository

import icfpc.y2023.db.model.ProblemContent
import org.springframework.data.jpa.repository.JpaRepository

interface ProblemContentRepository : JpaRepository<ProblemContent, Int>
