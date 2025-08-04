package org.light.challenge.core.engine

import java.time.Duration

/**
 * Configuration for RuleEngine behavior.
 */
data class RuleEngineConfig(
    val stopAfterFirstMatch: Boolean = false,
    val enableExpressionCache: Boolean = true,
    val maxCacheSize: Int = 1000,
    val timeout: Duration = Duration.ofMinutes(30)
) {
    init {
        require(maxCacheSize > 0) { "Max cache size must be positive" }
    }

    /**
     * Builder for RuleEngineConfig.
     */
    class Builder {
        private var stopAfterFirstMatch: Boolean = false
        private var enableExpressionCache: Boolean = true
        private var maxCacheSize: Int = 1000
        private var timeout: Duration = Duration.ofMinutes(30)

        fun stopAfterFirstMatch(stop: Boolean) = apply { this.stopAfterFirstMatch = stop }
        fun enableExpressionCache(enable: Boolean) = apply { this.enableExpressionCache = enable }
        fun maxCacheSize(size: Int) = apply {
            require(size > 0) { "Max cache size must be positive" }
            this.maxCacheSize = size
        }

        fun timeout(duration: Duration) = apply { this.timeout = duration }

        fun build(): RuleEngineConfig {
            return RuleEngineConfig(
                stopAfterFirstMatch = stopAfterFirstMatch,
                enableExpressionCache = enableExpressionCache,
                maxCacheSize = maxCacheSize,
                timeout = timeout
            )
        }
    }

    companion object {
        fun builder(): Builder = Builder()
        fun default(): RuleEngineConfig =
            builder()
                .enableExpressionCache(true)
                .stopAfterFirstMatch(true)
                .maxCacheSize(1000)
                .timeout(Duration.ofMinutes(60))
                .build()
    }
} 