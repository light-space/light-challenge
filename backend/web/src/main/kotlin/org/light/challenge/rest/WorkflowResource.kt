package org.light.challenge.rest

import org.light.challenge.api.WorkflowErrorType
import org.light.challenge.api.WorkflowRequest
import org.light.challenge.api.WorkflowService
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/workflow")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class WorkflowResource @Inject constructor(
    private val workflowService: WorkflowService
) {

    @POST
    @Path("/execute")
    fun executeWorkflow(request: WorkflowRequest): Response {
        val workflowResponse = workflowService.executeWorkflow(request)

        return when {
            workflowResponse.success -> {
                // 200 OK - Successful workflow execution
                Response.ok(workflowResponse).build()
            }

            workflowResponse.error?.type == WorkflowErrorType.VALIDATION_ERROR -> {
                // 400 Bad Request - Invalid input data
                Response.status(Response.Status.BAD_REQUEST)
                    .entity(workflowResponse)
                    .build()
            }

            workflowResponse.error?.type == WorkflowErrorType.NO_RULES_MATCHED -> {
                // 200 OK - Valid business outcome, no applicable rules found
                Response.ok(workflowResponse).build()
            }

            workflowResponse.error?.type == WorkflowErrorType.SYSTEM_ERROR -> {
                // 500 Internal Server Error - System error
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(workflowResponse)
                    .build()
            }

            else -> {
                // 500 Internal Server Error - Unexpected case (should never happen)
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(workflowResponse)
                    .build()
            }
        }
    }
}