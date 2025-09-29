package com.pureteamlead.backend.database

import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager

@Component
object DatabaseConnection : DatabaseConn {
    override fun getConnection(dbName: String): Connection {
        val conn = DriverManager.getConnection("jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1")

        // to guarantee that queries wouldn't modify the data in database
        conn.isReadOnly = true
        return conn
    }
}