package com.pureteamlead.backend.repository.impl

import com.pureteamlead.backend.csv.CSVToSQLParserImpl
import com.pureteamlead.backend.database.DatabaseConn
import com.pureteamlead.backend.errors.DatabaseException
import io.mockk.*
import kotlin.system.exitProcess
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.PreparedStatement
import java.sql.ResultSet

class DataRepositoryImplTest {

   @Test
   fun `executeQuery should return correct results from ResultSet`() {
    val db = mockk<DatabaseConn>()
    val conn = mockk<Connection>(relaxed = true)
    val stmt = mockk<PreparedStatement>(relaxed = true)
    val rs = mockk<ResultSet>(relaxed = true)
    val metaData = mockk<DatabaseMetaData>(relaxed = true)
    val columns = mockk<ResultSet>(relaxed = true)

    every { db.getConnection(any(), true) } returns conn
    every { conn.metaData } returns metaData
    every { conn.prepareStatement(any()) } returns stmt
    every { conn.nativeSQL(any()) } answers { firstArg() }
    every { stmt.metaData.getTableName(any()) } returns "mockTableName"
    every { stmt.executeQuery() } returns rs
    every { conn.commit() } just Runs

    // Simulate columns.next() - two columns
    every { metaData.getColumns(any(), any(), any(), any()) } returns columns
    every { columns.next() } returnsMany listOf(true, true, false)

    // Simulate rs.next() - one row
    every { rs.next() } returnsMany listOf(true, false)
    every { rs.getString(1) } returns "foo"
    every { rs.getString(2) } returns "bar"

    val repo = DataRepositoryImpl(db, "mockDB", "mockTableName", "test.csv")
    val result = repo.executeQuery("SELECT * FROM mockTableName")

    assertEquals(listOf(listOf("foo", "bar")), result)
   }
  }