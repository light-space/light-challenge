package org.light.challenge.core.repository

import org.light.challenge.core.engine.api.Rule

/**
 * In-memory repository for storing and managing rules.
 */
class RuleRepository(
    defaultRuleProvider: DefaultRuleProvider = DefaultRuleProvider()
) {
    private val rules = defaultRuleProvider.getDefaultRules()

    /**
     * Gets all rules as an immutable list.
     */
    fun getAllRules(): List<Rule> = rules.toList()

    /**
     * Gets the number of rules in the repository.
     */
    fun size(): Int = rules.size

}