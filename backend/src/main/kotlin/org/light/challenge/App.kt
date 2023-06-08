package org.light.challenge

import com.codahale.metrics.health.HealthCheck
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.light.challenge.rest.WorkflowResource

class Config : Configuration()

class App: Application<Config>() {
    override fun run(configuration: Config, environment: Environment) {
        environment.jersey().register(WorkflowResource())
        environment.healthChecks().register("APIHealthCheck", object : HealthCheck() {
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
    }
}

fun main(args: Array<String>) {
    App().run(*args)
}
