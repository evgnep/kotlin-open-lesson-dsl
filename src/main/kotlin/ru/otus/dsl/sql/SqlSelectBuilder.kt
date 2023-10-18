package ru.otus.dsl.sql

class SqlSelectBuilder {
    private var table: String? = null
    private var columns = mutableListOf<String>()
    private var where: SqlExpression? = null

    fun build(): String {
        requireNotNull(table) { "table must be set" }
        val cols = if (columns.isEmpty()) "*" else columns.joinToString(", ")
        val wherePart = if (where == null) "" else " where " + where!!.build()
        return "select $cols from $table$wherePart"
    }

    fun from(table: String) {
        this.table = table;
    }

    fun select(vararg columns: String) {
        this.columns.addAll(columns)
    }

    fun where(expr: SqlExpression) {
        this.where = expr
    }
}

fun query(block: SqlSelectBuilder.() -> Unit) = SqlSelectBuilder().apply(block)