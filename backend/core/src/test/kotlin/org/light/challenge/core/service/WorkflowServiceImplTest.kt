package org.light.challenge.core.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.light.challenge.api.WorkflowErrorType
import org.light.challenge.api.WorkflowRequest
import org.light.challenge.core.engine.RuleEngine
import org.light.challenge.core.engine.api.*
import org.light.challenge.core.repository.RuleRepository
import java.math.BigDecimal
import java.util.*

class WorkflowServiceImplTest {

    private val mockRuleRepository = mockk<RuleRepository>()
    private val mockRuleEngine = mockk<RuleEngine>()
    private val mockActionExecutor = mockk<ActionExecutor>()

    private val workflowService = WorkflowServiceImpl(
        ruleRepository = mockRuleRepository,
        ruleEngine = mockRuleEngine,
        actionExecutor = mockActionExecutor
    )

    @Test
    fun `should execute workflow successfully when rule matches and action succeeds`() {
        // Given
        val request = WorkflowRequest(
            amount = BigDecimal("15000"),
            department = "marketing",
            requiresManagerApproval = false
        )

        val rule = Rule(
            id = "test-rule",
            name = "Test Rule",
            description = "Test rule description",
            priority = 100,
            conditionExpression = "invoice.amount > 10000",
            action = Action(
                id = "send-email",
                parameters = mapOf("recipient" to "test@example.com")
            )
        )

        val actionResult = ActionResult.success("Email sent successfully")
        val ruleResult = RuleResult(
            id = UUID.randomUUID(),
            rule = rule,
            matched = true,
            evaluationError = null,
            actionResult = actionResult
        )

        every { mockRuleRepository.getAllRules() } returns listOf(rule)
        every { mockRuleEngine.evaluate(any(), any<Facts>(), any()) } returns listOf(ruleResult)

        // When
        val response = workflowService.executeWorkflow(request)

        // Then
        assertTrue(response.success)
        assertEquals("Email sent successfully", response.message)
        assertNotNull(response.rule)
        assertEquals("test-rule", response.rule!!.ruleId)
        assertEquals("Test Rule", response.rule!!.ruleName)
        assertNull(response.error)
        assertNotNull(response.tracing.workflowExecutionId)
        assertEquals(ruleResult.id, response.tracing.ruleResultId)

        verify { mockRuleRepository.getAllRules() }
        verify { mockRuleEngine.evaluate(any(), any<Facts>(), mockActionExecutor) }
    }

    @Test
    fun `should return failure response when rule matches but action fails`() {
        // Given
        val request = WorkflowRequest(
            amount = BigDecimal("15000"),
            department = "marketing",
            requiresManagerApproval = false
        )

        val rule = Rule(
            id = "test-rule",
            name = "Test Rule",
            description = "Test rule description",
            priority = 100,
            conditionExpression = "invoice.amount > 10000",
            action = Action(
                id = "send-email",
                parameters = mapOf("recipient" to "test@example.com")
            )
        )

        val actionResult = ActionResult.failure("Email sending failed")
        val ruleResult = RuleResult(
            id = UUID.randomUUID(),
            rule = rule,
            matched = true,
            evaluationError = null,
            actionResult = actionResult
        )

        every { mockRuleRepository.getAllRules() } returns listOf(rule)
        every { mockRuleEngine.evaluate(any(), any<Facts>(), any()) } returns listOf(ruleResult)

        // When
        val response = workflowService.executeWorkflow(request)

        // Then
        assertFalse(response.success)
        assertEquals("Rule matched but action failed: Email sending failed", response.message)
        assertNotNull(response.rule)
        assertEquals("test-rule", response.rule!!.ruleId)
        assertEquals("Test Rule", response.rule!!.ruleName)
        assertNotNull(response.error)
        assertEquals(WorkflowErrorType.SYSTEM_ERROR, response.error!!.type)
        assertNotNull(response.tracing.workflowExecutionId)
        assertEquals(ruleResult.id, response.tracing.ruleResultId)
    }

