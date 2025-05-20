package ru.otus.dsl.json

data class Json(
    val value: JsonValue
)

sealed interface JsonValue

object JsonNull: JsonValue

data class JsonNumber(val value: Number): JsonValue

data class JsonBoolean(val value: Boolean): JsonValue

data class JsonString(val value: String): JsonValue

data class JsonArray(val value: List<JsonValue>): JsonValue

data class JsonObject(val value: Map<String, JsonValue>): JsonValue
