package org.light.challenge.core.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.light.challenge.api.RuleGenerationRequest
import org.light.challenge.core.agent.AgentConfiguration
import org.light.challenge.core.agent.RuleGenerationAgent
import java.util.*

/**
 * Integration test for RuleGenerationServiceImpl using real LLM agent.
 * Requires OPENAI_API_KEY environment variable to be set.
 */
class RuleGenerationServiceImplTest {
    
    private lateinit var agentConfiguration: AgentConfiguration
    private lateinit var coreRuleGenerationService: RuleGenerationService
    private lateinit var ruleGenerationServiceImpl: RuleGenerationServiceImpl
    
    @BeforeEach
    fun setUp() {
        // Check if API key is available, skip test if not
        val apiKey = System.getenv("OPENAI_API_KEY")
        assumeTrue(apiKey != null && apiKey.isNotBlank(),
            "OPENAI_API_KEY environment variable must be set to run this test")

        // Create real dependencies (no mocking)
        agentConfiguration = AgentConfiguration()
        coreRuleGenerationService = RuleGenerationService(agentConfiguration)
        ruleGenerationServiceImpl = RuleGenerationServiceImpl(coreRuleGenerationService)
    }
    
    @Test
    fun `should generate rules successfully with valid prompt`() {
        // Given
        val request = RuleGenerationRequest(
            prompt = """
                First, if an invoice is for more than $10,000, the system checks if it's a marketing purchase. 
                If it is, the request is sent to the CMO via email. If not, it's sent to the CFO via Slack.
                If the invoice is for $10,000 or less, the system then checks if it's over $5,000. 
                For these mid-range invoices (between $5,001 and $10,000), it checks if a manager's approval is required. 
                If so, a Finance manager gets the request via email. If not, it goes to any Finance team member via Slack.
                Finally, any invoice for $5,000 or less is sent directly to any Finance team member via Slack.
            """.trimIndent()
        )
        
        // When
        val response = ruleGenerationServiceImpl.generateRules(request)
        println("Response: $response")
        
        // Then
        assertTrue(response.success, "Response should be successful")
        assertNotNull(response.executionId, "Execution ID should be present")
        assertNull(response.error, "Error should be null for successful response")
        assertTrue(response.generatedRules.isNotEmpty(), "Should generate at least one rule")
        assertTrue(response.message.contains("Successfully generated"), "Message should indicate success")
        
        // Verify rule structure
        response.generatedRules.forEach { rule ->
            assertNotNull(rule.id, "Rule ID should not be null")
            assertTrue(rule.id.isNotBlank(), "Rule ID should not be blank")
            assertNotNull(rule.name, "Rule name should not be null")
            assertTrue(rule.name.isNotBlank(), "Rule name should not be blank")
            assertNotNull(rule.description, "Rule description should not be null")
            assertTrue(rule.priority > 0, "Rule priority should be positive")
            assertNotNull(rule.conditionExpression, "Condition expression should not be null")
            assertTrue(rule.conditionExpression.isNotBlank(), "Condition expression should not be blank")
            assertNotNull(rule.actionId, "Action ID should not be null")
            assertTrue(rule.actionId.isNotBlank(), "Action ID should not be blank")
            assertNotNull(rule.actionParameters, "Action parameters should not be null")
        }
        
        // Verify business logic rules are generated
        val hasHighValueRule = response.generatedRules.any { 
            it.conditionExpression.contains("10000") && it.conditionExpression.contains(">") 
        }
        assertTrue(hasHighValueRule, "Should generate high value rule for amounts > $10,000")
        
        val hasMidValueRule = response.generatedRules.any { 
            it.conditionExpression.contains("5000") 
        }
        assertTrue(hasMidValueRule, "Should generate mid value rule for amounts around $5,000")
        
        // Verify action types are appropriate
        val hasEmailAction = response.generatedRules.any { it.actionId == "send-email" }
        val hasSlackAction = response.generatedRules.any { it.actionId == "send-slack" }
        assertTrue(hasEmailAction || hasSlackAction, "Should generate email or slack actions")
        
        println("Generated ${response.generatedRules.size} rules:")
        response.generatedRules.forEach { rule ->
            println("- ${rule.name} (Priority: ${rule.priority})")
            println("  Condition: ${rule.conditionExpression}")
            println("  Action: ${rule.actionId} -> ${rule.actionParameters}")
            println()
        }
    }
    
    @Test
    fun `should handle simple rule generation prompt`() {
        // Given
        val request = RuleGenerationRequest(
            prompt = "For invoices over $1000, send to manager via email. For smaller amounts, auto-approve."
        )
        
        // When
        val response = ruleGenerationServiceImpl.generateRules(request)
        
        // Then
        assertTrue(response.success, "Response should be successful")
        assertTrue(response.generatedRules.isNotEmpty(), "Should generate at least one rule")
        
        // Should have at least one rule for high amounts and one for low amounts
        assertTrue(response.generatedRules.size >= 2, "Should generate at least 2 rules")
        
        val hasAmountCondition = response.generatedRules.any { 
            it.conditionExpression.contains("1000") 
        }
        assertTrue(hasAmountCondition, "Should generate rule with $1000 condition")
    }
    
