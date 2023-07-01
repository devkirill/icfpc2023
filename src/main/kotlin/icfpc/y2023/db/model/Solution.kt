package icfpc.y2023.db.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "solutions")
data class Solution(
    @Id
    @GeneratedValue
    @Column
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonIgnore
    val problem: Problem,
    @Column
    val solution: String,
    @Column
    var score: Long? = null
) {
    @get:Transient
    val problemId get() = problem.id
}
