package io.xpk.apidb

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.*
import java.net.URL
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class ApiController(val apiService: ApiService) {

  @RequestMapping
  fun api(
    method: HttpMethod,
    httpEntity: HttpEntity<Map<String, Any>>,
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestParam params: Map<String, Any>,
    @RequestHeader("tenant-db-name", required = false) tenantDbNameHeader: String?,
    @RequestHeader("api-definition-db-name", required = false) apiDefinitionDbNameHeader: String?,
    @RequestHeader("api-timestamp", required = false) apiTimestamp: Long?
  ): Any {
    val url = URL(request.requestURL.toString())
    val args = if (method == HttpMethod.GET) params else httpEntity.body ?: hashMapOf()
    val tenantDbName = tenantDbNameHeader ?: url.host.substringBefore(".", "devdb")
    val apiDefinitionDbName = apiDefinitionDbNameHeader ?: url.host.substringBefore(".apidb", "devdb").substringAfterLast(".")
    val timestamp = apiTimestamp ?: System.currentTimeMillis()
    response.setHeader("tenant-db-name", tenantDbName)
    response.setHeader("api-definition-db-name", apiDefinitionDbName)
    response.setHeader("api-timestamp", timestamp.toString())
    return apiService.executeApi(method, url, args, tenantDbName, apiDefinitionDbName, timestamp)
  }
}
