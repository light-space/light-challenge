package org.light.challenge.core.engine.api

/**
 * Result of action execution.
 */
data class ActionResult(
    val success: Boolean,
    val message: String
) {
    init {
        require(message.isNotBlank()) { "Action result message cannot be blank" }
    }

    companion object {
        /**
         * Creates a successful result.
         */
        fun success(message: String): ActionResult {
            return ActionResult(success = true, message = message)
        }

        /**
         * Creates a failed result.
         */
        fun failure(message: String): ActionResult {
            return ActionResult(success = false, message = message)
        }
    }
}