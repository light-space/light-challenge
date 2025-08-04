package org.light.challenge.core.service

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.light.challenge.core.engine.api.Action
import org.light.challenge.core.engine.api.Facts

class WorkflowActionExecutorTest {

    private val actionExecutor = WorkflowActionExecutor.executor()

    @Test
    fun `should execute send-email action successfully`() {
        // Given
        val action = Action(
            id = "send-email",
            parameters = mapOf(
                "recipient" to "test@example.com",
                "subject" to "Test Subject",
                "body" to "Test email body"
            )
        )
        val facts = Facts().withFact("invoice", "test-invoice")

        // When
        val result = actionExecutor.execute(action, facts)

        // Then
        assertTrue(result.success)
        assertTrue(result.message.contains("test@example.com"))
        assertTrue(result.message.contains("Test Subject"))
        assertTrue(result.message.contains("Test email body"))
    }

    @Test
    fun `should execute send-slack action successfully`() {
        // Given
        val action = Action(
            id = "send-slack",
            parameters = mapOf(
                "recipient" to "slack_user_123",
                "message" to "Approval required for invoice"
            )
        )
        val facts = Facts().withFact("invoice", "test-invoice")

        // When
        val result = actionExecutor.execute(action, facts)

        // Then
        assertTrue(result.success)
        assertTrue(result.message.contains("slack_user_123"))
        assertTrue(result.message.contains("Approval required for invoice"))
    }

    @Test
    fun `should fail when email action has missing parameters`() {
        // Given
        val action = Action(
            id = "send-email",
            parameters = mapOf(
                "recipient" to "test@example.com"
                // Missing subject and body
            )
        )
        val facts = Facts()

        // When
        val result = actionExecutor.execute(action, facts)

        // Then
        assertFalse(result.success)
        assertTrue(result.message.contains("Missing required email parameters"))
    }

    @Test
    fun `should fail when slack action has missing parameters`() {
        // Given
        val action = Action(
            id = "send-slack",
            parameters = mapOf(
                "recipient" to "slack_user_123"
                // Missing message
            )
        )
        val facts = Facts()

        // When
        val result = actionExecutor.execute(action, facts)

        // Then
        assertFalse(result.success)
        assertTrue(result.message.contains("Missing required Slack parameters"))
    }

    @Test
    fun `should fail when action is not registered`() {
        // Given
        val action = Action(
            id = "unknown-action",
            parameters = mapOf("param" to "value")
        )
        val facts = Facts()

        // When
        val result = actionExecutor.execute(action, facts)

        // Then
        assertFalse(result.success)
        assertTrue(result.message.contains("No executor registered for action: unknown-action"))
    }
} 