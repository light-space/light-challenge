package org.light.challenge.app

import com.codahale.metrics.health.HealthCheck
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.eclipse.jetty.servlets.CrossOriginFilter
import java.util.EnumSet
import javax.servlet.DispatcherType

class Config : Configuration()

class App: Application<Config>() {
    override fun run(configuration: Config, environment: Environment) {
        environment.jersey().register(WorkflowResource())
        environment.healthChecks().register("APIHealthCheck", object : HealthCheck() {
            override fun check(): Result {
                return Result.healthy()
            }
        })

        val cors = environment.servlets().addFilter("CORS", CrossOriginFilter::class.java)
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*")
        cors.setInitParameter(
            CrossOriginFilter.ALLOWED_HEADERS_PARAM,
            "X-Requested-With,Content-Type,Accept,Origin,Authorization"
        )
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD")
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true")
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType::class.java), true, "/*")
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
