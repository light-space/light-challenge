package org.light.challenge.core.engine

import mu.KotlinLogging
import org.light.challenge.core.engine.api.ActionExecutor
import org.light.challenge.core.engine.api.ConditionEvaluator
import org.light.challenge.core.engine.api.Facts
import org.light.challenge.core.engine.api.Rule
import org.light.challenge.core.engine.api.RuleResult
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Evaluates individual rules against facts.
 * Handles condition evaluation and action execution using pluggable evaluators.
 */
class RuleEvaluator(
    private val actionExecutor: ActionExecutor,
    private val conditionEvaluator: ConditionEvaluator
) {

    /**
     * Evaluates a single rule against the given facts.
     */
    fun evaluate(rule: Rule, facts: Facts): RuleResult {
        return try {
            logger.debug { "Evaluating rule: ${rule.id}" }

            val conditionResult = conditionEvaluator.evaluateBoolean(rule.conditionExpression, facts)

            if (conditionResult) {
                logger.info { "Rule ${rule.id} matched, executing action" }
                val actionResult = actionExecutor.execute(rule.action, facts)
                RuleResult(UUID.randomUUID(), rule, true, actionResult, null)
            } else {
                logger.debug { "Rule ${rule.id} did not match" }
                RuleResult(UUID.randomUUID(), rule, false, null, null)
            }
        } catch (e: Exception) {
            logger.error(e) { "Error evaluating rule ${rule.id}: ${e.message}" }
            RuleResult(UUID.randomUUID(), rule, false, null, e.message)
        }
    }
} 