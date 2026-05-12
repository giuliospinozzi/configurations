package it.umbria.regione.config

import org.kohsuke.args4j.Option

open class DatabaseParameters : DefaultParameters() {

    @Option(name = "--dbUrl", metaVar = "URL", usage = "URL of database")
    var dbUrl: String = "localhost"

    @Option(name = "--dbPort", metaVar = "PORT", usage = "Port of database")
    var dbPort: Int = 3306

    @Option(name = "--dbUser", metaVar = "USER", usage = "User of database")
    var dbUser: String = "root"

    @Option(name = "--dbPassword", required = true, metaVar = "PASSWORD", usage = "Password of database (required)")
    lateinit var dbPassword: String

    @Option(name = "--dbName", metaVar = "NAME", usage = "Name of database")
    lateinit var dbName: String

    override fun toString(): String {
        return """${this::class.java.simpleName}
            |   dbUrl='$dbUrl' 
            |   dbPort=$dbPort 
            |   dbUser='$dbUser'
            |   dbPassword='********'
            |   dbName='$dbName'""".trimMargin()
    }
}
