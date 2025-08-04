package org.light.challenge.core.engine.api

/**
 * Container for facts used in rule evaluation.
 * Facts are key-value pairs accessible to MVEL expressions.
 */
class Facts {
    private val factMap: MutableMap<String, Any> = mutableMapOf()

    /**
     * Adds a fact and returns this instance for chaining.
     */
    fun withFact(name: String, value: Any): Facts {
        require(name.isNotBlank()) { "Fact name cannot be blank" }
        factMap[name] = value
        return this
    }

    /**
     * Gets a fact by name.
     */
    fun getFact(name: String): Any? = factMap[name]

    /**
     * Checks if a fact exists.
     */
    fun hasFact(name: String): Boolean = factMap.containsKey(name)

    /**
     * Returns all facts as a map for MVEL evaluation.
     */
    fun toMap(): Map<String, Any> = factMap.toMap()

    /**
     * Returns the number of facts.
     */
    fun size(): Int = factMap.size

    /**
     * Checks if facts are empty.
     */
    fun isEmpty(): Boolean = factMap.isEmpty()
} 