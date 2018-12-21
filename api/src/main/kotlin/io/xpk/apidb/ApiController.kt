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
    val tenantDbName = url.host.substringBefore(".", "devdb")
    val apiDefinitionDbName = url.host.substringBefore(".apidb", "devdb")
    return apiService.executeApi(method, url, args, tenantDbName, apiDefinitionDbName)
  }
}
