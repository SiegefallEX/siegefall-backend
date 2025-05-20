package com.michihides.siegefall_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SiegefallBackendApplication

fun main(args: Array<String>) {
	runApplication<SiegefallBackendApplication>(*args)
}
