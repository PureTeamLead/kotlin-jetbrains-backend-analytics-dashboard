package com.pureteamlead.backend.controller

import com.pureteamlead.backend.controller.mapper.PostQueryMapper
import com.pureteamlead.backend.controller.model.PostQueryRequest
import com.pureteamlead.backend.controller.model.PostQueryResponse
import com.pureteamlead.backend.entity.Query
import com.pureteamlead.backend.errors.InvalidQueryException
import com.pureteamlead.backend.service.QueryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.NoSuchElementException
import mu.KotlinLogging

@RestController
@RequestMapping("")
class QueryController(private val queryService: QueryService, private val queryMapper: PostQueryMapper) {
    private val logger = KotlinLogging.logger("cmd logger")

    @GetMapping("/queries")
    fun getQuery(): ResponseEntity<List<Query>> {
        val queries: List<Query>

        try {
            queries = queryService.getAll()
        } catch (e: Exception) {
            logger.error { "failed to retrieve queries: ${e.printStackTrace()}" }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }

        logger.info { "Queries successfully returned" }
        return ResponseEntity.status(HttpStatus.OK).body(queries)
    }

    @PostMapping("/queries")
    fun addQuery(@RequestBody queryRequest: PostQueryRequest): ResponseEntity<PostQueryResponse> {
        val query = queryMapper.fromPostReqToQuery(queryRequest)
        val id: Long

        try {
            id = queryService.create(query)
        } catch (e: InvalidQueryException) {
            logger.error{ "invalid data for creating query. Query: ${query.query}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        } catch (e: Exception) {
            logger.error { "failed to add query: ${e.printStackTrace()}" }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }

        logger.info { "Query (id: ${id}) successfully added" }
        return ResponseEntity.status(HttpStatus.CREATED).body(PostQueryResponse(id))
    }

    @GetMapping("/execute")
    fun executeQuery(@RequestParam(name = "query") queryID: Long): ResponseEntity<List<List<String>>> {
//        TODO: implement pagination for large datasets
        val result: List<List<String>>

        try {
            result = queryService.execute(queryID)
        } catch (e: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        } catch (e: Exception) {
            logger.error { "failed to execute query: ${e.printStackTrace()}" }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }

        logger.info { "Query (id: ${queryID}) successfully executed" }
        return ResponseEntity.status(HttpStatus.OK).body(result)
    }
}