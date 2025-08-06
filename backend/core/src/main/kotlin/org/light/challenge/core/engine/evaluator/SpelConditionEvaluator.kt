package org.light.challenge.core.engine.evaluator

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import mu.KotlinLogging
import org.light.challenge.core.engine.api.ConditionEvaluationException
import org.light.challenge.core.engine.api.ConditionEvaluator
import org.light.challenge.core.engine.api.Facts
import org.springframework.expression.Expression
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import java.time.Duration

private val logger = KotlinLogging.logger {}

/**
 * Spring SpEL-based implementation of ConditionEvaluator.
 * Manages its own expression caching for better performance.
 */
class SpelConditionEvaluator private constructor(
    private val expressionCache: Cache<String, Expression>?,
    private val parser: ExpressionParser
) : ConditionEvaluator {

    // Default constructor with sensible caching defaults
    constructor() : this(
        expressionCache = createDefaultCache(),
        parser = SpelExpressionParser()
    )

    override fun evaluateBoolean(condition: String, facts: Facts): Boolean {
        return try {
            logger.debug { "Evaluating SpEL condition: $condition" }

            val compiledExpression = getCompiledExpression(condition)
            val evaluationContext = createEvaluationContext(facts)
            val result = compiledExpression.getValue(evaluationContext)

            when (result) {
                is Boolean -> result
                else -> throw ConditionEvaluationException(
                    "Condition '$condition' did not evaluate to a boolean value. Got: ${result?.javaClass?.simpleName}"
                )
            }
        } catch (e: ConditionEvaluationException) {
            throw e
        } catch (e: Exception) {
            logger.error(e) { "Error evaluating SpEL condition '$condition': ${e.message}" }
            throw ConditionEvaluationException("Failed to evaluate condition '$condition': ${e.message}", e)
        }
    }

    private fun getCompiledExpression(condition: String): Expression {
        return if (expressionCache != null) {
            expressionCache.get(condition) { parser.parseExpression(it) }
        } else {
            parser.parseExpression(condition)
        }
    }

    private fun createEvaluationContext(facts: Facts): StandardEvaluationContext {
        val evaluationContext = StandardEvaluationContext()

        // Set each fact as a variable in the evaluation context
        facts.toMap().forEach { (key, value) ->
            evaluationContext.setVariable(key, value)
        }

        return evaluationContext
    }

    companion object {
        /**
         * Creates a SpEL evaluator with caching disabled.
         */
        fun withoutCache(): SpelConditionEvaluator {
            return SpelConditionEvaluator(null, SpelExpressionParser())
        }

        /**
         * Creates a SpEL evaluator with custom cache settings.
         */
        fun withCache(
            maxSize: Int = 1000,
            expireAfter: Duration = Duration.ofMinutes(30)
        ): SpelConditionEvaluator {
            val cache = Caffeine.newBuilder()
                .maximumSize(maxSize.toLong())
                .expireAfterWrite(expireAfter)
                .recordStats()
                .build<String, Expression>()

            return SpelConditionEvaluator(cache, SpelExpressionParser())
        }

        private fun createDefaultCache(): Cache<String, Expression> {
            return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats()
                .build()
        }
    }
} 