package com.pureteamlead.backend.database

import java.sql.Connection
import java.sql.DriverManager

object DatabaseConnection : DatabaseConn {
    override fun getConnection(dbName: String): Connection =
        DriverManager.getConnection("jdbc:h2:mem:$dbName")
}