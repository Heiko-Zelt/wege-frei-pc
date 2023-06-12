package de.heikozelt.liquibase

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.Scope
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.visitor.DefaultChangeExecListener
import liquibase.command.CommandScope
import liquibase.command.core.UpdateCommandStep
import liquibase.command.core.UpdateSqlCommandStep
import liquibase.command.core.helpers.DbUrlConnectionCommandStep
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.OutputStreamWriter
import java.lang.Thread.sleep
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import liquibase.command.core.helpers.DatabaseChangelogCommandStep

fun main(args: Array<String>) {

    val log: Logger = LoggerFactory.getLogger("main")
    log.info("Hello World!")
    log.info("Program arguments: ${args.joinToString()}")
    val conn = openConnection() //your openConnection logic
    updateDatabaseSchema(conn)
    sleep(1000)
    test(conn)
    conn.close()
    log.info("finished")
}

fun openConnection(): Connection {
    val jdbcUrl = "jdbc:h2:file:~/liquibase_test;TRACE_LEVEL_FILE=4"
    val username = "wegefrei"
    val password = ""
    return DriverManager.getConnection(jdbcUrl, username, password)
}

fun updateDatabaseSchema(conn: Connection) {
    val log: Logger = LoggerFactory.getLogger("updateDatabaseSchema")
    try {
        val config: MutableMap<String, Any> = HashMap()
        Scope.child(config) {
            val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(conn))
            val liquibase = Liquibase("database_changelog.xml", ClassLoaderResourceAccessor(), database)
            val writer = OutputStreamWriter(System.out, StandardCharsets.UTF_8)
            log.info("safe? ${liquibase.isSafeToRunUpdate}")
            log.info("auto commit? ${conn.autoCommit}")
            liquibase.validate()

            liquibase.listUnrunChangeSets(null, null);

            val updateSqlCommand = CommandScope(*UpdateSqlCommandStep.COMMAND_NAME).apply {
                addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database)
                addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "database_changelog.xml")
                addArgumentValue(UpdateCommandStep.CONTEXTS_ARG, Contexts().toString())
                addArgumentValue(UpdateCommandStep.LABEL_FILTER_ARG, LabelExpression().originalString)
                addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, ChangeLogParameters(database))
            }
            val results = updateSqlCommand.execute()
            log.info("RESULTS: $results")
            results.results.values.forEach { result ->
                log.info("result: $result")
                if(result is DefaultChangeExecListener) {
                    log.info("deployedChangeSets: ${result.deployedChangeSets}")
                }
            }

            val updateCommand = CommandScope(*UpdateCommandStep.COMMAND_NAME).apply {
                addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database)
                addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "database_changelog.xml")
                addArgumentValue(UpdateCommandStep.CONTEXTS_ARG, Contexts().toString())
                addArgumentValue(UpdateCommandStep.LABEL_FILTER_ARG, LabelExpression().originalString)
                addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, ChangeLogParameters(database))
            }
            updateCommand.execute()
        }
    } catch (ex: Exception) {
        log.error("exception bei Schema Update: $ex")
        log.error("autsch")
    }
}

fun test(conn: Connection) {
    val log: Logger = LoggerFactory.getLogger("test")
    val stmt = conn.createStatement();
    val rs = stmt.executeQuery("SELECT * FROM public.PHOTOS")
    while (rs.next()) {
        val path = rs.getString("PATH")
        log.info("path: $path\n")
    }
}
