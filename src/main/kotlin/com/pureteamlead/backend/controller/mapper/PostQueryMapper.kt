package com.pureteamlead.backend.controller.mapper

import com.pureteamlead.backend.controller.model.PostQueryRequest
import com.pureteamlead.backend.entity.Query

interface PostQueryMapper {
    fun fromPostReqToQuery(req: PostQueryRequest): Query
}