package com.pureteamlead.backend.controller.mapper

import com.pureteamlead.backend.controller.model.PostQueryRequest
import com.pureteamlead.backend.entity.Query
import org.springframework.stereotype.Component

@Component
class PostQueryMapperImpl : PostQueryMapper {
    override fun fromPostReqToQuery(req: PostQueryRequest) = Query(
        id = null,
        query = req.query
    )
}