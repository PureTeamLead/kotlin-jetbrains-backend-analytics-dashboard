package com.pureteamlead.backend.service.impl

import com.pureteamlead.backend.entity.Query
import com.pureteamlead.backend.errors.DatabaseException
import com.pureteamlead.backend.errors.InvalidQueryException
import com.pureteamlead.backend.repository.DataRepository
import com.pureteamlead.backend.repository.QueryRepository
import com.pureteamlead.backend.service.QueryService
import org.springframework.stereotype.Service
import java.lang.Exception
import java.sql.SQLException
import java.sql.SQLTimeoutException
import java.util.NoSuchElementException
import kotlin.jvm.Throws

@Service
class QueryServiceImpl(val queryRepo: QueryRepository, val dataRepo: DataRepository) : QueryService {

    @Throws(InvalidQueryException::class)
    override fun create(query: Query): Long {
        query.validate()

        val saved = queryRepo.save(query)
        return saved.id!!
    }

    override fun getAll(): List<Query> {
        return queryRepo.findAll()
    }

    @Throws(NoSuchElementException::class)
    override fun execute(id: Long): Any {
        val query = queryRepo.findById(id).orElseThrow()
        val result: Any

        try {
            result = dataRepo.executeQuery(query.query)
        } catch (e: Exception) {
            when (e) {
                is SQLTimeoutException, is SQLException -> {
                    throw DatabaseException("Executing query: failed to work with database: ${e.printStackTrace()}")
                }
                else -> throw e
            }
        }

        return result
    }
}