package cn.remering.customcraft.gui

import cn.remering.customcraft.CustomCraft
import cn.remering.customcraft.config.Config
import cn.remering.customcraft.predicate.CUSTOM_CRAFT_NAMESPACE
import cn.remering.customcraft.predicate.logic.AndPredicate
import cn.remering.customcraft.predicate.recipe.ShapeRecipeDetectPredicate
import cn.remering.customcraft.recipe.SimpleRecipe
import cn.remering.customcraft.registry.RECIPE_REGISTRY
import mc.obliviate.inventory.Gui
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*


class EditRecipeGui(player: Player, private val key: String) : Gui(player, "$CUSTOM_CRAFT_NAMESPACE:edit-recipe-gui", "Hello, world", InventoryType.WORKBENCH) {

    override fun onClick(event: InventoryClickEvent): Boolean {
        if (event.slot > inventory.size) return true
        val item = inventory.getItem(event.slot)
        if (item != null && item.type != Material.AIR) {
            when (event.click) {
                ClickType.LEFT -> item.amount++
                ClickType.RIGHT -> item.amount--
                else -> {}
            }
            inventory.setItem(event.slot, item)
            return false
        }
        // now item == null || item.type == Material.AIR
        inventory.setItem(event.slot, event.cursor)
        return false
    }

    override fun onDrag(event: InventoryDragEvent): Boolean {
        val newItem = event.newItems.values.firstOrNull()
        event.inventorySlots.forEach {
            if (it > inventory.size) return@forEach
            inventory.setItem(it, newItem)
        }
        return false
    }

    override fun onClose(event: InventoryCloseEvent) {
        val result = inventory.getItem(0)
        if (result == null || result.type.isAir) {
            player.sendMessage("Recipe won't save because result is empty")
            return
        }

        if (inventory.toList().subList(1, inventory.size).all { it == null || it.type == Material.AIR }) {
            player.sendMessage("Recipe won't save because ingredients is empty")
            return
        }

        val andPredicate = AndPredicate(
            listOf(
                ShapeRecipeDetectPredicate.fromItemStackList(
                    inventory.toList().subList(1, inventory.size)
                )
            )
        )

        val namespacedKey = NamespacedKey(CustomCraft.self, key)
        val recipe = SimpleRecipe(
            namespacedKey,
            andPredicate,
            listOf(result),
            0,
            0,
        )

        RECIPE_REGISTRY.register(recipe)
        Config.saveRecipes()
        player.sendMessage("Saved recipe with key: $namespacedKey")
    }
}