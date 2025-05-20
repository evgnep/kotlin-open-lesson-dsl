package ru.otus.dsl.json

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@DslMarker
annotation class JsonMarker

fun json(block: JsonValueBuilder.() -> Unit): Json {
    val builder = JsonBuilder()
    builder.block()
    return builder.build()
}

@JsonMarker
sealed class JsonValueBuilder {
    internal var setter: (JsonValue) -> Unit = {}

    fun number(value: Number) = JsonNumber(value).also(setter)

    fun string(value: String) = JsonString(value).also(setter)

    fun bool(value: Boolean) = JsonBoolean(value).apply(setter)

    fun nil() = JsonNull.also(setter)

    fun array(block: JsonArrayBuilder.() -> Unit) =
        JsonArrayBuilder().apply(block).build().also(setter)

    fun obj(block: JsonObjectBuilder.() -> Unit) =
        JsonObjectBuilder().apply(block).build().also(setter)
}

class JsonArrayBuilder : JsonValueBuilder() {
    private val value = mutableListOf<JsonValue>()

    init {
        setter = { value.add(it) }
    }

    fun build(): JsonArray = JsonArray(value)
}

class JsonBuilder : JsonValueBuilder() {
    private var value: JsonValue = JsonNull

    init {
        setter = { value = it }
    }

    fun build(): Json = Json(value)
}

class JsonObjectBuilder : JsonValueBuilder() {
    private val values = mutableMapOf<String, JsonValue>()

    infix fun String.to(value: Number) {
        values[this] = JsonNumber(value)
    }

    infix fun String.to(value: String) {
        values[this] = JsonString(value)
    }

    infix fun String.to(value: JsonValue) {
        values[this] = value
    }

    fun build() = JsonObject(values)
}

class JsonDslTest {
    fun x() {
        val j = Json(
            JsonObject(
                mapOf(
                    "name" to JsonString("A"),
                    "age" to JsonNumber(22))
            ))
    }


        @Test
        fun simple() {
            val j = json {
                number(10)
            }

            assertThat(j.value).isInstanceOfSatisfying(JsonNumber::class.java) {
                assertThat(it.value).isEqualTo(10)
            }
        }

        @Test
        fun array() {
            val j = json {
                array {
                    number(10)
                    nil()
                    string("hello")
                }
            }

            assertThat(j.value).isInstanceOfSatisfying(JsonArray::class.java) {
                assertThat(it.value).containsExactly(JsonNumber(10), JsonNull, JsonString("hello"))
            }
        }

        @Test
        fun hash() {
            val j = json {
                obj {
                    "a" to 10
                    "b" to array {
                        number(10)
                        number(20)
                    }
                    "c" to obj {
                        "x" to 1
                    }
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
