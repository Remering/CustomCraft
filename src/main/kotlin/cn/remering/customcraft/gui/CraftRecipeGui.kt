package cn.remering.customcraft.gui

import cn.remering.customcraft.CustomCraft
import cn.remering.customcraft.predicate.CUSTOM_CRAFT_NAMESPACE
import cn.remering.customcraft.predicate.RecipeInfo
import cn.remering.customcraft.predicate.RecipeInventory
import cn.remering.customcraft.registry.RECIPE_REGISTRY
import mc.obliviate.inventory.Gui
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


class CraftRecipeGui(player: Player) : Gui(player, "$CUSTOM_CRAFT_NAMESPACE:craft-recipe-gui", "Hello, world", InventoryType.WORKBENCH) {
    private val recipeInfo: RecipeInfo
        get() = object : RecipeInfo {
            override val human: HumanEntity
                get() = player
            override val inventory = object: RecipeInventory {
                override val inputItemStackList: MutableList<ItemStack>
                    get() = (1 until 10).map { inventory.getItem(it)?:ItemStack(Material.AIR) }.toMutableList()
                override val outputItemStackList: MutableList<ItemStack>
                    get() = mutableListOf(inventory.getItem(0)?: ItemStack(Material.AIR))
                override val outputSize: Int
                    get() = 1
                override val processTicks: Long
                    get() = 0
                override val inventory: Inventory
                    get() = this@CraftRecipeGui.inventory

            }
        }

    override fun onClick(event: InventoryClickEvent): Boolean {
        if (event.isLeftClick && event.slot == 0 && event.slotType == InventoryType.SlotType.RESULT) {
            val resultItem = inventory.getItem(0)
            if (event.cursor != null && (resultItem == null || resultItem.type == Material.AIR)) {
                return false
            }
            // now resultItemStack is not null
            inventory.toList().subList(1, inventory.size)
                .forEachIndexed { index, itemStack ->
                    itemStack.amount--
                    inventory.setItem(index, itemStack)
                }
        }
        Bukkit.getScheduler().runTaskLater(CustomCraft.self, this::putOutputItem, 1)
        return true
    }

    override fun onDrag(event: InventoryDragEvent): Boolean {
        Bukkit.getScheduler().runTaskLater(CustomCraft.self, this::putOutputItem, 1)
        return true
    }

    private fun putOutputItem() {
        val recipe = RECIPE_REGISTRY.firstOrNull {
            it.predicate.test(recipeInfo)
        }
        if (recipe != null) {
            inventory.setItem(0, recipe.result[0])
        } else {
            inventory.setItem(0, null)
        }
    }

    override fun onClose(event: InventoryCloseEvent?) {
        inventory.toList().subList(1, inventory.size)
            .filter { it != null && it.type != Material.AIR }
            .forEach { player.inventory.addItem(it) }
        inventory.setItem(0, null)
    }
}