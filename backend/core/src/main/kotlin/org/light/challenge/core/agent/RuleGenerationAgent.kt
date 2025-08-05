package org.light.challenge.core.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeLLMRequestStructured
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.structure.json.JsonStructuredData
import ai.koog.prompt.text.text
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.light.challenge.core.agent.models.LLMRules
import org.light.challenge.core.agent.models.LLMRule
import org.light.challenge.core.agent.models.LLMAction
import org.light.challenge.core.engine.api.Rule
import org.light.challenge.core.engine.api.Action

/**
 * Internal request data class for rule generation agent
 */
@Serializable
@SerialName("AgentRuleGenerationRequest")
data class AgentRuleGenerationRequest(
    val prompt: String
)

/**
 * LLM agent for generating workflow rules based on user prompts.
 * Uses Koog AI framework with proper structured data generation following the working example pattern.
 */
class RuleGenerationAgent(
    private val openAiApiKey: String
) {

    // Create example rules to help the LLM understand the format
    private val exampleRules = listOf(
        LLMRule(
            id = "route-high-value-marketing-to-cmo",
            name = "Route High-Value Marketing Invoice to CMO",
            description = "Routes invoices over $10,000 for marketing purposes to the CMO via email",
            priority = 100,
            conditionExpression = "invoice.amount > 10000 && invoice.department == 'marketing'",
            action = LLMAction(
                id = "send-email",
                parameters = mapOf(
                    "recipient" to "cmo@example.com",
                    "subject" to "High-Value Invoice Approval Request",
                    "body" to "A new marketing invoice over $10,000 requires your approval."
                )
            )
        ),
        LLMRule(
            id = "route-low-value-to-finance-team",
            name = "Route Low-Value Invoice to Finance Team",
            description = "Routes invoices for $5,000 or less directly to the Finance team via Slack",
            priority = 60,
            conditionExpression = "invoice.amount <= 5000",
            action = LLMAction(
                id = "send-slack",
                parameters = mapOf(
                    "recipient" to "finance_team_channel",
                    "message" to "New low-value invoice ready for processing."
                )
            )
        ),
        LLMRule(
            id = "route-mid-value-manager-approval",
            name = "Route Mid-Value Invoice with Manager Approval",
            description = "Routes invoices between $5,001 and $10,000 requiring manager approval to Finance Manager",
            priority = 80,
            conditionExpression = "invoice.amount > 5000 && invoice.amount <= 10000 && invoice.requiresManagerApproval == true",
            action = LLMAction(
                id = "send-email",
                parameters = mapOf(
                    "recipient" to "finance.managers@example.com",
                    "subject" to "Mid-Value Invoice Approval Request",
                    "body" to "A new invoice requiring manager approval has been submitted."
                )
            )
        )
    )

    // Generate JSON Schema for structured data
    private val rulesStructure = JsonStructuredData.createJsonStructure<LLMRules>(
        examples = listOf(LLMRules(exampleRules))
    )

    // Define the agent strategy
    private val agentStrategy = strategy<AgentRuleGenerationRequest, LLMRules>("rule-generation") {
        val prepareRequest by node<AgentRuleGenerationRequest, String> { request ->
            text {
                +"Generate workflow rules based on the following user prompt:"
                +""
                +"User Prompt: ${request.prompt}"
                +""
                +"Please generate appropriate workflow rules that match the described business logic."
            }
        }

        val getStructuredRules by nodeLLMRequestStructured(
            structure = rulesStructure,
            retries = 3,
            // Use GPT-4o for fixing malformed outputs
            fixingModel = OpenAIModels.Chat.GPT4o,
        )

        nodeStart then prepareRequest then getStructuredRules
        edge(getStructuredRules forwardTo nodeFinish transformed { it.getOrThrow().structure })
    }

    // Agent configuration
    private val agentConfig = AIAgentConfig(
        prompt = prompt("rule-generation-prompt") {
            system(buildSystemPrompt())
        },
        model = OpenAIModels.Chat.GPT4o,
        maxAgentIterations = 5
    )

    // Create the AI agent
    private val runner = AIAgent<AgentRuleGenerationRequest, LLMRules>(
        promptExecutor = MultiLLMPromptExecutor(
            LLMProvider.OpenAI to OpenAILLMClient(openAiApiKey)
        ),
        strategy = agentStrategy,
        agentConfig = agentConfig
    )

    /**
     * Generates workflow rules based on user prompt.
     *
     * @param userPrompt The user's description of the workflow rules
     * @return List of generated workflow rules
     */
    fun generateRules(userPrompt: String): List<Rule> = runBlocking {
        require(userPrompt.isNotBlank()) { "User prompt cannot be blank" }

        val result: LLMRules = runner.run(AgentRuleGenerationRequest(userPrompt))

        // Convert LLM models to domain models
        return@runBlocking result.rules.map { llmRule ->
            Rule(
                id = llmRule.id,
                name = llmRule.name,
                description = llmRule.description,
                priority = llmRule.priority,
                conditionExpression = llmRule.conditionExpression,
                action = Action(
                    id = llmRule.action.id,
                    parameters = llmRule.action.parameters.mapValues { it.value as Any }
                )
            )
        }
    }

    private fun buildSystemPrompt(): String {
        return """
            You are an expert workflow automation assistant specialized in generating business rules for invoice processing workflows.
            
            Your task is to analyze user prompts describing business logic and convert them into structured workflow rules.
            
            Guidelines for generating rules:
            1. Each rule should have a unique, descriptive ID (kebab-case)
            2. Rule names should be clear and descriptive
            3. Priorities should reflect the order of evaluation (higher values = higher priority)
            4. Condition expressions should use proper syntax for invoice properties:
               - invoice.amount (BigDecimal)
               - invoice.department (String) 
               - invoice.requiresManagerApproval (Boolean)
            5. Use proper comparison operators: >, <, >=, <=, ==, !=, &&, ||
            6. Actions should use standard communication channels:
               - "send-email" for email notifications
               - "send-slack" for Slack notifications
            
            Available action types and their required parameters:
            - send-email: recipient, subject, body
            - send-slack: recipient, message
            
            Common recipients:
            - cmo@example.com (Chief Marketing Officer)
            - cfo@example.com (Chief Financial Officer) 
            - finance.managers@example.com (Finance Managers)
            - cfo_slack_id (CFO's Slack ID)
            - finance_team_channel (Finance Team Slack Channel)
            
            Example condition expressions:
            - "invoice.amount > 10000 && invoice.department == 'marketing'"
            - "invoice.amount > 5000 && invoice.amount <= 10000 && invoice.requiresManagerApproval == true"
            - "invoice.amount <= 5000"
            
            Always generate rules that accurately reflect the business logic described in the user prompt.
            Return the rules in the specified JSON structure format.
        """.trimIndent()
    }
} 