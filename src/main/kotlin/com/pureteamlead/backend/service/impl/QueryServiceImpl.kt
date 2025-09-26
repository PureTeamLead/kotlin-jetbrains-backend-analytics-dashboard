package com.pureteamlead.backend.service.impl

import com.pureteamlead.backend.entity.Query
import com.pureteamlead.backend.repository.QueryRepository
import com.pureteamlead.backend.service.QueryService
import org.springframework.stereotype.Service

// TODO: validation?

@Service
class QueryServiceImpl(val queryRepo: QueryRepository) : QueryService {
    override fun create(query: Query): Long {
        return queryRepo.save(query)
    }

    override fun getAll(): List<Query> {
        return queryRepo.findAll()
    }

    override fun execute(id: Long): List<Array<Any>> {
        return emptyList()
    }
}