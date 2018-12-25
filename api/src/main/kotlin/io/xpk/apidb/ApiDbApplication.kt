package io.xpk.apidb

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Component
import javax.sql.DataSource

@SpringBootApplication
class ApiDbApplication : WebMvcConfigurer {
  @Bean
  fun dataSourceLookup(primaryDataSource: DataSource, apiDbProperties: ApiDbProperties): MapDataSourceLookup {
    val mapDataSourceLookup = MapDataSourceLookup()
    mapDataSourceLookup.addDataSource(apiDbProperties.primaryDataSourceName, primaryDataSource)
    for (dataSourceProperties in apiDbProperties.datasources) {
      val dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
      mapDataSourceLookup.addDataSource(dataSourceProperties.name, dataSource)
    }
    return mapDataSourceLookup
  }
}

@Component
@ConfigurationProperties("apidb")
class ApiDbProperties {
  var datasources: MutableList<DataSourceProperties> = mutableListOf()
  var primaryDataSourceName: String = "core"
}

fun main(args: Array<String>) {
  runApplication<ApiDbApplication>(*args)
}

