package org.light.challenge.core.service

import mu.KotlinLogging
import org.light.challenge.api.*
import org.light.challenge.core.engine.RuleEngine
import org.light.challenge.core.engine.RuleEngineConfig
import org.light.challenge.core.engine.api.ActionExecutor
import org.light.challenge.core.engine.api.Facts
import org.light.challenge.core.engine.api.RuleResult
import org.light.challenge.core.repository.RuleRepository
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Implementation of the workflow service that orchestrates workflow execution.
 */
class WorkflowServiceImpl(
    private val ruleRepository: RuleRepository = RuleRepository(),
    private val ruleEngine: RuleEngine = RuleEngine(RuleEngineConfig.default()),
    private val actionExecutor: ActionExecutor = WorkflowActionExecutor.executor()
) : WorkflowService {

    override fun executeWorkflow(request: WorkflowRequest): WorkflowResponse {
        val workflowExecutionId = UUID.randomUUID()

        logger.info { "Executing workflow [${workflowExecutionId}] for invoice: amount=${request.amount}, department=${request.department}, requiresManagerApproval=${request.requiresManagerApproval}" }

        return try {
            // Invoice validation happens here - will throw IllegalArgumentException if invalid
            val facts = createFacts(request)
            val rules = ruleRepository.getAllRules()
            val results = ruleEngine.evaluate(rules, facts, actionExecutor)

            logger.info { "Workflow [${workflowExecutionId}] completed with ${results.size} rule evaluations" }
            // we can save results for debugging and tracing.

            createResponse(results, workflowExecutionId)
        } catch (e: IllegalArgumentException) {
            // Validation errors from Invoice creation
            logger.warn { "Workflow [${workflowExecutionId}] validation failed: ${e.message}" }
            createValidationErrorResponse(e.message ?: "Invalid request data", workflowExecutionId)
        } catch (e: Exception) {
            logger.error(e) { "Error executing workflow [${workflowExecutionId}]: ${e.message}" }
            createSystemErrorResponse(e.message ?: "Unknown error", workflowExecutionId)
        }
    }

    /**
     * Creates facts from the workflow request.
     * Invoice validation occurs here and throws IllegalArgumentException if invalid.
     */
    private fun createFacts(request: WorkflowRequest): Facts {
        val invoice = Invoice(
            amount = request.amount,
            department = request.department,
            requiresManagerApproval = request.requiresManagerApproval
        )

        return Facts().withFact("invoice", invoice)
    }

    /**
     * Creates workflow response from rule results.
     */
    private fun createResponse(
        results: List<RuleResult>,
        workflowExecutionId: UUID
    ): WorkflowResponse {
        val successfulResult =
            results.firstOrNull { it.matched && it.actionResult?.success == true }

        return when {
            successfulResult != null -> {
                logger.info { "Workflow [${workflowExecutionId}] succeeded with rule [${successfulResult.rule.id}] result [${successfulResult.id}]" }
                WorkflowResponse(
                    success = true,
                    message = successfulResult.actionResult?.message
                        ?: "Action executed successfully",
                    tracing = TracingInfo(
                        workflowExecutionId = workflowExecutionId,
                        ruleResultId = successfulResult.id
                    ),
                    rule = RuleInfo(
                        ruleId = successfulResult.rule.id,
                        ruleName = successfulResult.rule.name
                    )
                )
            }

            results.any { it.matched } -> {
                val failedResult = results.first { it.matched }
                logger.error { "Workflow [${workflowExecutionId}] failed with rule [${failedResult.rule.id}] result [${failedResult.id}]: ${failedResult.error ?: failedResult.actionResult?.message}" }
                WorkflowResponse(
                    success = false,
                    message = failedResult.actionResult?.message ?: failedResult.error
                    ?: "Action execution failed",
                    tracing = TracingInfo(
                        workflowExecutionId = workflowExecutionId,
                        ruleResultId = failedResult.id
                    ),
                    rule = RuleInfo(
                        ruleId = failedResult.rule.id,
                        ruleName = failedResult.rule.name
                    ),
                    error = ErrorInfo(
                        type = WorkflowErrorType.SYSTEM_ERROR,
                        details = failedResult.error
                    )
                )
            }

            else -> {
                logger.info { "Workflow [${workflowExecutionId}] completed with no matching rules" }
                WorkflowResponse(
                    success = false,
                    message = "No rules matched the invoice criteria",
                    tracing = TracingInfo(
                        workflowExecutionId = workflowExecutionId,
                        ruleResultId = null
                    ),
                    error = ErrorInfo(
                        type = WorkflowErrorType.NO_RULES_MATCHED,
                        details = "No applicable rules found for the provided invoice criteria"
                    )
                )
            }
        }
    }

    /**
     * Creates a validation error response.
     */
    private fun createValidationErrorResponse(
        error: String,
        workflowExecutionId: UUID
    ): WorkflowResponse {
        return WorkflowResponse(
            success = false,
            message = "Invalid request: $error",
            tracing = TracingInfo(
                workflowExecutionId = workflowExecutionId,
                ruleResultId = null
            ),
            error = ErrorInfo(
                type = WorkflowErrorType.VALIDATION_ERROR,
                details = error
            )
        )
    }

    /**
     * Creates a system error response.
     */
    private fun createSystemErrorResponse(
        error: String,
        workflowExecutionId: UUID
    ): WorkflowResponse {
        return WorkflowResponse(
            success = false,
            message = "Error executing workflow: $error",
            tracing = TracingInfo(
                workflowExecutionId = workflowExecutionId,
                ruleResultId = null
            ),
            error = ErrorInfo(
                type = WorkflowErrorType.SYSTEM_ERROR,
                details = error
            )
        )
    }
} 