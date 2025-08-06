package org.light.challenge.core.engine

import mu.KotlinLogging
import org.light.challenge.core.engine.api.*
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Evaluates individual rules against facts.
 */
class RuleEvaluator(
    private val actionExecutor: ActionExecutor,
    private val conditionEvaluator: ConditionEvaluator
) {

    /**
     * Evaluates a single rule against the given facts.
     */
    fun evaluate(rule: Rule, facts: Facts): RuleResult {
        val ruleResultId = UUID.randomUUID()

        return try {
            logger.debug { "Evaluating rule: ${rule.id}" }

            val conditionMatched =
                conditionEvaluator.evaluateBoolean(rule.conditionExpression, facts)

            if (conditionMatched) {
                logger.debug { "Rule ${rule.id} matched, executing action" }
                val actionResult = actionExecutor.execute(rule.action, facts)

                RuleResult(
                    id = ruleResultId,
                    rule = rule,
                    matched = true,
                    evaluationError = null,
                    actionResult = actionResult
                )
            } else {
                logger.debug { "Rule ${rule.id} did not match" }
                RuleResult(
                    id = ruleResultId,
                    rule = rule,
                    matched = false,
                    evaluationError = null,
                    actionResult = null
                )
            }
        } catch (e: ConditionEvaluationException) {
            // Only catch condition evaluation errors
            logger.error(e) { "Error evaluating rule ${rule.id} condition: ${e.message}" }
            RuleResult(
                id = ruleResultId,
                rule = rule,
                matched = false,
                evaluationError = "Condition evaluation failed: ${e.message}",
                actionResult = null
            )
        } catch (e: Exception) {
            // Only catch action execution errors
            logger.error(e) { "Error executing rule ${rule.id} action: ${e.message}" }
            RuleResult(
                id = ruleResultId,
                rule = rule,
                matched = false,
                evaluationError = "Rule execution failed: ${e.message}",
                actionResult = null
            )
        }
    }
} 