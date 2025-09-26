package com.pureteamlead.backend.csv

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.pureteamlead.backend.database.DatabaseConn

class CSVToSQLParserImpl() : CSVToSQLParser {
    private val dbName = "project_db"

    override fun convert(filepath: String, db: DatabaseConn) {
        csvReader().open(filepath) {
            val data = readAllAsSequence()

            val rawHeaders: List<String>? = data.firstOrNull()
            val dbColumnsCount = rawHeaders!!.size

            if (dbColumnsCount == 0) {
                return@open
            }

            val strBuilder = StringBuilder("""
                CREATE TABLE IF NOT EXISTS entities(
            """.trimIndent())

            rawHeaders.forEach {
                if (it.contains("id")) {
                    strBuilder.append("$it BIGINT PRIMARY KEY")
                }else {
                    strBuilder.append("$it TEXT")
                }

                if (it != rawHeaders.last()) {
                    strBuilder.append(",\n")
                }else {
                    strBuilder.append("\n")
                }
            }
            strBuilder.append(");")

            // table creation
            val conn = db.getConnection(dbName)
            val stmt = conn.prepareStatement(conn.nativeSQL(strBuilder.toString()))
            stmt.execute()

            // preparing insert SQL query
            val insertStmtBuilder = StringBuilder("INSERT INTO $dbName VALUES(")

            for (i in 0..dbColumnsCount) {
                insertStmtBuilder.append("$$i")

                if (i != rawHeaders.size -1) {
                    insertStmtBuilder.append(",")
                }
            }
            insertStmtBuilder.append(");")
            val preparedInsertStmt = conn.prepareStatement(conn.nativeSQL(insertStmtBuilder.toString()))

            readAllAsSequence().forEach { row ->
                for (e in row) {
                    print("$e ")
                }

                println()
            }

            conn.commit()
        }
    }

    private fun createTableStmt(): String? {
        TODO()
    }

    private fun insertData() {
        TODO()
    }
}