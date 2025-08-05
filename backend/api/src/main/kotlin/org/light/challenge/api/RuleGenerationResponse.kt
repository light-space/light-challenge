package org.light.challenge.api

import java.util.*

/**
 * Generated rule information for the response.
 */
data class GeneratedRuleInfo(
    val id: String,
    val name: String,
    val description: String,
    val priority: Int,
    val conditionExpression: String,
    val actionId: String,
    val actionParameters: Map<String, Any>
)

/**
 * Response DTO for the rule generation endpoint.
 */
data class RuleGenerationResponse(
    val success: Boolean,
    val message: String,
    val generatedRules: List<GeneratedRuleInfo> = emptyList(),
    val executionId: UUID = UUID.randomUUID(),
    val error: String? = null
) 