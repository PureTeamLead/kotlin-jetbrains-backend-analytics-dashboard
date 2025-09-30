package com.pureteamlead.backend.entity

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pureteamlead.backend.errors.InvalidQueryException
 class QueryTest {

  @Test
  fun `validate should not throw for correct SELECT query`() {
   val validQuery = Query(id = null, query = "SELECT name FROM users")
   assertDoesNotThrow {
    validQuery.validate()
   }
  }

  @Test
  fun `validate should throw InvalidQueryException for incorrect query`() {
   val invalidQuery = Query(id = null, query = "INSERT INTO users (name) VALUES ('John')")
   val exception = assertThrows(InvalidQueryException::class.java) {
    invalidQuery.validate()
   }
   assertEquals("Invalid query format", exception.message)
  }

  @Test
  fun `validate should throw for missing SELECT`() {
   val invalidQuery = Query(id = null, query = "name FROM users")
   assertThrows(InvalidQueryException::class.java) {
    invalidQuery.validate()
   }
  }

  @Test
  fun `validate should throw for missing FROM`() {
   val invalidQuery = Query(id = null, query = "SELECT name users")
   assertThrows(InvalidQueryException::class.java) {
    invalidQuery.validate()
   }
  }
}