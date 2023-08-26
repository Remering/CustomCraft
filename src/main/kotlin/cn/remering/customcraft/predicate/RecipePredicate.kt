package cn.remering.customcraft.predicate

import cn.remering.customcraft.recipe.Recipe
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

interface RecipeInventory {
    val inputItemStackList: MutableList<ItemStack>
    val outputItemStackList: MutableList<ItemStack>
    val processTicks: Long
    val inventory: Inventory
}

interface RecipeInfo {
    val human: HumanEntity
    val recipe: Recipe
    val inventory: RecipeInventory
}

interface RecipePredicate: Predicate<RecipeInfo> {
    fun validate(): String?
}

interface RecipePredicateBuilder<P: RecipePredicate>: Keyed {
    fun serialize(map: Map<String, Any>): P?
    fun deserialize(predicate: P): Map<String, Any>
}

abstract class AbstractRecipePredicateBuilder<P: RecipePredicate>(
    private val key: NamespacedKey
) : RecipePredicateBuilder<P> {
    override fun getKey() = key
}

const val CUSTOM_CRAFT_NAMESPACE = "customcraft"
const val KEY_PATH = "key"
