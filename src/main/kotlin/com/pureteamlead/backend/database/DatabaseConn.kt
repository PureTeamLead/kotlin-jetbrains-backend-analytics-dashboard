package com.pureteamlead.backend.database

import java.sql.Connection

interface DatabaseConn {
    fun getConnection(dbName: String, publicConn: Boolean): Connection
}