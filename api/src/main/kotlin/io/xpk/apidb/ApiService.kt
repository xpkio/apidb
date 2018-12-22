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
import java.sql.Timestamp

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
    val api = method.name + " " + path
    val apiText =
      jdbc(apiDefinitionDbName).jdbcTemplate.query(
        "SELECT sql FROM api_sql WHERE api = ? AND vt < ? ORDER BY vt DESC LIMIT 1",
        SingleColumnRowMapper<String>(),
        api,
        Timestamp(timestamp)
      )
    if (apiText.isEmpty()) {
      throw NotFoundException("That api, '$api', does not exist in db '$apiDefinitionDbName'.")
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
