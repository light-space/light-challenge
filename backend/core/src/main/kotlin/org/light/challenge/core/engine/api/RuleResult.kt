package org.light.challenge.core.engine.api

import java.time.Instant
import java.util.*

/**
 * Result of rule evaluation and action execution.
 */
data class RuleResult(
    val id: UUID,
    val rule: Rule,
    val matched: Boolean,
    val evaluationError: String? = null,
    val actionResult: ActionResult? = null,
    val evaluatedAt: Instant = Instant.now()
) {
    /**
     * True if rule matched AND action executed successfully.
     */
    val isSuccessful: Boolean
        get() = matched && actionResult?.success == true

    val isActionFailure: Boolean
        get() = matched && actionResult?.success == false

}