    @Test
    fun `should return failure response when no rules match`() {
        // Given
        val request = WorkflowRequest(
            amount = BigDecimal("100"),
            department = "unknown",
            requiresManagerApproval = false
        )

        val rule = Rule(
            id = "test-rule",
            name = "Test Rule",
            description = "Test rule description",
            priority = 100,
            conditionExpression = "invoice.amount > 10000",
            action = Action(
                id = "send-email",
                parameters = mapOf("recipient" to "test@example.com")
            )
        )

        val ruleResult = RuleResult(
            id = UUID.randomUUID(),
            rule = rule,
            matched = false,
            evaluationError = null,
            actionResult = null
        )

        every { mockRuleRepository.getAllRules() } returns listOf(rule)
        every { mockRuleEngine.evaluate(any(), any<Facts>(), any()) } returns listOf(ruleResult)

        // When
        val response = workflowService.executeWorkflow(request)

        // Then
        assertFalse(response.success)
        assertEquals("No rules matched the invoice criteria", response.message)
        assertNull(response.rule)
        assertNotNull(response.error)
        assertEquals(WorkflowErrorType.NO_RULES_MATCHED, response.error!!.type)
        assertNotNull(response.tracing.workflowExecutionId)
        assertNull(response.tracing.ruleResultId)
    }

    @Test
    fun `should return error response when rule evaluation throws exception`() {
        // Given
        val request = WorkflowRequest(
            amount = BigDecimal("15000"),
            department = "marketing",
            requiresManagerApproval = false
        )

        every { mockRuleRepository.getAllRules() } throws RuntimeException("Database connection failed")

        // When
        val response = workflowService.executeWorkflow(request)

        // Then
        assertFalse(response.success)
        assertTrue(response.message.contains("Error executing workflow: Database connection failed"))
        assertNull(response.rule)
        assertNotNull(response.error)
        assertEquals(WorkflowErrorType.SYSTEM_ERROR, response.error!!.type)
        assertEquals("Database connection failed", response.error!!.details)
        assertNotNull(response.tracing.workflowExecutionId)
        assertNull(response.tracing.ruleResultId)
    }

    @Test
    fun `should handle rule evaluation error with error message in result`() {
        // Given
        val request = WorkflowRequest(
            amount = BigDecimal("15000"),
            department = "marketing",
            requiresManagerApproval = false
        )

        val rule = Rule(
            id = "test-rule",
            name = "Test Rule",
            description = "Test rule description",
            priority = 100,
            conditionExpression = "invalid.expression",
            action = Action(
                id = "send-email",
                parameters = mapOf("recipient" to "test@example.com")
            )
        )

        val ruleResult = RuleResult(
            id = UUID.randomUUID(),
            rule = rule,
            matched = false,  // Rule evaluation failed
            evaluationError = "SpEL compilation error",
            actionResult = null
        )

        every { mockRuleRepository.getAllRules() } returns listOf(rule)
        every { mockRuleEngine.evaluate(any(), any<Facts>(), any()) } returns listOf(ruleResult)

        // When
        val response = workflowService.executeWorkflow(request)

        // Then
        assertFalse(response.success)
        assertEquals("Rule evaluation failed: SpEL compilation error", response.message)
        assertNotNull(response.rule)
        assertEquals("test-rule", response.rule!!.ruleId)
        assertEquals("Test Rule", response.rule!!.ruleName)
        assertNotNull(response.error)
        assertEquals(WorkflowErrorType.SYSTEM_ERROR, response.error!!.type)
        assertEquals("Spel compilation error", response.error!!.details)
        assertNotNull(response.tracing.workflowExecutionId)
        assertEquals(ruleResult.id, response.tracing.ruleResultId)
    }

    @Test
    fun `should create facts correctly from workflow request`() {
        // Given
        val request = WorkflowRequest(
            amount = BigDecimal("15000"),
            department = "marketing",
            requiresManagerApproval = true
        )

        val rule = Rule(
            id = "test-rule",
            name = "Test Rule",
            description = "Test rule description",
            priority = 100,
            conditionExpression = "invoice.amount > 10000",
            action = Action(
                id = "send-email",
                parameters = mapOf("recipient" to "test@example.com")
            )
        )

        val actionResult = ActionResult.success("Email sent successfully")
        val ruleResult = RuleResult(
            id = UUID.randomUUID(),
            rule = rule,
            matched = true,
            evaluationError = null,
            actionResult = actionResult
        )

        every { mockRuleRepository.getAllRules() } returns listOf(rule)
        every { mockRuleEngine.evaluate(any(), any<Facts>(), any()) } returns listOf(ruleResult)

        // When
        workflowService.executeWorkflow(request)

        // Then
        verify {
            mockRuleEngine.evaluate(
                any(),
                match<Facts> { facts ->
                    val invoice = facts.getFact("invoice") as Invoice
                    invoice.amount == BigDecimal("15000") &&
                            invoice.department == "marketing" && invoice.requiresManagerApproval
                },
                mockActionExecutor
            )
        }
    }

