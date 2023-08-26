package cn.remering.customcraft.predicate.recipe

import cn.remering.customcraft.predicate.AbstractRecipePredicateBuilder
import cn.remering.customcraft.predicate.CUSTOM_CRAFT_NAMESPACE
import cn.remering.customcraft.predicate.RecipeInfo
import cn.remering.customcraft.predicate.RecipePredicate
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack


private const val NAMESPACED_KEY = "shape-recipe-consume"
private const val PATTERN_KEY = "pattern"
private const val REPLACEMENT_KEY = "replacement"

class ShapeRecipeConsumePredicate(
    private val pattern: List<String>,
    private val replacement: Map<String, ItemStack>
): RecipePredicate {

    companion object: AbstractRecipePredicateBuilder<ShapeRecipeConsumePredicate>(
        NamespacedKey(CUSTOM_CRAFT_NAMESPACE, NAMESPACED_KEY)
    ) {
        @Suppress("UNCHECKED_CAST")
        override fun serialize(map: Map<String, Any>): ShapeRecipeConsumePredicate? {
            val pattern = map[PATTERN_KEY] as? MutableList<String> ?: return null
            val replacement = map[REPLACEMENT_KEY] as? Map<String, Map<String, Any>> ?: return null
            return ShapeRecipeConsumePredicate(pattern, replacement.mapValues { ItemStack.deserialize(it.value) })
        }

        override fun deserialize(predicate: ShapeRecipeConsumePredicate): Map<String, Any> {
            val map = mutableMapOf<String, Any>()
            map[PATTERN_KEY] = predicate.pattern
            map[REPLACEMENT_KEY] = predicate.replacement.mapValues { it.value.serialize() }
            return map
        }

    }

    private val expected = mutableListOf<ItemStack>()

    override fun validate(): String? {
        for (line in pattern) {
            for (c in line) {
                expected += if (c == ' ') {
                    ItemStack(Material.AIR)
                } else {
                    val itemStack = replacement[expected.toString()]
                        ?: return "Unknown char $c in pattern $pattern with replacement $replacement"
                    itemStack
                }
            }
        }
        return null
    }

    override fun test(info: RecipeInfo): Boolean {
        val inventory = info.inventory
        val airItemStack = ItemStack(Material.AIR)
        val tempExpected = expected.toMutableList()
        val tempActual = inventory.inputItemStackList.toMutableList()
        if (tempExpected.size < tempActual.size) {
            repeat(tempActual.size - tempExpected.size) {
                tempExpected += airItemStack
            }
        } else if (tempActual.size < tempExpected.size) {
            repeat(tempExpected.size - tempActual.size) {
                tempActual += airItemStack
            }
        }
        val result = tempExpected == tempActual

        if (result) {
            for (index in inventory.inputItemStackList.indices) {
                inventory.inputItemStackList[index] = airItemStack
            }
        }
        return result
    }

}