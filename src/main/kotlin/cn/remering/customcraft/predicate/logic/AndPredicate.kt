package cn.remering.customcraft.predicate.logic

import cn.remering.customcraft.predicate.*
import cn.remering.customcraft.registry.RECIPE_PREDICATE_BUILDER_REGISTRY
import org.bukkit.NamespacedKey
import java.util.regex.Pattern


private const val RULES_PATH = "rules"
private const val NAMESPACED_KEY = "and"
private val pattern = Pattern.compile(":")
class AndPredicate (
    private val map: Map<String, Any>
): RecipePredicate {

    @Suppress("DEPRECATION")
    companion object: AbstractRecipePredicateBuilder<AndPredicate>(NamespacedKey(CUSTOM_CRAFT_NAMESPACE, NAMESPACED_KEY)) {
        override fun build(map: Map<String, Any>): AndPredicate? {
            if (!validateKey(map)) return null
            return AndPredicate(map)
        }
    }

    private val predicates = arrayListOf<RecipePredicate>()

    constructor(predicate: List<RecipePredicate>): this(mapOf()) {
        this.predicates.addAll(predicate)
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    override fun validate(): String? {
        if (predicates.isNotEmpty()) return null
        val rules = map[RULES_PATH] as? List<Map<String, Any>> ?: return "No rules specified"
        rules.forEach { rule ->
            val keySplit = (rule[KEY_PATH] as? String)?.split(pattern, 2)?: return "No rule's key specified"
            val namespacedKey = NamespacedKey(keySplit[0], keySplit[1])
            val predicateBuilder = RECIPE_PREDICATE_BUILDER_REGISTRY[namespacedKey] ?: return "No such predicate where key is $namespacedKey"
            val recipePredicate = predicateBuilder.build(rule) ?: return "Predicate builder returns null with $rule"
            val invalidMessage = recipePredicate.validate()
            if (invalidMessage != null) return "Invalid predicate with key $namespacedKey: $invalidMessage"
            predicates += recipePredicate
        }
        return null
    }

    override fun test(t: RecipeInfo) = predicates.all { it.test(t) }

    override fun serialize(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        putKey(map)
        map[RULES_PATH] = predicates.map { it.serialize() }
        return map
    }
}