package org.light.challenge.core.engine.api

/**
 * Abstraction for evaluating boolean condition expressions against facts.
 * This allows the rule engine to be agnostic to the specific expression language used.
 */
interface ConditionEvaluator {

    /**
     * Evaluates a boolean condition expression against the provided facts.
     *
     * @param condition The condition expression string to evaluate
     * @param facts Facts container with variables available to the expression
     * @return The boolean result of the condition evaluation
     * @throws ConditionEvaluationException if the condition cannot be evaluated
     */
    fun evaluateBoolean(condition: String, facts: Facts): Boolean
}

/**
 * Exception thrown when condition evaluation fails.
 */
class ConditionEvaluationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) 