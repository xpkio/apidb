package io.xpk.apidb

import org.springframework.http.HttpMethod
import org.springframework.jdbc.core.ColumnMapRowMapper
import org.springframework.jdbc.core.SingleColumnRowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import java.net.URL

@Service
class ApiService(val dataSourceLookup: MapDataSourceLookup) {

  fun executeApi(
    method: HttpMethod,
    url: URL,
    args: Map<String, Any>,
    tenantDbName: String,
    apiDefinitionDbName: String,
    timestamp: Long
  ): Any {
    val apiText = getApiSql(apiDefinitionDbName, method, url.path, timestamp)
    val db = jdbc(tenantDbName)
    val namedParameters = MapSqlParameterSource(args)
    return when (method) {
      HttpMethod.GET -> db.query(apiText, namedParameters, ColumnMapRowMapper())
      HttpMethod.POST -> {
        val generatedKeyHolder = GeneratedKeyHolder()
        db.update(apiText, namedParameters, generatedKeyHolder)
        return generatedKeyHolder.keyList
      }
      else -> throw UserErrorException("I don't know how to handle a $method")
    }
  }

  private fun getApiSql(
    apiDefinitionDbName: String,
    method: HttpMethod,
    path: String,
    timestamp: Long
  ): String {
    val apiText =
      jdbc(apiDefinitionDbName).jdbcTemplate.query(
        "SELECT sql_text FROM api WHERE method = ? AND path = ? AND version <= ? ORDER BY version DESC LIMIT 1",
        SingleColumnRowMapper<String>(),
        method.name,
        path,
        timestamp
      )
    if (apiText.isEmpty()) {
      throw NotFoundException("That api, '${method.name} $path', does not exist in db '$apiDefinitionDbName'.")
    }
    return apiText[0]
  }

  fun jdbc(dbName: String): NamedParameterJdbcTemplate {
    return try {
      NamedParameterJdbcTemplate(dataSourceLookup.getDataSource(dbName))
    } catch (ex: DataSourceLookupFailureException) {
      throw UserErrorException("That db, '$dbName', does not exist.")
    }
  }
}
