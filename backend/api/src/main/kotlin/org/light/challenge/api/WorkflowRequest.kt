package org.light.challenge.api

import java.math.BigDecimal

/**
 * Request DTO for the workflow execution endpoint
 */
data class WorkflowRequest(
    val amount: BigDecimal,

    val department: String,

    val requiresManagerApproval: Boolean
)