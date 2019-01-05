package io.xpk.apidb

import org.apache.commons.lang3.math.NumberUtils
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
    val (api, sqlScripts, links) = getStuff(apiDefinitionDbName, method, url.path, timestamp)
    val db = jdbc(tenantDbName)
    val namedParameters = getParameters(api[0], args)
    val totalResult = mutableMapOf("api" to api, "links" to links)
    sqlScripts.forEach {
      totalResult[it["result_key_name"] as String] = when (method) {
        HttpMethod.GET -> db.query(it["sql_text"] as String, namedParameters, ColumnMapRowMapper())
        HttpMethod.POST -> {
          val generatedKeyHolder = GeneratedKeyHolder()
          db.update(it["sql_text"] as String, namedParameters, generatedKeyHolder)
          return generatedKeyHolder.keyList
        }
        else -> throw UserErrorException("I don't know how to handle a $method")
      }
    }
    return totalResult
  }

  private fun getParameters(api: Map<String, Any>, args: Map<String, Any>): MapSqlParameterSource {
    val defaultQueryString = api["api_default_query_string"] as String? ?: ""
    val defaultParams = UriComponentsBuilder.newInstance().query(defaultQueryString).build().queryParams
    val completeArgs = args.mapValues { if (it.value is String && NumberUtils.isParsable(it.value as String)) Integer.parseInt(it.value as String) else it.value }
    return MapSqlParameterSource(defaultParams).addValues(completeArgs)
  }

  private fun getStuff(
    apiDefinitionDbName: String,
    method: HttpMethod,
    path: String,
    version: Long
  ): List<MutableList<MutableMap<String, Any>>> {
    val jdbc = jdbc(apiDefinitionDbName)
    val apiList = jdbc.jdbcTemplate.query(
      """
         SELECT *
         FROM api a
         WHERE a.api_method = ?
           AND a.api_path = ?
           AND a.api_version <= ?
         ORDER BY a.api_version DESC
         LIMIT 1
      """.trimIndent(),
      ColumnMapRowMapper(),
      method.name,
      path,
      version
    )
    if (apiList.isEmpty()) {
      throw NotFoundException("That api, '${method.name} $path', does not exist or does not have any attached SQL scripts in db '$apiDefinitionDbName'.")
    }
    val api = apiList[0]
    val sqlScripts = jdbc.jdbcTemplate.query(
      """
         SELECT sql_script.*, atss.result_key_name FROM sql_script
           JOIN api_to_sql_script atss ON sql_script.sql_script_id = atss.sql_script_id
           JOIN api a ON atss.api_id = a.api_id
         WHERE a.api_id = ?;
        """.trimIndent(),
      ColumnMapRowMapper(),
      api["api_id"]
    )
    val links = jdbc.jdbcTemplate.query(
      """
         SELECT link.* FROM link
           JOIN api_to_link atl ON link.link_id = atl.link_id
           JOIN api a ON atl.api_id = a.api_id
         WHERE a.api_id = ?;
        """.trimIndent(),
      ColumnMapRowMapper(),
      api["api_id"]
    )
    return listOf(apiList, sqlScripts, links)
  }

  fun jdbc(dbName: String): NamedParameterJdbcTemplate {
    return try {
      NamedParameterJdbcTemplate(dataSourceLookup.getDataSource(dbName))
    } catch (ex: DataSourceLookupFailureException) {
      throw UserErrorException("That db, '$dbName', does not exist.")
    }
  }
}
