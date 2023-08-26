package cn.remering.customcraft.predicate.logic

import cn.remering.customcraft.predicate.*
import cn.remering.customcraft.registry.RECIPE_PREDICATE_BUILDER_REGISTRY
import org.bukkit.NamespacedKey
import java.util.regex.Pattern


private const val RULES_PATH = "rules"
private const val NAMESPACED_KEY = "and"
private val pattern = Pattern.compile(":")
class AndPredicate private constructor(
    private val map: Map<String, Any>
): RecipePredicate {

    companion object: AbstractRecipePredicateBuilder<AndPredicate>(NamespacedKey(CUSTOM_CRAFT_NAMESPACE, NAMESPACED_KEY)) {
        override fun serialize(map: Map<String, Any>): AndPredicate? {
            if (!validateKey(map)) return null
            return AndPredicate(map)
        }

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(predicate: AndPredicate): Map<String, Any> {
            val map = mutableMapOf<String, Any>()
            putKey(map)
            map[RULES_PATH] = predicate.predicates.map {(mapKey, mapValue) ->
                val recipeBuilder = mapValue as RecipePredicateBuilder<RecipePredicate>
                recipeBuilder.deserialize(mapKey)
            }
            return map
        }

    }

    private lateinit var predicates: Map<RecipePredicate, RecipePredicateBuilder<*>>

    @Suppress("UNCHECKED_CAST")
    override fun validate(): String? {
        val rules = map[RULES_PATH] as? List<Map<String, Any>> ?: return "No rules specified"
        predicates = rules.map { rule ->
            val keySplit = (rule[KEY_PATH] as? String)?.split(pattern, 1)?: return "No rule's key specified"
            val namespacedKey = NamespacedKey(keySplit[0], keySplit[1])
            val predicateBuilder = RECIPE_PREDICATE_BUILDER_REGISTRY[namespacedKey] ?: return "No such predicate where key is $namespacedKey"
            val recipePredicate = predicateBuilder.serialize(rule) ?: return "Predicate builder returns null with $rule"
            val validMessage = recipePredicate.validate()
            if (validMessage != null) return validMessage
            recipePredicate to predicateBuilder
        }.associate { it }
        return null
    }

    override fun test(t: RecipeInfo) = predicates.keys.all { it.test(t) }
}