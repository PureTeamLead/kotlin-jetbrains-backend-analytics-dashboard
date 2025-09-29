package com.pureteamlead.backend.repository.impl

import com.pureteamlead.backend.csv.CSVToSQLParser
import com.pureteamlead.backend.csv.CSVToSQLParserImpl
import com.pureteamlead.backend.database.DatabaseConn
import com.pureteamlead.backend.errors.DatabaseException
import com.pureteamlead.backend.repository.DataRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.sql.SQLException
import java.sql.SQLTimeoutException
import kotlin.system.exitProcess

@Repository
class DataRepositoryImpl(
    private val db: DatabaseConn,
    @Value("\${db.name}") private val dbName: String,
    @Value("\${db.table.name}") private val tableName: String,
    @Value("\${csv.filename}") private val filename: String
) : DataRepository {

    @PostConstruct
    fun setup() {
        println("setup started")
        val parser: CSVToSQLParser = CSVToSQLParserImpl(dbName, tableName)
        try {
            parser.convert(filename, db)
        } catch (e: DatabaseException) {
            println("Failed csv file converting: ${e.message}")
            exitProcess(1)
        }

        println("Table in DB is created")
    }

    @Throws(SQLException::class, SQLTimeoutException::class)
    override fun executeQuery(query: String): List<List<String>> {
        val conn = db.getConnection(dbName)
        val metaData = conn.metaData

        val stmt = conn.prepareStatement(conn.nativeSQL(query))
        val tableName = stmt.metaData.getTableName(1)

        val rs = stmt.executeQuery()
        conn.commit()

        val result: MutableList<List<String>> = mutableListOf()

        while (rs.next()) {
            // Non-production ready, performance is really poor here. Should somehow reuse columns variable, not recreate
            val columns = metaData.getColumns(null, null, tableName, null)
            val row: MutableList<String> = mutableListOf()
            var i = 1
            while (columns.next()) {
                row.addLast(rs.getString(i))
                i++
            }
            result.addLast(row)
        }

        return result
    }
}