package icfpc.y2023.db.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "problems")
data class Problem(
    @Id
    @Column
    val id: Long
)
