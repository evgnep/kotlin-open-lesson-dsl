package ru.otus.dsl.sql

data class SqlOr(val expressions: List<SqlExpression>): SqlExpression {
    override fun build(): String = expressions.joinToString(" or ", prefix = "(", postfix = ")") { it.build() }
}

fun or(vararg expressions: SqlExpression) = SqlOr(expressions.asList())

infix fun SqlExpression.or(right: SqlExpression) = SqlOr(listOf(this, right))