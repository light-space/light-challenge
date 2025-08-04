package org.light.challenge.core.repository

import org.light.challenge.core.engine.api.Action
import org.light.challenge.core.engine.api.Rule

/**
 * Provides default workflow rules for invoice processing.
 */
class DefaultRuleProvider {

    /**
     * Returns the default set of workflow rules.
     */
    fun getDefaultRules(): List<Rule> {
        return listOf(
            createHighValueMarketingRule(),
            createHighValueNonMarketingRule(),
            createMidValueManagerApprovalRule(),
            createMidValueStandardRule(),
            createLowValueRule()
        )
    }

    private fun createHighValueMarketingRule(): Rule {
        return Rule(
            id = "route-high-value-marketing-to-cmo",
            name = "Route High-Value Marketing Invoice to CMO",
            description = "Routes invoices over $10,000 for marketing purposes to the CMO via email.",
            priority = 100,
            conditionExpression = "invoice.amount > 10000.23555B && invoice.department == 'marketing'",
            action = Action(
                id = "send-email",
                parameters = mapOf(
                    "recipient" to "cmo@example.com",
                    "subject" to "High-Value Invoice Approval Request",
                    "body" to "A new marketing invoice over $10,000 requires your approval."
                )
            )
        )
    }

    private fun createHighValueNonMarketingRule(): Rule {
        return Rule(
            id = "route-high-value-non-marketing-to-cfo",
            name = "Route High-Value Non-Marketing Invoice to CFO",
            description = "Routes non-marketing invoices over $10,000 to the CFO via Slack.",
            priority = 90,
            conditionExpression = "invoice.amount > 10000",
            action = Action(
                id = "send-slack",
                parameters = mapOf(
                    "recipient" to "cfo_slack_id",
                    "message" to "A new non-marketing invoice over $10,000 requires your approval."
                )
            )
        )
    }

    private fun createMidValueManagerApprovalRule(): Rule {
        return Rule(
            id = "route-mid-value-manager-approval-to-finance-manager",
            name = "Route Mid-Value Invoice with Manager Approval",
            description = "Routes invoices between $5,001 and $10,000 requiring manager approval to a Finance Manager via email.",
            priority = 80,
            conditionExpression = "invoice.amount > 5000 && invoice.amount <= 10000 && invoice.requiresManagerApproval == true",
            action = Action(
                id = "send-email",
                parameters = mapOf(
                    "recipient" to "finance.managers@example.com",
                    "subject" to "Mid-Value Invoice Approval Request",
                    "body" to "A new invoice requiring manager approval has been submitted."
                )
            )
        )
    }

    private fun createMidValueStandardRule(): Rule {
        return Rule(
            id = "route-mid-value-standard-to-finance-team",
            name = "Route Mid-Value Standard Invoice to Finance Team",
            description = "Routes invoices between $5,001 and $10,000 not requiring manager approval to the Finance team via Slack.",
            priority = 70,
            conditionExpression = "invoice.amount > 5000 && invoice.amount <= 10000",
            action = Action(
                id = "send-slack",
                parameters = mapOf(
                    "recipient" to "finance_team_channel",
                    "message" to "New mid-value invoice ready for processing."
                )
            )
        )
    }

    private fun createLowValueRule(): Rule {
        return Rule(
            id = "route-low-value-to-finance-team",
            name = "Route Low-Value Invoice to Finance Team",
            description = "Routes invoices for $5,000 or less directly to the Finance team via Slack.",
            priority = 60,
            conditionExpression = "invoice.amount <= 5000",
            action = Action(
                id = "send-slack",
                parameters = mapOf(
                    "recipient" to "finance_team_channel",
                    "message" to "New low-value invoice ready for processing."
                )
            )
        )
    }
} 