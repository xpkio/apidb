package io.xpk.apidb

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.jdbc.core.ColumnMapRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.servlet.HandlerMapping

@RestController
class ApiController(val dataSourceLookup: MapDataSourceLookup) {

  @GetMapping
  fun api(webRequest: ServletWebRequest): Any {
    val path = webRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) as String
    val jdbcTemplate = JdbcTemplate(dataSourceLookup.getDataSource("default"))
    return jdbcTemplate.query("SELECT * FROM public.user", ColumnMapRowMapper())
  }

  @PostMapping("/connectDb")
  fun connectDb(@RequestBody body: HashMap<String, Any>) {
    val properties = DataSourceProperties()
    properties.url = body.getOrDefault("url", "jdbc:postgresql://localhost:5432/apidb") as String
    properties.username = body.getOrDefault("username", "postgres") as String
    properties.password = body.getOrDefault("password", "postgres") as String
    val dataSourceName = body.getOrDefault("name", "default") as String
    val dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    dataSourceLookup.addDataSource(dataSourceName, dataSource)
  }
}