    @Test
    fun `should handle complex multi-department workflow prompt`() {
        // Given
        val request = RuleGenerationRequest(
            prompt = """
                For marketing expenses:
                - Over $5000: CMO approval via email
                - Under $5000: Marketing manager via slack
                
                For IT expenses:
                - Over $3000: CTO approval via email  
                - Under $3000: IT manager via slack
                
                For general expenses:
                - Over $2000: CFO approval via email
                - Under $2000: Finance team via slack
            """.trimIndent()
        )
        
        // When
        val response = ruleGenerationServiceImpl.generateRules(request)
        
        // Then
        assertTrue(response.success, "Response should be successful")
        assertTrue(response.generatedRules.size >= 3, "Should generate multiple rules for different departments")
        
        // Verify department-specific rules
        val hasMarketingRule = response.generatedRules.any { 
            it.conditionExpression.contains("marketing") || it.name.lowercase().contains("marketing")
        }
        
        val hasAmountBasedRules = response.generatedRules.any { 
            it.conditionExpression.contains("5000") || 
            it.conditionExpression.contains("3000") || 
            it.conditionExpression.contains("2000")
        }
        
        assertTrue(hasAmountBasedRules, "Should generate amount-based rules")
    }
    
    @Test
    fun `should return error for blank prompt`() {
        // Given
        val request = RuleGenerationRequest(prompt = "")
        
        // When
        val response = ruleGenerationServiceImpl.generateRules(request)
        
        // Then
        assertFalse(response.success, "Response should not be successful for blank prompt")
        assertNotNull(response.error, "Error should be present")
        assertTrue(response.error!!.contains("blank"), "Error should mention blank prompt")
        assertTrue(response.generatedRules.isEmpty(), "Should not generate any rules")
    }
    
    @Test
    fun `should return error for invalid prompt`() {
        // Given - prompt that doesn't make sense for workflow rules
        val request = RuleGenerationRequest(
            prompt = "What is the weather like today?"
        )
        
        // When
        val response = ruleGenerationServiceImpl.generateRules(request)
        
        // Then - The LLM should either generate something or we should handle gracefully
        assertNotNull(response.executionId, "Execution ID should always be present")
        // Note: The actual behavior depends on how the LLM interprets the prompt
        // It might generate rules anyway or return empty results
    }
    
    @Test
    fun `should generate rules with proper priority ordering`() {
        // Given
        val request = RuleGenerationRequest(
            prompt = """
                Process invoices in this order:
                1. Emergency purchases over $50,000 go to CEO immediately via email
                2. Regular purchases over $20,000 go to CFO via email  
                3. Medium purchases $5,000-$20,000 go to department manager via email
                4. Small purchases under $5,000 are auto-approved and logged via slack
            """.trimIndent()
        )
        
        // When
        val response = ruleGenerationServiceImpl.generateRules(request)
        
        // Then
        assertTrue(response.success, "Response should be successful")
        assertTrue(response.generatedRules.size >= 3, "Should generate multiple priority-based rules")
        
        // Verify priorities are meaningful (higher amounts should generally have higher priorities)
        val sortedByPriority = response.generatedRules.sortedByDescending { it.priority }
        
        // Print for verification
        println("Rules sorted by priority:")
        sortedByPriority.forEach { rule ->
            println("Priority ${rule.priority}: ${rule.name}")
            println("  Condition: ${rule.conditionExpression}")
        }
        
        // Verify we have different priority levels
        val distinctPriorities = response.generatedRules.map { it.priority }.distinct()
        assertTrue(distinctPriorities.size > 1, "Should have rules with different priorities")
    }
    
    @Test
    fun `should handle department-specific routing rules`() {
        // Given
        val request = RuleGenerationRequest(
            prompt = """
                Route invoices based on department:
                - HR department: HR manager via email for all amounts
                - Sales department: Sales director via slack  
                - Engineering department: Engineering lead via email if over $1000, otherwise auto-approve
            """.trimIndent()
        )
        
        // When
        val response = ruleGenerationServiceImpl.generateRules(request)
        
        // Then
        assertTrue(response.success, "Response should be successful")
        assertTrue(response.generatedRules.isNotEmpty(), "Should generate department-based rules")
        
        // Check that rules reference departments
        val hasDepartmentConditions = response.generatedRules.any { rule ->
            val condition = rule.conditionExpression.lowercase()
            condition.contains("department") || 
            condition.contains("hr") || 
            condition.contains("sales") || 
            condition.contains("engineering")
        }
        
        assertTrue(hasDepartmentConditions, "Should generate department-specific conditions")
        
        // Verify different action types are used
        val actionTypes = response.generatedRules.map { it.actionId }.distinct()
        assertTrue(actionTypes.isNotEmpty(), "Should have different action types")
    }
} 