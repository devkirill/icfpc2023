package icfpc.y2023.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

data class Task(
    @JsonProperty("room_width")
    val room_width: Double,
    @JsonProperty("room_height")
    val room_height: Double,
    @JsonProperty("stage_width")
    val stage_width: Double,
    @JsonProperty("stage_height")
    val stage_height: Double,
    @JsonProperty("stage_bottom_left")
    val stage_bottom_left: List<Double>,
    @JsonProperty("musicians")
    val musicians: List<Int>,
    @JsonProperty("attendees")
    val attendees: List<Attendees>,
    @JsonProperty("pillars")
    val pillars: List<Pillars>
) {
    companion object {
        fun parse(json: String): Task {
            return ObjectMapper().readValue(json, Task::class.java)
        }
    }
}

data class Attendees(
    @JsonProperty("x")
    val x: Double,
    @JsonProperty("y")
    val y: Double,
    @JsonProperty("tastes")
    val tastes: List<Double>
)

data class Pillars(
    @JsonProperty("center")
    val center: List<Double>,
    @JsonProperty("radius")
    val radius: Double
)
