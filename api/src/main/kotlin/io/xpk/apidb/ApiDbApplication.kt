package io.xpk.apidb

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.config.SortedResourcesFactoryBean
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class ApiDbApplication : WebMvcConfigurer {
  @Bean
  fun dataSourceLookup(applicationContext: ApplicationContext, apiDbProperties: ApiDbProperties): MapDataSourceLookup {
    val mapDataSourceLookup = MapDataSourceLookup()
    for (dataSourceProperties in apiDbProperties.datasources) {
      val factory = SortedResourcesFactoryBean(
        applicationContext,
        listOfNotNull(dataSourceProperties.schema, dataSourceProperties.data).flatten()
      )
      factory.afterPropertiesSet()
      val resourceDatabasePopulator = ResourceDatabasePopulator()
      resourceDatabasePopulator.addScripts(*factory.getObject())
      val dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
      DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource)

      mapDataSourceLookup.addDataSource(dataSourceProperties.name, dataSource)
    }
    return mapDataSourceLookup
  }
}

@Component
@ConfigurationProperties("apidb")
class ApiDbProperties {
  var datasources: MutableList<DataSourceProperties> = mutableListOf()
}

fun main(args: Array<String>) {
  runApplication<ApiDbApplication>(*args)
}

