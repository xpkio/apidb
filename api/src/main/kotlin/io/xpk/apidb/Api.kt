package io.xpk.apidb

import org.springframework.http.HttpMethod

data class ApiCall(
  val method: HttpMethod,
  val path: String,
  val tenantDbName: String,
  val apiDbName: String
)
