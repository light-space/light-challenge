package org.light.challenge.api

/**
 * Request DTO for the rule generation endpoint.
 */
data class RuleGenerationRequest(
    val prompt: String
) {
    init {
        require(prompt.isNotBlank()) { "Prompt cannot be blank" }
    }
} 