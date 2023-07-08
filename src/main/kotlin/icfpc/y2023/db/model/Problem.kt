package icfpc.y2023.db.model

import icfpc.y2023.model.Task
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "problems")
data class Problem(
    @Id
    @Column
    val id: Int,
    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    var problem: Task,
    @Column
    var lastSendedId: Int? = null,
    @Column
    var bestScore: Long? = null,
)
