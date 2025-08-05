package org.light.challenge.core.agent

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configuration class for LLM agents.
 * Provides the RuleGenerationAgent with proper API key injection.
 */
@Singleton
class AgentConfiguration @Inject constructor() {

    /**
     * Creates and provides the RuleGenerationAgent.
     * The OpenAI API key should be provided via environment variable OPENAI_API_KEY.
     */
    fun provideRuleGenerationAgent(): RuleGenerationAgent {
        val openAiApiKey = System.getenv("OPENAI_API_KEY")
            ?: throw IllegalStateException("OPENAI_API_KEY environment variable is required")

        return RuleGenerationAgent(openAiApiKey)
    }
} 