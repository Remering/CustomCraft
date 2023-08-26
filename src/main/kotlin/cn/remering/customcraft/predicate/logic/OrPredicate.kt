package cn.remering.customcraft.predicate.logic

import cn.remering.customcraft.predicate.*
import cn.remering.customcraft.registry.RECIPE_PREDICATE_BUILDER_REGISTRY
import org.bukkit.NamespacedKey
import java.util.regex.Pattern


private const val RULES_PATH = "rules"
private const val NAMESPACED_KEY = "or"
private val pattern = Pattern.compile(":")
class OrPredicate (
    private val map: Map<String, Any>
): RecipePredicate<OrPredicate> {

    companion object: AbstractRecipePredicateBuilder<OrPredicate>(NamespacedKey(CUSTOM_CRAFT_NAMESPACE, NAMESPACED_KEY)) {
        override fun build(map: Map<String, Any>): OrPredicate? {
            if (!validateKey(map)) return null
            return OrPredicate(map)
        }
    }

    private val predicates = arrayListOf<RecipePredicate<*>>()

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    override fun validate(): String? {
        val rules = map[RULES_PATH] as? List<Map<String, Any>> ?: return "No rules specified"
         rules.forEach { rule ->
            val keySplit = (rule[KEY_PATH] as? String)?.split(pattern, 1)?: return "No rule's key specified"
            val namespacedKey = NamespacedKey(keySplit[0], keySplit[1])
            val predicateBuilder = RECIPE_PREDICATE_BUILDER_REGISTRY[namespacedKey] ?: return "No such predicate where key is $namespacedKey"
            val recipePredicate = predicateBuilder.build(rule) ?: return "Predicate builder returns null with $rule"
            val invalidMessage = recipePredicate.validate()
            if (invalidMessage != null) return "Invalid predicate with key $namespacedKey: $invalidMessage"
            predicates += recipePredicate
        }
        return null
    }

    override fun test(t: RecipeInfo) = predicates.any { it.test(t) }

    override fun serialize(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        putKey(map)
        map[RULES_PATH] = predicates.map { it.serialize() }
        return map
    }
}