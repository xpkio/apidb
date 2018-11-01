package io.xpk.apidb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApidbApplication

fun main(args: Array<String>) {
    runApplication<ApidbApplication>(*args)
}