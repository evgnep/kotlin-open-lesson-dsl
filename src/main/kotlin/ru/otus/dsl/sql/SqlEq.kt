package ru.otus.dsl.sql

data class SqlEq(val left: SqlExpression, val right: SqlExpression): SqlExpression {
    override fun build(): String = left.build() + " = " + right.build()
}

infix fun String.eq(arg: String) = SqlEq(SqlCol(this), SqlString(arg))

infix fun String.eq(arg: Number) = SqlEq(SqlCol(this), SqlNumber(arg))

data class SqlEqNull(val left: SqlExpression): SqlExpression {
    override fun build(): String = left.build() + " is null"
}

infix fun String.eq(arg: Nothing?) = SqlEqNull(SqlCol(this))