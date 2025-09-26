package com.pureteamlead.backend.controller

import com.pureteamlead.backend.controller.mapper.PostQueryMapper
import com.pureteamlead.backend.controller.model.PostQueryRequest
import com.pureteamlead.backend.entity.Query
import com.pureteamlead.backend.service.QueryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("")
class QueryController(private val queryService: QueryService) {

    @GetMapping("/queries")
    fun getQuery(): ResponseEntity<List<Query>> {
        val queries = queryService.getAll()
        return ResponseEntity.status(HttpStatus.OK).body(queries)
    }

    @PostMapping("/queries")
    fun recordQuery(@RequestBody queryRequest: PostQueryRequest, queryMapper: PostQueryMapper): ResponseEntity<Long> {
        val query = queryMapper.fromPostReqToQuery(queryRequest)
        val id = queryService.create(query)

        return ResponseEntity.status(HttpStatus.CREATED).body(id)
    }

    @GetMapping("/execute")
    fun executeQuery(@RequestParam(name = "query") queryID: Long): ResponseEntity<List<Array<Any>>> {
        val result = queryService.execute(queryID)

        return ResponseEntity.status(HttpStatus.OK).body(result)
    }
}