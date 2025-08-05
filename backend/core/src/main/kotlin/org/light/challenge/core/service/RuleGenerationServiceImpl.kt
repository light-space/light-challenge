package org.light.challenge.core.service

import org.light.challenge.api.GeneratedRuleInfo
import org.light.challenge.api.RuleGenerationRequest
import org.light.challenge.api.RuleGenerationResponse
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import org.light.challenge.api.RuleGenerationService as ApiRuleGenerationService

/**
 * Implementation of the rule generation service that uses LLM agent.
 */
@Singleton
class RuleGenerationServiceImpl @Inject constructor(
    private val coreRuleGenerationService: RuleGenerationService
) : ApiRuleGenerationService {

    override fun generateRules(request: RuleGenerationRequest): RuleGenerationResponse {
        return try {
            val generatedRules = coreRuleGenerationService.generateRules(request.prompt)

            val ruleInfos = generatedRules.map { rule ->
                GeneratedRuleInfo(
                    id = rule.id,
                    name = rule.name,
                    description = rule.description ?: "",
                    priority = rule.priority,
                    conditionExpression = rule.conditionExpression,
                    actionId = rule.action.id,
                    actionParameters = rule.action.parameters
                )
            }

            RuleGenerationResponse(
                success = true,
                message = "Successfully generated ${generatedRules.size} workflow rules",
                generatedRules = ruleInfos,
                executionId = UUID.randomUUID()
            )

        } catch (e: IllegalArgumentException) {
            RuleGenerationResponse(
                success = false,
                message = "Invalid request",
                error = e.message,
                executionId = UUID.randomUUID()
            )
        } catch (e: Exception) {
            RuleGenerationResponse(
                success = false,
                message = "Failed to generate rules",
                error = e.message,
                executionId = UUID.randomUUID()
            )
        }
    }
} 