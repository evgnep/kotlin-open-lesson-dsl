package ru.otus.dsl.json

@DslMarker
annotation class JsonMarker

@JsonMarker
sealed class JsonValueBuilder {
    internal var setter: (JsonValue?) -> Unit = {}

    fun number(value: Number): JsonNumber = JsonNumber(value).also(setter)

    fun bool(value: Boolean) = JsonBoolean(value).also(setter)

    fun string(value: String) = JsonString(value).also(setter)


    fun nil() = setter(null).let { null }


    fun array(block: JsonArrayBuilder.() -> Unit) = JsonArrayBuilder().apply(block).build().also(setter)

    fun obj(block: JsonObjectBuilder.() -> Unit) = JsonObjectBuilder().apply(block).build().also(setter)
}

class JsonBuilder : JsonValueBuilder() {
    private var value: JsonValue? = null

    init {
        setter = { value  = it }
    }


    internal fun build() = Json(value)
}

class JsonArrayBuilder : JsonValueBuilder() {
    private val values = mutableListOf<JsonValue?>()

    init {
        setter = { values.add(it) }
    }

    internal fun build() = JsonArray(values)
}

class JsonObjectBuilder : JsonValueBuilder() {
    private val values = mutableMapOf<String, JsonValue?>()

    infix fun String.to(value: JsonValue?) {
        values[this] = value
    }

    infix fun String.to(value: Number) {
        values[this] = JsonNumber(value)
    }

    infix fun String.to(value: Boolean) {
        values[this] = JsonBoolean(value)
    }

    infix fun String.to(value: String) {
        values[this] = JsonString(value)
    }

    internal fun build() = JsonObject(values)
}


fun json(block: JsonValueBuilder.() -> Unit): Json =
    JsonBuilder().apply(block).build()
