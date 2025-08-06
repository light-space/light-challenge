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

        logger.info { "Executing workflow [${workflowExecutionId}] for invoice: amount=${request.amount}, department=${request.department}" }

        return try {
            val facts = createFacts(request)
            val rules = ruleRepository.getAllRules()
            val results = ruleEngine.evaluate(rules, facts, actionExecutor)

            createResponse(results, workflowExecutionId)
        } catch (e: IllegalArgumentException) {
            logger.warn { "Workflow [${workflowExecutionId}] validation failed: ${e.message}" }
            createValidationErrorResponse(e.message ?: "Invalid request data", workflowExecutionId)
        } catch (e: Exception) {
            logger.error(e) { "Error executing workflow [${workflowExecutionId}]: ${e.message}" }
            createSystemErrorResponse(e.message ?: "Unknown error", workflowExecutionId)
        }
    }

    private fun createFacts(request: WorkflowRequest): Facts {
        val invoice = Invoice(
            amount = request.amount,
            department = request.department,
            requiresManagerApproval = request.requiresManagerApproval
        )
        return Facts().withFact("invoice", invoice)
    }

    private fun createResponse(
        results: List<RuleResult>,
        workflowExecutionId: UUID
    ): WorkflowResponse {
        // First successful result wins
        val successfulResult = results.firstOrNull { it.isSuccessful }
        if (successfulResult != null) {
            return createSuccessResponse(successfulResult, workflowExecutionId)
        }

        // Check for evaluation errors
        val evaluationFailure = results.firstOrNull { it.evaluationError != null }
        if (evaluationFailure != null) {
            return createEvaluationErrorResponse(evaluationFailure, workflowExecutionId)
        }

        // Check for action failures
        val actionFailure = results.firstOrNull { it.isActionFailure }
        if (actionFailure != null) {
            return createActionFailureResponse(actionFailure, workflowExecutionId)
        }

        // No rules matched
        return createNoMatchResponse(workflowExecutionId)
    }

    private fun createSuccessResponse(
        result: RuleResult,
        workflowExecutionId: UUID
    ): WorkflowResponse {
        return WorkflowResponse(
            success = true,
            message = result.actionResult?.message ?: "Action executed successfully",
            tracing = TracingInfo(workflowExecutionId, result.id),
            rule = RuleInfo(result.rule.id, result.rule.name)
        )
    }

    private fun createEvaluationErrorResponse(
        result: RuleResult,
        workflowExecutionId: UUID
    ): WorkflowResponse {
        return WorkflowResponse(
            success = false,
            message = "Rule evaluation failed: ${result.evaluationError}",
            tracing = TracingInfo(workflowExecutionId, result.id),
            rule = RuleInfo(result.rule.id, result.rule.name),
            error = ErrorInfo(WorkflowErrorType.SYSTEM_ERROR, result.evaluationError)
        )
    }

    private fun createActionFailureResponse(
        result: RuleResult,
        workflowExecutionId: UUID
    ): WorkflowResponse {
        return WorkflowResponse(
            success = false,
            message = "Rule matched but action failed: ${result.actionResult?.message}",
            tracing = TracingInfo(workflowExecutionId, result.id),
            rule = RuleInfo(result.rule.id, result.rule.name),
            error = ErrorInfo(WorkflowErrorType.SYSTEM_ERROR, result.actionResult?.message)
        )
    }

    private fun createNoMatchResponse(workflowExecutionId: UUID): WorkflowResponse {
        return WorkflowResponse(
            success = false,
            message = "No rules matched the invoice criteria",
            tracing = TracingInfo(workflowExecutionId, null),
            error = ErrorInfo(WorkflowErrorType.NO_RULES_MATCHED, "No applicable rules found")
        )
    }

    private fun createValidationErrorResponse(
        error: String,
        workflowExecutionId: UUID
    ): WorkflowResponse {
        return WorkflowResponse(
            success = false,
            message = "Invalid request: $error",
            tracing = TracingInfo(workflowExecutionId, null),
            error = ErrorInfo(WorkflowErrorType.VALIDATION_ERROR, error)
        )
    }

    private fun createSystemErrorResponse(
        error: String,
        workflowExecutionId: UUID
    ): WorkflowResponse {
        return WorkflowResponse(
            success = false,
            message = "Error executing workflow: $error",
            tracing = TracingInfo(workflowExecutionId, null),
            error = ErrorInfo(WorkflowErrorType.SYSTEM_ERROR, error)
        )
    }
} 