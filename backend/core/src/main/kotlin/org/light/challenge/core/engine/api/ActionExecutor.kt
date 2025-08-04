package org.light.challenge.core.engine.api

/**
 * Executes actions by mapping action IDs to executable functions.
 */
class ActionExecutor {
    private val executors = mutableMapOf<String, (Action, Facts) -> ActionResult>()

    /**
     * Registers an action executor using a lambda function.
     */
    fun register(actionId: String, executor: (Action, Facts) -> ActionResult) {
        executors[actionId] = executor
    }

    /**
     * Executes an action using the registered executor.
     */
    fun execute(action: Action, facts: Facts): ActionResult {
        val executor = executors[action.id]
            ?: return ActionResult.failure("No executor registered for action: ${action.id}")

        return executor(action, facts)
    }
} 