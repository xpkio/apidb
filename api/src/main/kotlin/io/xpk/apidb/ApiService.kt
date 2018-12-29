package io.xpk.apidb

import org.springframework.http.HttpMethod
import org.springframework.jdbc.core.ColumnMapRowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
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
    val (api, links) = getApiAndLinks(apiDefinitionDbName, method, url.path, timestamp)
    val db = jdbc(tenantDbName)
    val namedParameters = getParameters(api, args)
    val result = when (method) {
      HttpMethod.GET -> db.query(api["sql_text"] as String, namedParameters, ColumnMapRowMapper())
      HttpMethod.POST -> {
        val generatedKeyHolder = GeneratedKeyHolder()
        db.update(api["sql_text"] as String, namedParameters, generatedKeyHolder)
        return generatedKeyHolder.keyList
      }
      else -> throw UserErrorException("I don't know how to handle a $method")
    }
    return mapOf("links" to links, "results" to result)
  }

  private fun getParameters(api: Map<String, Any>, args: Map<String, Any>): MapSqlParameterSource {
    val defaultQueryString = api["default_query_string"] as String? ?: ""
    val defaultParams = UriComponentsBuilder.newInstance().query(defaultQueryString).build().queryParams
    return MapSqlParameterSource(defaultParams).addValues(args)
  }

  private fun getApiAndLinks(
    apiDefinitionDbName: String,
    method: HttpMethod,
    path: String,
    version: Long
  ): Pair<Map<String, Any>, List<Map<String, Any>>> {
    val jdbc = jdbc(apiDefinitionDbName)
    val apiList =
      jdbc.jdbcTemplate.query(
        "SELECT * FROM api WHERE method = ? AND path = ? AND version <= ? ORDER BY version DESC LIMIT 1",
        ColumnMapRowMapper(),
        method.name,
        path,
        version
      )
    if (apiList.isEmpty()) {
      throw NotFoundException("That api, '${method.name} $path', does not exist in db '$apiDefinitionDbName'.")
    }
    val linkList =
      jdbc.jdbcTemplate.query(
        "SELECT * FROM link WHERE api_method = ? AND api_path = ? AND api_version_max > ?  AND api_version_min <= ? ",
        ColumnMapRowMapper(),
        method.name,
        path,
        version,
        version
      )
    return Pair(apiList[0], linkList)
  }

  fun jdbc(dbName: String): NamedParameterJdbcTemplate {
    return try {
      NamedParameterJdbcTemplate(dataSourceLookup.getDataSource(dbName))
    } catch (ex: DataSourceLookupFailureException) {
      throw UserErrorException("That db, '$dbName', does not exist.")
    }
  }
}
