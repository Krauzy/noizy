package com.noizy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class NoizyApplication

fun main(args: Array<String>) {
    runApplication<NoizyApplication>(*args)
}
