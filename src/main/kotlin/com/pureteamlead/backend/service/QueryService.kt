package com.pureteamlead.backend.service

import com.pureteamlead.backend.entity.Query

interface QueryService {
    fun create(query: Query): Long
    fun getAll(): List<Query>
    fun execute(id: Long): List<Array<kotlin.Any>>
}