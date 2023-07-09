package icfpc.y2023.db.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import icfpc.y2023.model.Solve
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

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
    @JdbcTypeCode(SqlTypes.JSON)
    val contents: Solve,
    @Column
    var score: Long? = null
) {
    @get:Transient
    val problemId get() = problem.id

    companion object {
        fun parse(json: String): Solution {
            return ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(json, Solution::class.java)
        }
    }
}
