package org.light.challenge.core.engine

import com.github.benmanes.caffeine.cache.Cache
import mu.KotlinLogging
import org.light.challenge.core.engine.api.ActionExecutor
import org.light.challenge.core.engine.api.Facts
import org.light.challenge.core.engine.api.Rule
import org.light.challenge.core.engine.api.RuleResult
import org.mvel2.MVEL
import java.io.Serializable
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Evaluates individual rules against facts.
 * Handles MVEL expression evaluation and action execution.
 */
class RuleEvaluator(
    private val actionExecutor: ActionExecutor,
    private val expressionCache: Cache<String, Serializable>?
) {

    /**
     * Evaluates a single rule against the given facts.
     */
    fun evaluate(rule: Rule, facts: Facts): RuleResult {
        return try {
            logger.debug { "Evaluating rule: ${rule.id}" }

            val conditionResult = evaluateCondition(rule.conditionExpression, facts.toMap())

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

    /**
     * Evaluates a condition expression using MVEL.
     */
    private fun evaluateCondition(expression: String, factMap: Map<String, Any>): Boolean {
        val compiledExpression = if (expressionCache != null) {
            expressionCache.get(expression) { MVEL.compileExpression(it) }
        } else {
            MVEL.compileExpression(expression)
        }

        return MVEL.executeExpression(compiledExpression, factMap) as Boolean
    }
} 