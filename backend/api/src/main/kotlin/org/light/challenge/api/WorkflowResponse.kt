package org.light.challenge.api

import java.util.*

/**
 * Error types for workflow execution
 */
enum class WorkflowErrorType {
    VALIDATION_ERROR,     // 400 Bad Request - Invalid input data
    NO_RULES_MATCHED,     // 200 OK (valid business outcome, success=false)
    SYSTEM_ERROR          // 500 Internal Server Error
}

/**
 * Tracing information for debugging and monitoring
 */
data class TracingInfo(
    /**
     * Unique ID for this workflow execution instance
     */
    val workflowExecutionId: UUID,

    /**
     * ID of the specific rule result that was executed (null if no rules matched)
     */
    val ruleResultId: UUID? = null
)

/**
 * Information about successful rule execution
 */
data class RuleInfo(
    val ruleId: String,
    val ruleName: String
)

/**
 * Error details for failed executions
 */
data class ErrorInfo(
    val type: WorkflowErrorType,
    val details: String? = null
)

/**
 * Response DTO for the workflow execution endpoint
 */
data class WorkflowResponse(
    val success: Boolean,

    val message: String,

    /**
     * Tracing information - always present for debugging
     */
    val tracing: TracingInfo,

    /**
     * Rule info - only present when a rule was successfully executed
     */
    val rule: RuleInfo? = null,

    /**
     * Error details - only present when execution failed
     */
    val error: ErrorInfo? = null
)
