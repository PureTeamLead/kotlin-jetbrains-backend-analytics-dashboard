package com.pureteamlead.backend.controller

import com.pureteamlead.backend.controller.model.PostQueryRequest
import com.pureteamlead.backend.entity.Query
import com.pureteamlead.backend.controller.mapper.PostQueryMapper
import com.pureteamlead.backend.service.QueryService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pureteamlead.backend.errors.InvalidQueryException

class QueryControllerTest {

 private val queryService = mockk<QueryService>()
 private val queryMapper = mockk<PostQueryMapper>()
 private val controller = QueryController(queryService, queryMapper)
 private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()
 private val objectMapper = jacksonObjectMapper()

 @Test
 fun `getQuery should return 200 OK and list of queries`() {
  val queries = listOf(Query(1L, "SELECT * FROM users"))
  every { queryService.getAll() } returns queries

  mockMvc.perform(get("/queries"))
   .andExpect(status().isOk)
   .andExpect(jsonPath("$[0].id").value(1L))
   .andExpect(jsonPath("$[0].query").value("SELECT * FROM users"))
 }

 @Test
 fun `getQuery should return 500 on exception`() {
  every { queryService.getAll() } throws RuntimeException("fail")

  mockMvc.perform(get("/queries"))
   .andExpect(status().isInternalServerError)
 }

 @Test
 fun `addQuery should create query and return 201 Created`() {
  val req = PostQueryRequest("SELECT * FROM users")
  val query = Query(null, req.query)
  every { queryMapper.fromPostReqToQuery(req) } returns query
  every { queryService.create(query) } returns 42L

  mockMvc.perform(
   post("/queries")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(req))
  )
   .andExpect(status().isCreated)
   .andExpect(jsonPath("$.id").value(42L))
 }

 @Test
 fun `addQuery should return 400 on InvalidQueryException`() {
  val req = PostQueryRequest("bad")
  val query = Query(null, req.query)
  every { queryMapper.fromPostReqToQuery(req) } returns query
  every { queryService.create(query) } throws InvalidQueryException("bad query")

  mockMvc.perform(
   post("/queries")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(req))
  )
   .andExpect(status().isBadRequest)
 }

 @Test
 fun `addQuery should return 500 on generic exception`() {
  val req = PostQueryRequest("SELECT * FROM users")
  val query = Query(null, req.query)
  every { queryMapper.fromPostReqToQuery(req) } returns query
  every { queryService.create(query) } throws RuntimeException("fail")

  mockMvc.perform(
   post("/queries")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(req))
  )
   .andExpect(status().isInternalServerError)
 }

 @Test
 fun `executeQuery should return 200 OK and result`() {
  val result = listOf(listOf("foo", "bar"))
  every { queryService.execute(1L) } returns result

  mockMvc.perform(get("/execute").param("query", "1"))
   .andExpect(status().isOk)
   .andExpect(jsonPath("$[0][0]").value("foo"))
   .andExpect(jsonPath("$[0][1]").value("bar"))
 }

 @Test
 fun `executeQuery should return 404 on NoSuchElementException`() {
  every { queryService.execute(1L) } throws NoSuchElementException("not found")

  mockMvc.perform(get("/execute").param("query", "1"))
   .andExpect(status().isNotFound)
 }

 @Test
 fun `executeQuery should return 500 on generic exception`() {
  every { queryService.execute(1L) } throws RuntimeException("fail")

  mockMvc.perform(get("/execute").param("query", "1"))
   .andExpect(status().isInternalServerError)
 }
}