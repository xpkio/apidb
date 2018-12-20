package io.xpk.apidb

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableWebMvc
@Configuration
class ApiDbWebMvcConfiguration(
  val apiCallCallArgResolver: ApiCallArgResolver,
  val tenantDbArgResolver: TenantDbArgResolver,
  val apiDbArgResolver: ApiDbArgResolver
) : WebMvcConfigurer {

  override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
    argumentResolvers.add(apiCallCallArgResolver)
    argumentResolvers.add(tenantDbArgResolver)
    argumentResolvers.add(apiDbArgResolver)
  }

}