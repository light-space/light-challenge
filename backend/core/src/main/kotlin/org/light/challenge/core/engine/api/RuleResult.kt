package org.light.challenge.core.engine.api

import java.util.*

/**
 * Result of rule evaluation and action execution.
 */
data class RuleResult(
    val id: UUID,
    val rule: Rule,
    val matched: Boolean,
    val actionResult: ActionResult?,
    val error: String?
)