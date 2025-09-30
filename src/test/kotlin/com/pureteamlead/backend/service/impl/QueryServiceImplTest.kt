import com.pureteamlead.backend.entity.Query
import com.pureteamlead.backend.errors.DatabaseException
import com.pureteamlead.backend.repository.DataRepository
import com.pureteamlead.backend.repository.QueryRepository
import com.pureteamlead.backend.service.impl.QueryServiceImpl
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.SQLException
import java.sql.SQLTimeoutException
import java.util.*

class QueryServiceImplTest {
 @Test
 fun `create should validate and save query, returning id`() {
  val query = mockk<Query>(relaxed = true)
  val repo = mockk<QueryRepository>()
  val dataRepo = mockk<DataRepository>()
  val saved = Query(id = 42L, query = "SELECT * FROM users")
  every { query.validate() } just Runs
  every { repo.save(query) } returns saved

  val service = QueryServiceImpl(repo, dataRepo)
  val result = service.create(query)
  assertEquals(42L, result)
  verify { query.validate() }
  verify { repo.save(query) }
 }

 @Test
 fun `getAll should return all queries`() {
  val repo = mockk<QueryRepository>()
  val dataRepo = mockk<DataRepository>()
  val queries = listOf(
   Query(id = 1L, query = "SELECT * FROM a"),
   Query(id = 2L, query = "SELECT * FROM b")
  )
  every { repo.findAll() } returns queries

  val service = QueryServiceImpl(repo, dataRepo)
  val result = service.getAll()
  assertEquals(queries, result)
 }

 @Test
 fun `execute should return result from dataRepo for valid id`() {
  val repo = mockk<QueryRepository>()
  val dataRepo = mockk<DataRepository>()
  val query = Query(id = 1L, query = "SELECT * FROM a")
  val resultList = listOf(listOf("foo", "bar"))
  every { repo.findById(1L) } returns Optional.of(query)
  every { dataRepo.executeQuery(query.query) } returns resultList

  val service = QueryServiceImpl(repo, dataRepo)
  val result = service.execute(1L)
  assertEquals(resultList, result)
 }

 @Test
 fun `execute should throw NoSuchElementException if id not found`() {
  val repo = mockk<QueryRepository>()
  val dataRepo = mockk<DataRepository>()
  every { repo.findById(42L) } returns Optional.empty()

  val service = QueryServiceImpl(repo, dataRepo)
  assertThrows(NoSuchElementException::class.java) {
   service.execute(42L)
  }
 }

 @Test
 fun `execute should throw DatabaseException on SQLTimeoutException`() {
  val repo = mockk<QueryRepository>()
  val dataRepo = mockk<DataRepository>()
  val query = Query(id = 1L, query = "SELECT * FROM a")
  every { repo.findById(1L) } returns Optional.of(query)
  every { dataRepo.executeQuery(any()) } throws SQLTimeoutException("timeout")

  val service = QueryServiceImpl(repo, dataRepo)
  val ex = assertThrows(DatabaseException::class.java) {
   service.execute(1L)
  }
  assertTrue(ex.message.contains("failed to work with database"))
 }

 @Test
 fun `execute should throw DatabaseException on SQLException`() {
  val repo = mockk<QueryRepository>()
  val dataRepo = mockk<DataRepository>()
  val query = Query(id = 1L, query = "SELECT * FROM a")
  every { repo.findById(1L) } returns Optional.of(query)
  every { dataRepo.executeQuery(any()) } throws SQLException()

  val service = QueryServiceImpl(repo, dataRepo)
  val ex = assertThrows(DatabaseException::class.java) {
   service.execute(1L)
  }
  assertTrue(ex.message.contains("failed to work with database"))
 }

 @Test
 fun `execute should rethrow other exceptions`() {
  val repo = mockk<QueryRepository>()
  val dataRepo = mockk<DataRepository>()
  val query = Query(id = 1L, query = "SELECT * FROM a")
  every { repo.findById(1L) } returns Optional.of(query)
  every { dataRepo.executeQuery(any()) } throws IllegalArgumentException("bad")

  val service = QueryServiceImpl(repo, dataRepo)
  assertThrows(IllegalArgumentException::class.java) {
   service.execute(1L)
  }
 }
}