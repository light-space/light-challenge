package org.light.challenge.core.engine.api

/**
 * Generic action definition for rule engine.
 * Contains action identifier and parameters for execution.
 */
data class Action(
    val id: String,
    val parameters: Map<String, Any> = emptyMap()
) {
    init {
        require(id.isNotBlank()) { "Action ID cannot be blank" }
    }

    /**
     * Gets a required parameter, throws if missing.
     */
    fun getRequiredParameter(key: String): Any {
        return parameters[key] ?: throw IllegalStateException("Required parameter '$key' not found")
    }
}