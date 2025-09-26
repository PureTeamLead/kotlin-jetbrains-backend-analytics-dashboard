package com.pureteamlead.backend.csv

import com.pureteamlead.backend.database.DatabaseConnection

fun main() {
    val parser = CSVToSQLParserImpl()
    parser.convert("./src/main/resources/titanic.csv", DatabaseConnection)
}