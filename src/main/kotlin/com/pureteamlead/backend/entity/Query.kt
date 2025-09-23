package com.pureteamlead.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "queries")
data class Query(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long,

    @Column(name = "query")
    val queryStr: String
)


