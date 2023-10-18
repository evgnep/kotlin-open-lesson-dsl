package ru.otus.dsl.sql

interface SqlExpression {
    fun build() : String
}

data class SqlCol(val name: String): SqlExpression {
    override fun build(): String  = name
}

data class SqlString(val value: String): SqlExpression {
    override fun build(): String  = "'$value'"
}

data class SqlNumber(val value: Number): SqlExpression {
    override fun build(): String  = value.toString()
}
