package com.pureteamlead.backend.repository

import com.pureteamlead.backend.entity.Query
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QueryRepository : JpaRepository<Query, Long>