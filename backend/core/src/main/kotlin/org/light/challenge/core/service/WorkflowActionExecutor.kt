package org.light.challenge.core.service

import mu.KotlinLogging
import org.light.challenge.core.engine.api.Action
import org.light.challenge.core.engine.api.ActionExecutor
import org.light.challenge.core.engine.api.ActionResult

private val logger = KotlinLogging.logger {}

/**
 * Business-specific action executors for workflow operations.
 */
object WorkflowActionExecutor {
    /**
     * Registers all workflow-specific action executors.
     */
    fun executor(): ActionExecutor {
        val executor = ActionExecutor()
        val emailExecutor = EmailActionExecutor()
        val slackExecutor = SlackActionExecutor()
        executor.register("send-email") { action, _ ->
            emailExecutor.execute(action)
        }

        executor.register("send-slack") { action, _ ->
            slackExecutor.execute(action)
        }

        return executor
    }
}

/**
 * Executor for email sending actions.
 */
private class EmailActionExecutor {
    fun execute(action: Action): ActionResult {
        return try {
            val recipient = action.getRequiredParameter("recipient") as String
            val subject = action.getRequiredParameter("subject") as String
            val body = action.getRequiredParameter("body") as String

            validateEmailParameters(recipient, subject, body)

            val message = "Sending email to $recipient with subject '$subject' and body '$body'"
            logger.info { message }
            println(message)

            ActionResult.success(message)
        } catch (e: IllegalStateException) {
            ActionResult.failure("Missing required email parameters: ${e.message}")
        } catch (e: IllegalArgumentException) {
            ActionResult.failure("Invalid email parameters: ${e.message}")
        }
    }

    private fun validateEmailParameters(recipient: String, subject: String, body: String) {
        require(recipient.isNotBlank()) { "recipient cannot be blank" }
        require(subject.isNotBlank()) { "subject cannot be blank" }
        require(body.isNotBlank()) { "body cannot be blank" }
    }
}

/**
 * Executor for Slack sending actions.
 */
private class SlackActionExecutor {
    fun execute(action: Action): ActionResult {
        return try {
            val recipient = action.getRequiredParameter("recipient") as String
            val messageText = action.getRequiredParameter("message") as String

            validateSlackParameters(recipient, messageText)

            val message = "Sending approval via Slack to $recipient: $messageText"
            logger.info { message }
            println(message)

            ActionResult.success(message)
        } catch (e: IllegalStateException) {
            ActionResult.failure("Missing required Slack parameters: ${e.message}")
        } catch (e: IllegalArgumentException) {
            ActionResult.failure("Invalid Slack parameters: ${e.message}")
        }
    }

    private fun validateSlackParameters(recipient: String, message: String) {
        require(recipient.isNotBlank()) { "recipient cannot be blank" }
        require(message.isNotBlank()) { "message cannot be blank" }
    }
} 