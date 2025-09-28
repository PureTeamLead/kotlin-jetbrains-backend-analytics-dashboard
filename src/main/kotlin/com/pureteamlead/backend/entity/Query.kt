package com.pureteamlead.backend.entity

import com.pureteamlead.backend.errors.InvalidQueryException
import jakarta.persistence.*

@Entity
@Table(name = "queries")
data class Query(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long?,

    @Column(name = "query")
    val query: String,
) {
    @Throws(InvalidQueryException::class)
    fun validate() {
        val regexp = Regex("""(?i)^\s*SELECT\s+.+\s+FROM\s+\S+.*${'$'}""")

        if (!regexp.containsMatchIn(query)) {
            throw InvalidQueryException("Invalid query format")
        }
    }
}


