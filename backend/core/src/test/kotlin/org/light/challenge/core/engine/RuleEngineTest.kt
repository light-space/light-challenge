package org.light.challenge.core.engine

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.light.challenge.core.engine.api.Facts
import org.light.challenge.core.repository.RuleRepository
import org.light.challenge.core.service.Invoice
import org.light.challenge.core.service.WorkflowActionExecutor
import java.math.BigDecimal
import java.util.stream.Stream

class RuleEngineTest {

    private val actionExecutor = WorkflowActionExecutor.executor()
    private val ruleRepository = RuleRepository()

    @ParameterizedTest
    @MethodSource("stopAfterFirstMatchTestCases")
    fun `test stopAfterFirstMatch behavior`(
        stopAfterFirstMatch: Boolean,
        amount: BigDecimal,
        department: String,
        requiresManagerApproval: Boolean,
        expectedMatchCount: Int,
        expectedRuleIds: Set<String>,
        description: String
    ) {
        // Given
        val config = RuleEngineConfig.builder()
            .stopAfterFirstMatch(stopAfterFirstMatch)
            .build()
        val ruleEngine = RuleEngine(config)

        val invoice = Invoice(
            amount = amount,
            department = department,
            requiresManagerApproval = requiresManagerApproval
        )
        val facts = Facts().withFact("invoice", invoice)
        val rules = ruleRepository.getAllRules()

        // When
        val results = ruleEngine.evaluate(rules, facts, actionExecutor)

        // Then
        val matchedResults = results.filter { it.matched }
        assertEquals(
            expectedMatchCount,
            matchedResults.size,
            "Should match exactly $expectedMatchCount rules for: $description"
        )

        val actualRuleIds = matchedResults.map { it.rule.id }.toSet()
        assertEquals(
            expectedRuleIds,
            actualRuleIds,
            "Should match expected rule IDs for: $description"
        )

        // Verify all matched actions were successful
        matchedResults.forEach { result ->
            assertNotNull(
                result.actionResult,
                "Action result should not be null for rule: ${result.rule.id}"
            )
            assertTrue(
                result.actionResult!!.success,
                "Action should be successful for rule: ${result.rule.id}"
            )
        }
    }

    @ParameterizedTest
    @MethodSource("singleMatchInvoiceTestCases")
    fun `test invoice routing scenarios with single matches`(
        stopAfterFirstMatch: Boolean,
        amount: BigDecimal,
        department: String,
        requiresManagerApproval: Boolean,
        expectedRuleId: String,
        expectedMessageContains: String,
        description: String
    ) {
        // Given - These scenarios should have the same result regardless of stopAfterFirstMatch
        val config = RuleEngineConfig.builder()
            .stopAfterFirstMatch(stopAfterFirstMatch)
            .build()
        val ruleEngine = RuleEngine(config)

        val invoice = Invoice(
            amount = amount,
            department = department,
            requiresManagerApproval = requiresManagerApproval
        )
        val facts = Facts().withFact("invoice", invoice)
        val rules = ruleRepository.getAllRules()

        // When
        val results = ruleEngine.evaluate(rules, facts, actionExecutor)

        // Then
        val matchedResults = results.filter { it.matched }
        assertEquals(1, matchedResults.size, "Should match exactly 1 rule for: $description")

        val targetRule = matchedResults.find { it.rule.id == expectedRuleId }
        assertNotNull(targetRule, "Should match rule: $expectedRuleId for: $description")

        val actionResult = targetRule!!.actionResult
        assertNotNull(actionResult, "Action result should not be null for: $description")
        assertTrue(actionResult!!.success, "Action should be successful for: $description")
        assertTrue(
            actionResult.message.contains(expectedMessageContains),
            "Message should contain '$expectedMessageContains' for: $description. Actual: ${actionResult.message}"
        )
    }

    @Test
    fun `test rule evaluation with empty rules list`() {
        // Given
        val config = RuleEngineConfig.default()
        val ruleEngine = RuleEngine(config)

        val invoice = Invoice(
            amount = BigDecimal("15000"),
            department = "marketing",
            requiresManagerApproval = false
        )
        val facts = Facts().withFact("invoice", invoice)
        val emptyRules = emptyList<org.light.challenge.core.engine.api.Rule>()

        // When
        val results = ruleEngine.evaluate(emptyRules, facts, actionExecutor)

        // Then
        assertTrue(results.isEmpty(), "Should return empty results for empty rules")
    }

