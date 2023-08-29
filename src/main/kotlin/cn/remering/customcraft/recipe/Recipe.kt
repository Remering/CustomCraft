package cn.remering.customcraft.recipe

import cn.remering.customcraft.CustomCraft
import cn.remering.customcraft.predicate.RecipePredicate
import cn.remering.customcraft.registry.RECIPE_PREDICATE_BUILDER_REGISTRY
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import java.util.regex.Pattern

interface Recipe: ConfigurationSerializable, Keyed, Comparable<Recipe> {
    val predicate: RecipePredicate
    val result: List<ItemStack>
    val totalProcessTicks: Int
    val priority: Int
        get() = 0

    override fun compareTo(other: Recipe) = priority - other.priority
    override fun serialize(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["key"] = key.toString()
        map["predicate"] = predicate.serialize()
        map["result"] = result.map { it.serialize() }
        map["total-process-ticks"] = totalProcessTicks
        map["priority"] = priority
        return map
    }
}

data class SimpleRecipe(
    private val key: NamespacedKey,
    override val predicate: RecipePredicate,
    override val result: List<ItemStack>,
    override val totalProcessTicks: Int,
    override val priority: Int
) : Recipe {
    override fun getKey() = key

    companion object {

        private val pattern = Pattern.compile(":")
        @JvmStatic
        @Suppress("DEPRECATION", "UNCHECKED_CAST", "unused")
        fun deserialize(map: Map<String, Any>): SimpleRecipe? {
            val logger = CustomCraft.self.logger

            val keyStr = map["key"] as? String
            val keySplit= keyStr?.split(pattern, 2)
            if (keySplit == null) {
                logger.severe("Recipe with $keyStr won't be loaded for invalid key")
                return null
            }
            val namespacedKey = NamespacedKey(keySplit[0], keySplit[1])

            val predicateMap = map["predicate"] as? Map<String, Any>
            if (predicateMap == null) {
                logger.severe("Recipe with $namespacedKey won't be loaded for no predicate")
                return null
            }
            val predicateKey = predicateMap["key"] as? String
            if (predicateKey == null) {
                logger.severe("Recipe with $namespacedKey won't be loaded for no predicate key")
            }
            val predicateKeySplit = predicateKey?.split(pattern, 2)?: return null

            val namespacedPredicateKey = NamespacedKey(predicateKeySplit[0], predicateKeySplit[1])
            val predicateBuilder = RECIPE_PREDICATE_BUILDER_REGISTRY[namespacedPredicateKey] ?: return null
            val predicate = predicateBuilder.build(predicateMap)
            if (predicate == null) {
                logger.severe("Recipe with $namespacedKey won't be loaded because no predicate builder with key $predicateKey")
                return null
            }
            val invalidMessage = predicate.validate()
            if (invalidMessage != null) {
                CustomCraft.self.logger.warning("Recipe with key $namespacedKey won't be loaded because of invalid predicate which key is $namespacedPredicateKey: $invalidMessage")
                return null
            }
            val resultMapList = map["result"] as? List<Map<String, Any>>?:return null
            val result = resultMapList.map { ItemStack.deserialize(it) }
            val totalProcessTicks = map["total-process-ticks"] as? Int ?: return null
            val priority = map["priority"] as? Int ?: return null
            return SimpleRecipe(namespacedKey, predicate, result, totalProcessTicks, priority)
        }
    }
}
