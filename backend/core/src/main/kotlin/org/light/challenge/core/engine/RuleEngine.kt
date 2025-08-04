package org.light.challenge.core.engine

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import mu.KotlinLogging
import org.light.challenge.core.engine.api.ActionExecutor
import org.light.challenge.core.engine.api.Facts
import org.light.challenge.core.engine.api.Rule
import org.light.challenge.core.engine.api.RuleResult
import java.io.Serializable

private val logger = KotlinLogging.logger {}

/**
 * Orchestrates rule evaluation process.
 * Manages caching, rule ordering, and evaluation flow control.
 */
class RuleEngine(private val config: RuleEngineConfig) {
    private val expressionCache: Cache<String, Serializable>? = createCache()

    /**
     * Evaluates rules against facts using the provided action executor.
     * Rules are processed in priority order.
     */
    fun evaluate(
        rules: List<Rule>,
        facts: Facts,
        actionExecutor: ActionExecutor
    ): List<RuleResult> {
        logger.info { "Evaluating ${rules.size} rules with config: stopAfterFirstMatch=${config.stopAfterFirstMatch}" }

        val ruleEvaluator = RuleEvaluator(actionExecutor, expressionCache)
        val results = mutableListOf<RuleResult>()
        val sortedRules = rules.sortedByDescending { it.priority }

        for (rule in sortedRules) {
            val result = ruleEvaluator.evaluate(rule, facts)
            results.add(result)

            if (shouldStopEvaluation(result)) {
                logger.info { "Stopping evaluation after first match (rule: ${rule.id})" }
                break
            }
        }

        return results
    }

    /**
     * Creates cache if enabled in configuration.
     */
    private fun createCache(): Cache<String, Serializable>? {
        return if (config.enableExpressionCache) {
            Caffeine.newBuilder()
                .maximumSize(config.maxCacheSize.toLong())
                .expireAfterWrite(config.timeout)
                .recordStats()
                .build()
        } else null
    }

    /**
     * Determines if evaluation should stop based on configuration and result.
     */
    private fun shouldStopEvaluation(result: RuleResult): Boolean {
        return result.matched && config.stopAfterFirstMatch
    }
}

