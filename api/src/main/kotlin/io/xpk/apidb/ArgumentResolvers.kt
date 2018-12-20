package io.xpk.apidb

import org.springframework.core.MethodParameter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.lookup.DataSourceLookup
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.servlet.http.HttpServletRequest

@Component
class ApiCallArgResolver : HandlerMethodArgumentResolver {
  override fun supportsParameter(parameter: MethodParameter): Boolean {
    return parameter.parameterType == ApiCall::class.java
  }

  override fun resolveArgument(
    parameter: MethodParameter,
    mavContainer: ModelAndViewContainer?,
    webRequest: NativeWebRequest,
    binderFactory: WebDataBinderFactory?
  ): Any? {
    return apiCall(webRequest)
  }
}

private fun apiCall(webRequest: NativeWebRequest): ApiCall {
  val method = (webRequest as ServletWebRequest).httpMethod!!
  val path = webRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) as String
  val host = ServletUriComponentsBuilder.fromServletMapping(
    webRequest.getNativeRequest(HttpServletRequest::class.java)
  ).build().host!!
  val dbName = host.substringBefore(".", "devdb")
  val apiDbName = host.substringBefore(".apidb", "devdb")
  return ApiCall(method, path, dbName, apiDbName)
}

@Component
class TenantDbArgResolver(
  val dataSourceLookup: DataSourceLookup
) : HandlerMethodArgumentResolver {
  override fun supportsParameter(parameter: MethodParameter): Boolean {
    return parameter.hasParameterAnnotation(TenantDb::class.java) &&
        parameter.parameterType == JdbcTemplate::class.java
  }

  override fun resolveArgument(
    parameter: MethodParameter,
    mavContainer: ModelAndViewContainer?,
    webRequest: NativeWebRequest,
    binderFactory: WebDataBinderFactory?
  ): Any? {
    val apiCall = apiCall(webRequest)
    return jdbcTemplate(dataSourceLookup, apiCall.tenantDbName)
  }
}

@Component
class ApiDbArgResolver(
  val dataSourceLookup: DataSourceLookup
) : HandlerMethodArgumentResolver {
  override fun supportsParameter(parameter: MethodParameter): Boolean {
    return parameter.hasParameterAnnotation(ApiDb::class.java) &&
        parameter.parameterType == JdbcTemplate::class.java
  }

  override fun resolveArgument(
    parameter: MethodParameter,
    mavContainer: ModelAndViewContainer?,
    webRequest: NativeWebRequest,
    binderFactory: WebDataBinderFactory?
  ): Any? {
    val apiCall = apiCall(webRequest)
    return jdbcTemplate(dataSourceLookup, apiCall.apiDbName)
  }
}

fun jdbcTemplate(dataSourceLookup: DataSourceLookup, dbName: String): JdbcTemplate? {
  return try {
    JdbcTemplate(dataSourceLookup.getDataSource(dbName))
  } catch (ex: DataSourceLookupFailureException) {
    null
  }
}

annotation class TenantDb
annotation class ApiDb