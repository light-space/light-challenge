package org.light.challenge.app

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.light.challenge.core.WorkflowConfig
import org.light.challenge.rest.WorkflowResource


class Config : io.dropwizard.Configuration()

class App : io.dropwizard.Application<Config>() {
    override fun run(configuration: Config, environment: Environment) {
        // Register the HK2 service binder
        environment.jersey().register(WorkflowConfig())

        // Register the REST resource
        environment.jersey().register(WorkflowResource::class.java)

        // Register health check
        environment.healthChecks()
            .register("APIHealthCheck", object : com.codahale.metrics.health.HealthCheck() {
                override fun check(): Result {
                    return Result.healthy()
                }
            })
    }

    override fun initialize(bootstrap: Bootstrap<Config>) {
        bootstrap.objectMapper.registerModules(
            KotlinModule.Builder().build(),
        )
        bootstrap.objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }
}

fun main(args: Array<String>) {
    App().run(*args)
}
