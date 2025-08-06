package org.light.challenge.core.engine

import org.light.challenge.core.engine.api.ConditionEvaluator
import org.light.challenge.core.engine.evaluator.SpelConditionEvaluator

/**
 * Configuration for RuleEngine behavior.
 */
data class RuleEngineConfig(
    val stopAfterFirstMatch: Boolean = false,
    val conditionEvaluator: ConditionEvaluator = SpelConditionEvaluator()
) {

    /**
     * Builder for RuleEngineConfig.
     */
    class Builder {
        private var stopAfterFirstMatch: Boolean = false
        private var conditionEvaluator: ConditionEvaluator? = null

        fun stopAfterFirstMatch(stop: Boolean) = apply { this.stopAfterFirstMatch = stop }
        fun conditionEvaluator(evaluator: ConditionEvaluator) =
            apply { this.conditionEvaluator = evaluator }

        fun build(): RuleEngineConfig {
            return RuleEngineConfig(
                stopAfterFirstMatch = stopAfterFirstMatch,
                conditionEvaluator = conditionEvaluator ?: SpelConditionEvaluator()
            )
        }
    }

    companion object {
        fun builder(): Builder = Builder()

        fun default(): RuleEngineConfig = RuleEngineConfig()
    }
}