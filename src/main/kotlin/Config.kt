package org.censusmate

data class Config(
    val database: DatabaseConfig,
    val jwt: JwtConfig,
    val server: ServerConfig
) {
    data class DatabaseConfig(
        val url: String,
        val username: String,
        val password: String,
        val maxPoolSize: Int
    )

    data class JwtConfig(
        val secret: String,
        val issuer: String,
        val audience: String,
        val expirationHours: Long,
    )

    data class ServerConfig(
        val host: String,
        val port: Int
    )

    companion object {
        fun load(): Config {
            val databaseConfig = DatabaseConfig(
                url = System.getenv("DB_URL") ?: "jdbc:postgresql://127.0.0.1:5432/censusmate?sslmode=disable",
                username = System.getenv("DB_USER") ?: "postgres",
                password = System.getenv("DB_PASSWORD") ?: "password",
                maxPoolSize = System.getenv("DB_MAX_POOL_SIZE")?.toIntOrNull() ?: 10
            )

            val jwtConfig = JwtConfig(
                secret = System.getenv("JWT_SECRET") ?: "default-secret",
                issuer = System.getenv("JWT_ISSUER") ?: "censusmate",
                audience = System.getenv("JWT_AUDIENCE") ?: "censusmate-users",
                expirationHours = System.getenv("JWT_EXPIRATION_HOURS")?.toLongOrNull() ?: 24L
            )

            val serverConfig = ServerConfig(
                host = System.getenv("SERVER_HOST") ?: "127.0.0.1",
                port = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 3000
            )

            return Config(
                database = databaseConfig,
                jwt = jwtConfig,
                server = serverConfig
            )
        }
    }
}