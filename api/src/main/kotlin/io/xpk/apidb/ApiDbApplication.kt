package io.xpk.apidb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class ApiDbApplication : WebMvcConfigurer {
  @Bean
  fun dataSourceLookup(): MapDataSourceLookup {
    return MapDataSourceLookup()
  }
}

fun main(args: Array<String>) {
  runApplication<ApiDbApplication>(*args)
}

