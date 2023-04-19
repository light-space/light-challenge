package org.light.challenge.app

import com.codahale.metrics.health.HealthCheck
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment

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
}

fun main(args: Array<String>) {
    App().run(*args)
}
