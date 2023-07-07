package icfpc.y2023.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

data class Task(
    @JsonProperty("room_width")
    val width: Double,
    @JsonProperty("room_height")
    val height: Double,
    @JsonProperty("stage_width")
    val stageWidth: Double,
    @JsonProperty("stage_height")
    val stageHeight: Double,
    @JsonProperty("stage_bottom_left")
    val stageBottomLeft: List<Double>,
    @JsonProperty("musicians")
    val musicians: List<Int>,
    @JsonProperty("attendees")
    val attendees: List<Attendees>,
    @JsonProperty("pillars")
    val pillars: List<String>?
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
