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
    apiDefinitionDbName: String
  ): Any {
    val apiText = getApiSql(apiDefinitionDbName, method, url.path)
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

  private fun getApiSql(apiDefinitionDbName: String, method: HttpMethod, api: String): String {
    val apiText =
      jdbc(apiDefinitionDbName).jdbcTemplate.query(
        "SELECT sql FROM api_sql WHERE api = ?",
        SingleColumnRowMapper<String>(),
        method.name + " " + api
      )
    if (apiText.isEmpty()) {
      throw NotFoundException("That api, '$api', does not exist in db '$apiDefinitionDbName'.")
    }
    if (apiText.size > 1) {
      throw NotFoundException("That api, '$api', has too many definitions (${apiText.size}) in db '$apiDefinitionDbName'.")
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
