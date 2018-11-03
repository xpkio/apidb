import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        maven("https://repo.spring.io/libs-milestone")
    }

    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.0.6.RELEASE")
    }
}

apply {
    plugin("org.springframework.boot")
}

plugins {
    val kotlinVersion = "1.2.61"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.3.RELEASE"
}

version = "1.0.0-SNAPSHOT"

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-jdbc")
    compile("org.apache.commons:commons-lang3")

    runtime("org.postgresql:postgresql")

    compile("org.jetbrains.kotlin:kotlin-stdlib")
    compile("org.jetbrains.kotlin:kotlin-reflect")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin")

    compile("ch.qos.logback:logback-classic:1.2.3")
    compile("ch.qos.logback:logback-core:1.2.3")
    compile("ch.qos.logback:logback-access:1.2.3")
    compile("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    compile("ch.qos.logback.contrib:logback-jackson:0.1.5")
    compile("net.logstash.logback:logstash-logback-encoder:4.8")
    compile("org.apache.httpcomponents:httpclient:4.4.1")

    testCompile("com.h2database:h2")
    testCompile("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testCompile("org.springframework.security:spring-security-test")
    testCompile("org.junit.jupiter:junit-jupiter-api")
    testRuntime("org.junit.jupiter:junit-jupiter-engine")
}

