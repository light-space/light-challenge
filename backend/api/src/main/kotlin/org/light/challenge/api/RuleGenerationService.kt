package org.light.challenge.api

/**
 * Interface for the rule generation service that uses LLM to generate workflow rules.
 */
interface RuleGenerationService {
    
    /**
     * Generates workflow rules based on user prompt.
     * 
     * @param request The rule generation request containing the user prompt
     * @return The rule generation response with generated rules
     */
    fun generateRules(request: RuleGenerationRequest): RuleGenerationResponse
} 