package icfpc.y2023.utils

import com.fasterxml.jackson.databind.JsonNode

import com.fasterxml.jackson.databind.ObjectMapper

fun minimizeJson(json: String): String {
    val objectMapper = ObjectMapper()
    val jsonNode = objectMapper.readValue(json, JsonNode::class.java)
    return jsonNode.toString()
}
