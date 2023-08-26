package cn.remering.plugin.customcraft

import cn.remering.plugin.customcraft.command.registerCommands
import cn.remering.plugin.customcraft.command.unregisterCommands
import cn.remering.plugin.customcraft.config.Config
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin

class CustomCraft : JavaPlugin() {

    companion object {
        lateinit var self: CustomCraft
    }

    override fun onLoad() {
        self = this
        if (!CommandAPI.isLoaded()) {
            CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(true))
        }
    }

    override fun onEnable() {
        CommandAPI.onEnable()
        registerCommands()
        Config.load()
    }

    override fun onDisable() {
        unregisterCommands()
        CommandAPI.onDisable()
    }
}
