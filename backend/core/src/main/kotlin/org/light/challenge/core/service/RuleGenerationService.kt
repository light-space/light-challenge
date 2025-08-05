package org.light.challenge.core.service

import org.light.challenge.core.agent.AgentConfiguration
import org.light.challenge.core.engine.api.Rule
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for generating workflow rules using LLM agent.
 */
@Singleton
class RuleGenerationService @Inject constructor(
    private val agentConfiguration: AgentConfiguration
) {

    private val ruleGenerationAgent by lazy {
        agentConfiguration.provideRuleGenerationAgent()
    }

    /**
     * Generates workflow rules based on user prompt.
     *
     * @param userPrompt The user's description of the workflow rules
     * @return List of generated workflow rules
     * @throws IllegalArgumentException if the prompt is blank
     * @throws Exception if rule generation fails
     */
    fun generateRules(userPrompt: String): List<Rule> {
        require(userPrompt.isNotBlank()) { "User prompt cannot be blank" }

        return try {
            ruleGenerationAgent.generateRules(userPrompt)
        } catch (e: Exception) {
            throw Exception("Failed to generate rules: ${e.message}", e)
        }
    }
} 