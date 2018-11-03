package io.xpk.apidb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup

@SpringBootApplication
class ApidbApplication {
  @Bean
  fun dataSourceLookup(): MapDataSourceLookup {
    return MapDataSourceLookup()
  }
}

fun main(args: Array<String>) {
  runApplication<ApidbApplication>(*args)
}

