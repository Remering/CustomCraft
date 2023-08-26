package cn.remering.plugin.customcraft.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

object EventListener: Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        event.whoClicked.sendMessage("You clicked!")
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        event.whoClicked.sendMessage("You drag!")
    }

    @EventHandler
    fun onCraftItem(event: CraftItemEvent) {
        event.whoClicked.hasMetadata()
    }
}