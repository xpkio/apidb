package io.xpk.apidb

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.*
import java.net.URL
import javax.servlet.http.HttpServletRequest

@RestController
class ApiController(val apiService: ApiService) {

  @RequestMapping
  fun api(
    method: HttpMethod,
    httpEntity: HttpEntity<Map<String, Any>>,
    webRequest: HttpServletRequest,
    params: Map<String, String>
  ): Any {
    val url = URL(webRequest.requestURL.toString())
    val args = if (method == HttpMethod.GET) params else httpEntity.body ?: hashMapOf()
    val tenantDbName = getTenantDbName(url, httpEntity)
    val apiDefinitionDbName = getApiDefinitionDbName(url, httpEntity)
    return apiService.executeApi(method, url, args, tenantDbName, apiDefinitionDbName)
  }

  private fun getTenantDbName(url: URL, httpEntity: HttpEntity<Map<String, Any>>): String {
    if (httpEntity.headers.containsKey("tenant-db-name") && httpEntity.headers["tenant-db-name"] != null) {
      return httpEntity.headers["tenant-db-name"]!![0]
    }
    return url.host.substringBefore(".", "devdb")
  }

  private fun getApiDefinitionDbName(url: URL, httpEntity: HttpEntity<Map<String, Any>>): String {
    if (httpEntity.headers.containsKey("api-definition-db-name") && httpEntity.headers["api-definition-db-name"] != null) {
      return httpEntity.headers["api-definition-db-name"]!![0]
    }
    return url.host.substringBefore(".apidb", "devdb").substringAfterLast(".")
  }
}
