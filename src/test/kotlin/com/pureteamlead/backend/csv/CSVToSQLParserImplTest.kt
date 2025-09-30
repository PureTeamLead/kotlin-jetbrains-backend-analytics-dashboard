package com.pureteamlead.backend.csv

import com.pureteamlead.backend.database.DatabaseConn
import com.pureteamlead.backend.errors.DatabaseException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.assertThrows
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class CSVToSQLParserImplTest {

@Test
 fun `convert successful`() {
 val db = mockk<DatabaseConn>()
 val conn = mockk<Connection>(relaxed = true)
 val createStmt = mockk<PreparedStatement>(relaxed = true)
 val insertStmt = mockk<PreparedStatement>(relaxed = true)

 every { db.getConnection(any(), any()) } returns conn
 every { conn.prepareStatement(any()) } returnsMany listOf(createStmt, insertStmt)
 every { createStmt.execute() } returns true
 every { insertStmt.executeUpdate() } returns 1
 every { conn.commit() } returns Unit

 val converter = CSVToSQLParserImpl("mockDB", "mockTableName")

 converter.convert("test.csv", db)

 verify { createStmt.execute() }
 verify { insertStmt.executeUpdate() }
 verify { conn.commit() }
 }

  @Test
  fun `convert should throw DatabaseException on SQL error`() {
   val db = mockk<DatabaseConn>()
   val conn = mockk<Connection>(relaxed = true)
   val createStmt = mockk<PreparedStatement>(relaxed = true)

   every { db.getConnection(any(), any()) } returns conn
   every { conn.prepareStatement(any()) } returns createStmt
   every { createStmt.execute() } throws SQLException("DB error!")

   val converter = CSVToSQLParserImpl("mockDB", "mockTableName") // Replace with actual class name

   assertThrows<DatabaseException> {
    converter.convert("test.csv", db)
   }
  }
}