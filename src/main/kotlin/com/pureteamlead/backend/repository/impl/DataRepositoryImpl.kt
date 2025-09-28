package com.pureteamlead.backend.repository.impl

import com.pureteamlead.backend.csv.CSVToSQLParser
import com.pureteamlead.backend.csv.CSVToSQLParserImpl
import com.pureteamlead.backend.database.DatabaseConn
import com.pureteamlead.backend.repository.DataRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

@Repository
class DataRepositoryImpl(
    private val db: DatabaseConn,
    @Value("\${db.name}") private val dbName: String,
    @Value("\${db.table.name}") private val tableName: String,
    @Value("\${csv.filepath}") private val CSVFilePath: String
) : DataRepository {

    @PostConstruct
    fun setup() {
        val parser: CSVToSQLParser = CSVToSQLParserImpl(dbName, tableName)
        parser.convert(CSVFilePath, db)
    }

    // TODO: catch SQL exceptions
    override fun executeQuery(query: String): Any {
        val conn = db.getConnection(dbName)
        val stmt = conn.prepareStatement(conn.nativeSQL(query))

        return stmt.executeQuery()
    }
}