package cn.remering.customcraft.predicate.recipe

import cn.remering.customcraft.predicate.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

private const val NAMESPACED_KEY = "shape-recipe-detect"
private const val PATTERN_PATH = "pattern"
private const val REPLACEMENT_PATH = "replacement"

class ShapeRecipeDetectPredicate(
    private val pattern: List<String>,
    private val replacement: Map<String, ItemStack>
) : RecipePredicate {

    @Suppress("DEPRECATION")
    companion object : AbstractRecipePredicateBuilder<ShapeRecipeDetectPredicate>(
        NamespacedKey(CUSTOM_CRAFT_NAMESPACE, NAMESPACED_KEY)
    ) {
        @Suppress("UNCHECKED_CAST")
        override fun build(map: Map<String, Any>): ShapeRecipeDetectPredicate? {
            if (!validateKey(map)) return null
            val pattern = map[PATTERN_PATH] as? MutableList<String> ?: return null
            val replacement = map[REPLACEMENT_PATH] as? Map<String, Map<String, Any>> ?: return null
            return ShapeRecipeDetectPredicate(pattern, replacement.mapValues { ItemStack.deserialize(it.value) })
        }

        fun fromItemStackList(itemStackList: List<ItemStack?>): ShapeRecipeDetectPredicate {
            var currentChar = 'A'
            val replacement = hashMapOf<String, ItemStack>()
            val itemStackList = itemStackList.toList()
            val text = buildString {
                outerLoop@ for (itemStack in itemStackList) {
                    if (itemStack == null || itemStack.type == Material.AIR) {
                        append(' ')
                        continue
                    }
                    itemStack.amount = 1
                    for ((k, v) in replacement) {
                        if (v.isSimilar(itemStack)) {
                            append(k)
                            continue@outerLoop
                        }
                    }
                    replacement[currentChar.toString()] = itemStack
                    append(currentChar++)
                }
            }
            val pattern = text.windowed(3, 3, false)
            return ShapeRecipeDetectPredicate(
                pattern,
                replacement
            )
        }
    }

    private val expected = mutableListOf<ItemStack>()

    override fun validate(): String? {
        for (line in pattern) {
            for (c in line) {
                expected += if (c == ' ') {
                    ItemStack(Material.AIR)
                } else {
                    val itemStack = replacement[c.toString()]
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

        return canCraft(tempExpected, tempActual)
    }

    override fun serialize(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        putKey(map)
        map[PATTERN_PATH] = pattern
        map[REPLACEMENT_PATH] = replacement.mapValues { it.value.serialize() }
        return map
    }

}