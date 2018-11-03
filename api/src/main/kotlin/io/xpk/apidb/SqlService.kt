package io.xpk.apidb

import org.springframework.http.HttpMethod
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.SingleColumnRowMapper
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.stereotype.Service

@Service
class SqlService(val dataSourceLookup: MapDataSourceLookup) {

  fun getSqlForPath(method: HttpMethod, path: String): String {
    val split = path.split("/", limit = 4)
    val dbName = split[1] // [0] is empty string
    val schemaNameMaybe = split[2]
    val api = method.name + " " + split[3]
    val jdbcTemplate = jdbcTemplate(dbName)
    val schemaName = validateSchemaName(dbName, schemaNameMaybe)
    return jdbcTemplate.query("SELECT sql_text FROM $schemaName.sql where api = ?", SingleColumnRowMapper<String>(), api)
      .getOrNull(0) ?: throw NotFoundException("API '$api' does not exist.")
  }

  private fun jdbcTemplate(dbName: String): JdbcTemplate {
    try {
      return JdbcTemplate(dataSourceLookup.getDataSource(dbName))
    } catch (ex: DataSourceLookupFailureException) {
      throw NotFoundException("DB name '$dbName' does not exist.")
    }
  }

  private fun validateSchemaName(dbName: String, schemaName: String): String {
    return jdbcTemplate(dbName).query(
      "SELECT schema_name FROM information_schema.schemata WHERE schema_name = ?",
      SingleColumnRowMapper<String>(),
      schemaName
    ).getOrNull(0) ?: throw NotFoundException("Schema '$schemaName' does not exist.")
  }
}
