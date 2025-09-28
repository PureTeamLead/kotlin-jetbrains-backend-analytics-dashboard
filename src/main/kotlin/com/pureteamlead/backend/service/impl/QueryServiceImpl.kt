package com.pureteamlead.backend.service.impl

import com.pureteamlead.backend.entity.Query
import com.pureteamlead.backend.repository.DataRepository
import com.pureteamlead.backend.repository.QueryRepository
import com.pureteamlead.backend.service.QueryService
import org.springframework.stereotype.Service

@Service
class QueryServiceImpl(val queryRepo: QueryRepository, val dataRepo: DataRepository) : QueryService {
    override fun create(query: Query): Long {
        // TODO: validate query
        return queryRepo.save(query)
    }

    override fun getAll(): List<Query> {
        return queryRepo.findAll()
    }

    // TODO: handle NoSuchElementException
    override fun execute(id: Long): Any {
        val query = queryRepo.findById(id).orElseThrow()

        dataRepo.executeQuery(query.queryStr)
    }
}