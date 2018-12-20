package io.xpk.apidb

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.web.bind.annotation.*

@RestController
class DbController(
  val dataSourceLookup: MapDataSourceLookup
) {

  @PutMapping("/apidb/db/{dbName}")
  fun connectDb(@RequestBody body: HashMap<String, Any>, @PathVariable dbName: String) {
    val properties = DataSourceProperties()
    properties.url = body.getOrDefault("url", "jdbc:postgresql://localhost:5432/apidb") as String
    properties.username = body.getOrDefault("username", "postgres") as String
    properties.password = body.getOrDefault("password", "postgres") as String
    properties.name = dbName

    val dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    dataSource.poolName = dbName

    // Idempotent
    dataSourceLookup.addDataSource(dbName, dataSource)
  }

  @GetMapping("/apidb/dbs")
  fun getDbs(): List<String> {
    return dataSourceLookup.dataSources.keys.toList()
  }

  @PostMapping("/apidb/db/{apiDbName}/init")
  fun initDb(@PathVariable apiDbName: String) {
    val jdbcTemplate = JdbcTemplate(dataSourceLookup.getDataSource(apiDbName))
    jdbcTemplate.update(
      "CREATE TABLE IF NOT EXISTS api_sql\n" +
          "(\n" +
          "    id serial PRIMARY KEY,\n" +
          "    api text,\n" +
          "    sql text\n" +
          ")"
    )
    jdbcTemplate.update("CREATE UNIQUE INDEX IF NOT EXISTS api_sql_id_uindex ON apidb.api_sql (id)")
    jdbcTemplate.update("CREATE UNIQUE INDEX IF NOT EXISTS api_sql_api_uindex ON apidb.api_sql (api)")
  }
}
