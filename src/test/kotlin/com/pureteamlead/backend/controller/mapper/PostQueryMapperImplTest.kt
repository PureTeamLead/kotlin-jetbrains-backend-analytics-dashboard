package com.pureteamlead.backend.controller.mapper

import com.pureteamlead.backend.controller.model.PostQueryRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PostQueryMapperImplTest {

 private val mapper = PostQueryMapperImpl()

 @Test
 fun `fromPostReqToQuery should map request to Query entity`() {
  val req = PostQueryRequest(query = "SELECT * FROM users")
  val result = mapper.fromPostReqToQuery(req)
  assertEquals(null, result.id)
  assertEquals(req.query, result.query)
 }
}