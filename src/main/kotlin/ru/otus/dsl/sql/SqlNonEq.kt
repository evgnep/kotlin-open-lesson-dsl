package ru.otus.dsl.sql

data class SqlNonEq(val left: SqlExpression, val right: SqlExpression): SqlExpression {
    override fun build(): String = left.build() + " != " + right.build()
}

data class SqlNonNull(val left: SqlExpression): SqlExpression {
    override fun build(): String = left.build() + " !is null"
}

infix fun String.nonEq(arg: String) = SqlNonEq(SqlCol(this), SqlString(arg))

infix fun String.nonEq(arg: Number) = SqlNonEq(SqlCol(this), SqlNumber(arg))

infix fun String.nonEq(arg: Nothing?) = SqlNonNull(SqlCol(this))
