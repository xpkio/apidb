package io.xpk.apidb

import org.springframework.jdbc.core.ColumnMapRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.SingleColumnRowMapper
import org.springframework.jdbc.datasource.lookup.DataSourceLookup
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController {

  @GetMapping
  fun api(apiCall: ApiCall, @TenantDb tenantDb: JdbcTemplate, @ApiDb apiDb: JdbcTemplate): Any {
    val api = apiCall.method.name + " " + apiCall.path
    val apiText = apiDb.query("SELECT sql FROM api_sql WHERE api = ?", SingleColumnRowMapper<String>(), api)
    if (apiText.isEmpty()) {
      throw NotFoundException("That api, '$api', does not exist in db ${apiCall.apiDbName}.")
    }
    if (apiText.size > 1) {
      throw NotFoundException("That api, '$api', has too many definitions (${apiText.size}) in db ${apiCall.apiDbName}.")
    }
    return tenantDb.query(apiText[0], ColumnMapRowMapper())
  }
}
