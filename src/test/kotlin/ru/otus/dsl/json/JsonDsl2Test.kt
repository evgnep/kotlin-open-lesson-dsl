package ru.otus.dsl.json

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@JsonMarker
class JsonObjectBuilder2 {
    private val values = mutableMapOf<String, JsonValue>()

    infix fun String.to(value: Number) {
        values[this] = JsonNumber(value)
    }

    infix fun String.to(value: Boolean) {
        values[this] = JsonBoolean(value)
    }

    infix fun String.to(value: Nothing) {
        values[this] = JsonNull
    }

    infix fun String.to(value: String) {
        values[this] = JsonString(value)
    }

    infix fun String.to(value: List<*>) {
        values[this] = jsonValue(value)
    }

    infix fun String.to(block: JsonObjectBuilder2.() -> Unit) {
        values[this] = JsonObjectBuilder2().apply(block).build()
    }

    fun build(): JsonValue = JsonObject(values)
}

internal fun jsonValue(value: Any?): JsonValue = when (value) {
    null -> JsonNull
    is Boolean -> JsonBoolean(value)
    is Number -> JsonNumber(value)
    is String -> JsonString(value)
    is List<*> -> JsonArray(value.map(::jsonValue))
    is Map<*, *> -> JsonObject(value.entries.associate { it.key.toString() to jsonValue(it.value) })
    else -> throw IllegalArgumentException("Unknown value type: $value")
}

fun json2(bool: Boolean): Json = Json(JsonBoolean(bool))

fun json2(number: Number): Json = Json(JsonNumber(number))

fun json2(string: String): Json = Json(JsonString(string))

fun json2(list: List<*>): Json = Json(jsonValue(list))

fun json2(vararg args: Any?): Json = Json(jsonValue(args.toList()))

fun json2(nil: Nothing?): Json = Json(JsonNull)

fun json2(block: JsonObjectBuilder2.() -> Unit): Json = Json(JsonObjectBuilder2().apply(block).build())


class JsonDsl2Test {
    @Test
    fun simple() {
        val j = json2(10)

        assertThat(j.value).isInstanceOfSatisfying(JsonNumber::class.java) {
            assertThat(it.value).isEqualTo(10)
        }
    }

    @Test
    fun array() {
        val j = json2(10, null, "hello")

        assertThat(j.value).isInstanceOfSatisfying(JsonArray::class.java) {
            assertThat(it.value).containsExactly(JsonNumber(10), JsonNull, JsonString("hello"))
        }
    }

    @Test
    fun hash() {
        val j = json2 {
            "a" to 10
            "b" to listOf(10, 20)
            "c" to {
                "x" to 1
            }
        }

        assertThat(j.value).isInstanceOfSatisfying(JsonObject::class.java) {
            assertThat(it.value)
                .containsEntry("a", JsonNumber(10))
                .containsEntry("b", JsonArray(listOf(JsonNumber(10), JsonNumber(20))))
                .containsEntry("c", JsonObject(mapOf("x" to JsonNumber(1))))
        }
    }
}
