package com.pureteamlead.backend.repository

interface DataRepository {
    fun executeQuery(query: String): Any
}