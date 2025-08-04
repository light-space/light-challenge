package org.light.challenge.api

/**
 * Interface for the workflow service that orchestrates the workflow execution
 */
interface WorkflowService {

    /**
     * Executes the workflow for the given request
     * @param request The workflow request containing invoice data
     * @return The workflow response with execution results
     */
    fun executeWorkflow(request: WorkflowRequest): WorkflowResponse
}