    // === VALIDATION TESTS - Now handled by Invoice constructor ===

    @Test
    fun `should return validation error for negative amount`() {
        // Given
        val request = WorkflowRequest(
            amount = BigDecimal("-100"),
            department = "marketing",
            requiresManagerApproval = false
        )

        // When
        val response = workflowService.executeWorkflow(request)

        // Then
        assertFalse(response.success)
        assertTrue(response.message.contains("Invalid request"))
        assertTrue(response.message.contains("must be positive"))
        assertNull(response.rule)
        assertNotNull(response.error)
        assertEquals(WorkflowErrorType.VALIDATION_ERROR, response.error!!.type)
        assertTrue(response.error!!.details!!.contains("Amount must be positive"))
        assertNotNull(response.tracing.workflowExecutionId)
        assertNull(response.tracing.ruleResultId)
    }

    @Test
    fun `should return validation error for zero amount`() {
        // Given
        val request = WorkflowRequest(
            amount = BigDecimal.ZERO,
            department = "marketing",
            requiresManagerApproval = false
        )

        // When
        val response = workflowService.executeWorkflow(request)

        // Then
        assertFalse(response.success)
        assertTrue(response.message.contains("Invalid request"))
        assertTrue(response.message.contains("must be positive"))
        assertNull(response.rule)
        assertNotNull(response.error)
        assertEquals(WorkflowErrorType.VALIDATION_ERROR, response.error!!.type)
        assertTrue(response.error!!.details!!.contains("Amount must be positive"))
    }

    @Test
    fun `should return validation error for blank department`() {
        // Given
        val request = WorkflowRequest(
            amount = BigDecimal("1000"),
            department = "   ",
            requiresManagerApproval = false
        )

        // When
        val response = workflowService.executeWorkflow(request)

        // Then
        assertFalse(response.success)
        assertTrue(response.message.contains("Invalid request"))
        assertTrue(response.message.contains("Department cannot be blank"))
        assertNull(response.rule)
        assertNotNull(response.error)
        assertEquals(WorkflowErrorType.VALIDATION_ERROR, response.error!!.type)
        assertTrue(response.error!!.details!!.contains("Department cannot be blank"))
    }

    @Test
    fun `should return validation error for too long department name`() {
        // Given
        val longDepartment = "a".repeat(101)
        val request = WorkflowRequest(
            amount = BigDecimal("1000"),
            department = longDepartment,
            requiresManagerApproval = false
        )

        // When
        val response = workflowService.executeWorkflow(request)

        // Then
        assertFalse(response.success)
        assertTrue(response.message.contains("Invalid request"))
        assertTrue(response.message.contains("too long"))
        assertNull(response.rule)
        assertNotNull(response.error)
        assertEquals(WorkflowErrorType.VALIDATION_ERROR, response.error!!.type)
        assertTrue(response.error!!.details!!.contains("Department name too long"))
    }

    @Test
    fun `should validate Invoice creation directly`() {
        // Test valid invoice
        assertDoesNotThrow {
            Invoice(BigDecimal("1000"), "marketing", false)
        }

        // Test invalid amount
        val negativeAmountException = assertThrows(IllegalArgumentException::class.java) {
            Invoice(BigDecimal("-100"), "marketing", false)
        }
        assertTrue(negativeAmountException.message!!.contains("Amount must be positive"))

        // Test zero amount
        val zeroAmountException = assertThrows(IllegalArgumentException::class.java) {
            Invoice(BigDecimal.ZERO, "marketing", false)
        }
        assertTrue(zeroAmountException.message!!.contains("Amount must be positive"))

        // Test blank department
        val blankDeptException = assertThrows(IllegalArgumentException::class.java) {
            Invoice(BigDecimal("1000"), "   ", false)
        }
        assertTrue(blankDeptException.message!!.contains("Department cannot be blank"))

        // Test long department
        val longDeptException = assertThrows(IllegalArgumentException::class.java) {
            Invoice(BigDecimal("1000"), "a".repeat(101), false)
        }
        assertTrue(longDeptException.message!!.contains("Department name too long"))
    }
} 