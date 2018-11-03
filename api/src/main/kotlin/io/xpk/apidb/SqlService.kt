package io.xpk.apidb

import org.springframework.http.HttpMethod
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.SingleColumnRowMapper
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.stereotype.Service

@Service
class SqlService(val dataSourceLookup: MapDataSourceLookup) {

  fun getSqlForPath(method: HttpMethod, path: String): String {
    val split = path.split("/", limit = 3)
    val dbName = split[1] // [0] is empty string
    val pathKey = method.name + " " + split[2]
    val dataSource = dataSourceLookup.getDataSource(dbName)
    val jdbcTemplate = JdbcTemplate(dataSource)
    return jdbcTemplate.query("SELECT sql_text FROM public.sql where key = ?", SingleColumnRowMapper<String>(), pathKey)[0]!!
  }
}
