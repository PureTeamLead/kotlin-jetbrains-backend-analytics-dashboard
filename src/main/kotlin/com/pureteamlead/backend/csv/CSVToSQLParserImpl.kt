package com.pureteamlead.backend.csv

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.pureteamlead.backend.database.DatabaseConn
import com.pureteamlead.backend.errors.DatabaseException
import java.sql.SQLException
import java.sql.SQLTimeoutException

class CSVToSQLParserImpl(private val dbName: String, private val tableName: String) : CSVToSQLParser {

    @Throws(DatabaseException::class)
    override fun convert(filepath: String, db: DatabaseConn) {
        csvReader().open(filepath) {
            val rawHeaders: List<String>? = readAllAsSequence().firstOrNull()
            val dbColumnsCount = rawHeaders!!.size

            if (dbColumnsCount == 0) {
                println("Zero columns")
                return@open
            }

            val createTableQuery = buildCreateTableQuery(rawHeaders)
            val insertQuery = buildInsertQuery(dbColumnsCount)

            // Working with Database
            try {
                db.getConnection(dbName).use {
                    // creating table in DB
                    val stmt = it.prepareStatement(it.nativeSQL(createTableQuery))
                    stmt.execute()

                    // reusable SQL statement
                    val preparedInsertStmt = it.prepareStatement(it.nativeSQL(insertQuery))

                    // inserting data from CSV file
                    readAllAsSequence().forEach { row ->
                        row.forEachIndexed { index, e ->
                            if (e.isBlank()) {
                                preparedInsertStmt.setNull(index+1, java.sql.Types.VARCHAR)
                            }

                            preparedInsertStmt.setObject(index+1, e)
                        }

                        println(preparedInsertStmt)
                        val rowModified = preparedInsertStmt.executeUpdate()
                        if (rowModified != 1) {
                            println("Modified wrong number of rows: $rowModified")
                        }
                        preparedInsertStmt.clearParameters()
                    }

                    it.commit()
                }
            } catch(e: Exception) {
                when (e) {
                    is SQLTimeoutException, is SQLException -> {
                        throw DatabaseException("Failed to work with database: ${e.printStackTrace()}")
                    }
                    else -> throw e
                }
            }
        }
    }

    private fun buildCreateTableQuery(rawHeaders: List<String>): String {
        // building create table SQL query
        val strBuilder = StringBuilder("""CREATE TABLE IF NOT EXISTS $tableName(
            """.trimIndent())

        rawHeaders.forEach {
            if (it.contains("id")) {
                strBuilder.append("""$it BIGINT PRIMARY KEY""")
            }else {
                strBuilder.append("""$it TEXT""")
            }

            if (it != rawHeaders.last()) {
                strBuilder.append(",\n")
            }else {
                strBuilder.append("\n")
            }
        }
        strBuilder.append(");")
        return strBuilder.toString()
    }

    private fun buildInsertQuery(columnsCount: Int): String {
        // building insert SQL query
        val insertStmtBuilder = StringBuilder("INSERT INTO $tableName VALUES(")
        for (i in 0..<columnsCount) {
            insertStmtBuilder.append("?")

            if (i != columnsCount -1) {
                insertStmtBuilder.append(", ")
            }
        }
        insertStmtBuilder.append(");")

        return insertStmtBuilder.toString()
    }
}