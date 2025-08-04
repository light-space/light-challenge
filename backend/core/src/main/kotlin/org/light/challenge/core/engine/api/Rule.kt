package org.light.challenge.core.engine.api

/**
 * Represents a business rule with condition and action.
 */
data class Rule(
    val id: String,
    val name: String,
    val description: String? = null,
    val priority: Int = 0,
    val conditionExpression: String,
    val action: Action
) {
    init {
        require(id.isNotBlank()) { "Rule ID cannot be blank" }
        require(name.isNotBlank()) { "Rule name cannot be blank" }
        require(conditionExpression.isNotBlank()) { "Rule condition expression cannot be blank" }
    }
}

