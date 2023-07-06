package icfpc.y2023.db.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "solutions")
data class Solution(
    @Id
    @GeneratedValue
    @Column
    val id: Int? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonIgnore
    val problem: Problem,
    @Column
    val contents: String,
    @Column
    var score: Long? = null
) {
    @get:Transient
    val problemId get() = problem.id
}
