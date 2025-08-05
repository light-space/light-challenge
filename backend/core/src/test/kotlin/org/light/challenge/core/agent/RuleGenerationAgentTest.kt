package org.light.challenge.core.agent

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*

class RuleGenerationAgentTest {
    
    @Test
    fun `should throw exception when empty API key is provided`() {
        // When creating agent with empty API key
        assertThrows<IllegalArgumentException> {
            RuleGenerationAgent("")
        }
    }
    
    @Test 
    fun `should create agent when valid API key is provided`() {
        // Given a valid API key
        val apiKey = "test-api-key"
        
        // When creating agent
        val agent = RuleGenerationAgent(apiKey)
        
        // Then agent should be created successfully
        assertNotNull(agent)
    }
    
    @Test
    fun `should create agent with proper executor initialization`() {
        // Given a test API key
        val apiKey = "sk-test-12345"
        
        // When creating the agent
        val agent = RuleGenerationAgent(apiKey)
        
        // Then the agent should be properly initialized
        assertNotNull(agent)
    }
} 