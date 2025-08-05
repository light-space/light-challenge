package org.light.challenge.core

import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.light.challenge.api.WorkflowService
import org.light.challenge.core.agent.AgentConfiguration
import org.light.challenge.core.service.RuleGenerationService
import org.light.challenge.api.RuleGenerationService as ApiRuleGenerationService
import org.light.challenge.core.service.RuleGenerationServiceImpl
import org.light.challenge.core.service.WorkflowServiceImpl

/**
 * HK2 binding configuration for the workflow application.
 */
class WorkflowConfig : AbstractBinder() {
    override fun configure() {
        this.bind(WorkflowServiceImpl::class.java)
            .to(WorkflowService::class.java)
        this.bind(RuleGenerationService::class.java)
            .to(RuleGenerationService::class.java)
        this.bind(AgentConfiguration::class.java)
            .to(AgentConfiguration::class.java)
        this.bind(RuleGenerationServiceImpl::class.java)
            .to(ApiRuleGenerationService::class.java)
    }
}