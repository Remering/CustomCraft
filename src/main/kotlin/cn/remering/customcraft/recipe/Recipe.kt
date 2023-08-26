package cn.remering.customcraft.recipe

import cn.remering.customcraft.predicate.RecipePredicate
import cn.remering.customcraft.registry.RECIPE_PREDICATE_BUILDER_REGISTRY
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate
import java.util.regex.Pattern

interface Recipe: ConfigurationSerializable, Keyed, Comparable<Recipe> {
    val predicate: RecipePredicate<*>
    val result: List<ItemStack>
    val priority: Int
        get() = 0

    override fun compareTo(other: Recipe) = priority - other.priority
    override fun serialize(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["key"] = key.toString()
        map["predicate"] = predicate.serialize()
        map["result"] = result.map { it.serialize() }
        return map
    }
}

class SimpleRecipe(
    private val key: NamespacedKey,
    override val predicate: RecipePredicate<*>,
    override val result: List<ItemStack>
) : Recipe {
    override fun getKey() = key

    companion object {

        private val pattern = Pattern.compile(":")

        @JvmStatic
        @Suppress("DEPRECATION", "UNCHECKED_CAST", "unused")
        fun valueOf(map: Map<String, Any>): SimpleRecipe? {
            val keySplit= (map["key"] as? String)?.split(pattern, 1)?: return null
            val namespacedKey = NamespacedKey(keySplit[0], keySplit[1])
            val predicateMap = map["predicate"] as? Map<String, Any>?: return null
            val predicateKey = (predicateMap["key"] as? String)?.split(pattern, 1)?: return null
            val namespacedPredicateKey = NamespacedKey(predicateKey[0], predicateKey[1])
            val predicateBuilder = RECIPE_PREDICATE_BUILDER_REGISTRY[namespacedPredicateKey] ?: return null
            val predicate = predicateBuilder.build(predicateMap)?: return null
            val resultMapList = map["result"] as? List<Map<String, Any>>?:return null
            val result = resultMapList.map { ItemStack.deserialize(it) }
            return SimpleRecipe(namespacedKey, predicate, result)
        }
    }
}
