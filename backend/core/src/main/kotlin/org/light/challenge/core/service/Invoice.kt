package org.light.challenge.core.service

import java.math.BigDecimal

/**
 * Represents an invoice in the workflow system.
 */
data class Invoice(
    val amount: BigDecimal,
    val department: String,
    val requiresManagerApproval: Boolean
) {
    init {
        require(amount > BigDecimal.ZERO) {
            "Amount must be positive. Received: $amount"
        }
        require(department.isNotBlank()) {
            "Department cannot be blank or empty"
        }
        require(department.length <= 100) {
            "Department name too long. Maximum 100 characters, received: ${department.length}"
        }
    }
}