    @Test
    fun `test rule evaluation with invalid facts structure`() {
        // Given
        val config = RuleEngineConfig.default()
        val ruleEngine = RuleEngine(config)

        val facts = Facts().withFact("wrongKey", "someValue")
        val rules = ruleRepository.getAllRules()

        // When
        val results = ruleEngine.evaluate(rules, facts, actionExecutor)

        // Then
        val matchedResults = results.filter { it.matched }
        assertEquals(
            0,
            matchedResults.size,
            "Should not match any rules when facts don't contain expected invoice object"
        )

        // All results should have errors due to missing invoice fact
        val errorResults = results.filter { it.evaluationError != null }
        assertTrue(errorResults.isNotEmpty(), "Should have error results when facts are malformed")
    }

    companion object {
        @JvmStatic
        fun stopAfterFirstMatchTestCases(): Stream<Arguments> {
            return Stream.of(
                // High-value marketing invoice - matches 2 rules
                arguments(
                    false,
                    BigDecimal("15000"),
                    "marketing",
                    false,
                    2,
                    setOf(
                        "route-high-value-marketing-to-cmo",
                        "route-high-value-non-marketing-to-cfo"
                    ),
                    "High-value marketing (stopAfterFirstMatch=false, should match 2 rules)"
                ),
                arguments(
                    true, BigDecimal("15000"), "marketing", false,
                    1, setOf("route-high-value-marketing-to-cmo"),
                    "High-value marketing (stopAfterFirstMatch=true, should match 1 rule)"
                ),

                // Mid-value with manager approval - matches 2 rules
                arguments(
                    false,
                    BigDecimal("7500"),
                    "operations",
                    true,
                    2,
                    setOf(
                        "route-mid-value-manager-approval-to-finance-manager",
                        "route-mid-value-standard-to-finance-team"
                    ),
                    "Mid-value with manager approval (stopAfterFirstMatch=false, should match 2 rules)"
                ),
                arguments(
                    true, BigDecimal("7500"), "operations", true,
                    1, setOf("route-mid-value-manager-approval-to-finance-manager"),
                    "Mid-value with manager approval (stopAfterFirstMatch=true, should match 1 rule)"
                )
            )
        }

        @JvmStatic
        fun singleMatchInvoiceTestCases(): Stream<Arguments> {
            return Stream.of(
                // High value non-marketing - only matches 1 rule regardless of stopAfterFirstMatch
                arguments(
                    false,
                    BigDecimal("15000"),
                    "finance",
                    false,
                    "route-high-value-non-marketing-to-cfo",
                    "cfo_slack_id",
                    "High value non-marketing invoice (stopAfterFirstMatch=false)"
                ),
                arguments(
                    true,
                    BigDecimal("15000"),
                    "finance",
                    false,
                    "route-high-value-non-marketing-to-cfo",
                    "cfo_slack_id",
                    "High value non-marketing invoice (stopAfterFirstMatch=true)"
                ),

                // Mid value without manager approval - only matches 1 rule regardless of stopAfterFirstMatch
                arguments(
                    false,
                    BigDecimal("7500"),
                    "operations",
                    false,
                    "route-mid-value-standard-to-finance-team",
                    "finance_team_channel",
                    "Mid value invoice without manager approval (stopAfterFirstMatch=false)"
                ),
                arguments(
                    true,
                    BigDecimal("7500"),
                    "operations",
                    false,
                    "route-mid-value-standard-to-finance-team",
                    "finance_team_channel",
                    "Mid value invoice without manager approval (stopAfterFirstMatch=true)"
                ),

                // Low value - only matches 1 rule regardless of stopAfterFirstMatch
                arguments(
                    false,
                    BigDecimal("3000"),
                    "hr",
                    false,
                    "route-low-value-to-finance-team",
                    "finance_team_channel",
                    "Low value invoice (stopAfterFirstMatch=false)"
                ),
                arguments(
                    true,
                    BigDecimal("3000"),
                    "hr",
                    false,
                    "route-low-value-to-finance-team",
                    "finance_team_channel",
                    "Low value invoice (stopAfterFirstMatch=true)"
                )
            )
        }
    }
} 