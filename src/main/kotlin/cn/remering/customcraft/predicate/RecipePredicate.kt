package cn.remering.customcraft.predicate

import cn.remering.customcraft.recipe.Recipe
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

interface RecipeInventory {
    val inputItemStackList: MutableList<ItemStack>
    val outputItemStackList: MutableList<ItemStack>
    val outputSize: Int
    val processTicks: Long
    val inventory: Inventory
}

interface RecipeInfo {
    val human: HumanEntity
    val inventory: RecipeInventory
}

interface RecipePredicate: Predicate<RecipeInfo>, ConfigurationSerializable {
    fun validate(): String?
}

interface RecipePredicateBuilder<P: RecipePredicate>: Keyed {
    fun build(map: Map<String, Any>): P?
}

abstract class AbstractRecipePredicateBuilder<P: RecipePredicate>(
    private val key: NamespacedKey
) : RecipePredicateBuilder<P> {
    override fun getKey() = key

    fun validateKey(map: Map<String, Any>): Boolean {
        val keyStr = map[KEY_PATH] as? String ?: return false
        return keyStr == key.toString()
    }

    fun putKey(map: MutableMap<String, Any>) {
        map[KEY_PATH] = key.toString()
    }
}

const val CUSTOM_CRAFT_NAMESPACE = "customcraft"
const val KEY_PATH = "key"

