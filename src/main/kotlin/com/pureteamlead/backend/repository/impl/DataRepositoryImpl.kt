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
    @Value("\${csv.filepath}") private val csvFilePath: String
) : DataRepository {

    @PostConstruct
    fun setup() {
        println("setup started")
        val parser: CSVToSQLParser = CSVToSQLParserImpl(dbName, tableName)
        try {
            parser.convert(csvFilePath, db)
        } catch (e: DatabaseException) {
            println("Failed csv file converting: ${e.message}")
            exitProcess(1)
        }

        println("Table in DB is created")
    }

    @Throws(SQLException::class, SQLTimeoutException::class)
    override fun executeQuery(query: String): Any {
        val conn = db.getConnection(dbName)

        val stmt = conn.prepareStatement(conn.nativeSQL(query))

        val result = stmt.executeQuery()
        conn.commit()
        return result
    }
}