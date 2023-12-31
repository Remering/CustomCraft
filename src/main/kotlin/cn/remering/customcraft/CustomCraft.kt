package cn.remering.customcraft

import cn.remering.customcraft.command.registerCommands
import cn.remering.customcraft.command.unregisterCommands
import cn.remering.customcraft.config.Config
import cn.remering.customcraft.registry.registerConfigSerialization
import cn.remering.customcraft.registry.registerRecipePredicateBuilders
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import mc.obliviate.inventory.InventoryAPI
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class CustomCraft : JavaPlugin() {

    companion object {
        lateinit var self: CustomCraft
        lateinit var inventoryApi: InventoryAPI
    }

    override fun onLoad() {
        self = this
        if (!CommandAPI.isLoaded()) {
            CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(true))
        }
        registerConfigSerialization()
        registerRecipePredicateBuilders()
    }

    override fun onEnable() {
        CommandAPI.onEnable()
        inventoryApi = InventoryAPI(this).apply {
            init()
        }
        registerCommands()
        Config.load()
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers()
            .mapNotNull { inventoryApi.getPlayersCurrentGui(it) }
            .filter { !it.isClosed }
            .forEach { it.player.closeInventory() }
        unregisterCommands()
        CommandAPI.onDisable()
    }
}
