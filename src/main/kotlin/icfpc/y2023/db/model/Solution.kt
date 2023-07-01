package icfpc.y2023.db.model

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
    val problem: Problem,
    @Column
    val solution: String,
    @Column
    var score: Long? = null
)
