package org.light.challenge.core.engine.api

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Executes actions by mapping action IDs to executable functions.
 * Provides timing and detailed error information for better tracing.
 */
class ActionExecutor {
    private val executors = mutableMapOf<String, (Action, Facts) -> ActionResult>()

    /**
     * Registers an action executor using a lambda function.
     */
    fun register(actionId: String, executor: (Action, Facts) -> ActionResult) {
        executors[actionId] = executor
        logger.debug { "Registered executor for action: $actionId" }
    }

    /**
     * Executes an action using the registered executor with timing and error handling.
     */
    fun execute(action: Action, facts: Facts): ActionResult {
        logger.debug { "Executing action: ${action.id}" }

        val executor = executors[action.id]
        if (executor == null) {
            logger.error { "No executor registered for action: ${action.id}" }
            return ActionResult.failure(
                message = "No executor registered for action: ${action.id}. Available actions: ${
                    executors.keys.joinToString(
                        ", "
                    )
                }",
            )
        }

        return try {
            executor(action, facts)
        } catch (e: Exception) {
            logger.error(e) { "Unexpected error executing action ${action.id}: ${e.message}" }
            ActionResult.failure(
                message = "Unexpected error during action execution: ${e.message}",
            )
        }
    }
} 