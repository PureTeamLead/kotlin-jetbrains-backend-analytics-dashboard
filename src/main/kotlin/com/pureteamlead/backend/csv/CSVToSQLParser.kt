package com.pureteamlead.backend.csv

import com.pureteamlead.backend.database.DatabaseConn

interface CSVToSQLParser {
    fun convert(filepath: String, db: DatabaseConn)
}