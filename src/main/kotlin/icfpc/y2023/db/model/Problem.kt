package icfpc.y2023.db.model

import icfpc.y2023.model.Task
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "problems")
data class Problem(
    @Id
    @Column
    val id: Int,
    @Column
    var problemId: Int,
    @Column
    var lastSendedId: Int? = null,
    @Column
    var bestScore: Long? = null,
)

@Entity
@Table(name = "problem_contents")
data class ProblemContent(
    @Id
    @GeneratedValue
    @Column
    val id: Int? = null,
    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val content: Task
)

