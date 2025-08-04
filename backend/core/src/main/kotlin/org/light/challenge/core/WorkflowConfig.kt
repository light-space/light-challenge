package org.light.challenge.core

import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.light.challenge.api.WorkflowService
import org.light.challenge.core.service.WorkflowServiceImpl

/**
 * HK2 binding configuration for the workflow application.
 */
class WorkflowConfig : AbstractBinder() {

    override fun configure() {
        this.bind(WorkflowServiceImpl::class.java)
            .to(WorkflowService::class.java)
    }
}