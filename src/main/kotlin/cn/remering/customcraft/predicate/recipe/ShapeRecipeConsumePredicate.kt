package cn.remering.customcraft.predicate.recipe

import cn.remering.customcraft.predicate.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack


private const val NAMESPACED_KEY = "shape-recipe-consume"
private const val PATTERN_PATH = "pattern"
private const val REPLACEMENT_PATH = "replacement"

class ShapeRecipeConsumePredicate (
    private val pattern: List<String>,
    private val replacement: Map<String, ItemStack>
): RecipePredicate<ShapeRecipeConsumePredicate> {

    @Suppress("DEPRECATION")
    companion object: AbstractRecipePredicateBuilder<ShapeRecipeConsumePredicate>(
        NamespacedKey(CUSTOM_CRAFT_NAMESPACE, NAMESPACED_KEY)
    ) {
        @Suppress("UNCHECKED_CAST")
        override fun build(map: Map<String, Any>): ShapeRecipeConsumePredicate? {
            if (!validateKey(map)) return null
            val pattern = map[PATTERN_PATH] as? MutableList<String> ?: return null
            val replacement = map[REPLACEMENT_PATH] as? Map<String, Map<String, Any>> ?: return null
            return ShapeRecipeConsumePredicate(pattern, replacement.mapValues { ItemStack.deserialize(it.value) })
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

    private fun canCraft(expected: List<ItemStack>, actual: List<ItemStack>): Boolean {
        for (index in expected.indices) {
            val expectedItemStack = expected[index]
            val actualItemStack = actual[index]
            if (!expectedItemStack.isSimilar(actualItemStack) || actualItemStack.amount < expectedItemStack.amount)
                return false
        }
        return true
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
        val result = canCraft(tempExpected, tempActual)

        if (result) {
            for (itemStack in inventory.inputItemStackList) {
                if (itemStack.isSimilar(airItemStack)) continue
                if (!expected.any { it.isSimilar(itemStack) }) continue
                itemStack.amount--
            }
        }
        return result
    }

    override fun serialize(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        putKey(map)
        map[PATTERN_PATH] = pattern
        map[REPLACEMENT_PATH] = replacement.mapValues { it.value.serialize() }
        return map
    }
}