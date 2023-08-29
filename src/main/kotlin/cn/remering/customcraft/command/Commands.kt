package cn.remering.customcraft.command

import cn.remering.customcraft.EDIT_PERMISSION
import cn.remering.customcraft.OPEN_PERMISSION
import cn.remering.customcraft.gui.CraftRecipeGui
import cn.remering.customcraft.gui.EditRecipeGui
import cn.remering.customcraft.registry.RECIPE_REGISTRY
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.SafeSuggestions
import dev.jorel.commandapi.executors.PlayerExecutionInfo
import java.util.function.Function


private val openCommand = CommandAPICommand("open")
    .withPermission(OPEN_PERMISSION)
    .executesPlayer(PlayerExecutionInfo {
        val player = it.sender()
        val craftGui = CraftRecipeGui(player)
        craftGui.open()
    })

private val editCommand = CommandAPICommand("edit")
    .withPermission(EDIT_PERMISSION)
    .withArguments(GreedyStringArgument("key").replaceSafeSuggestions((SafeSuggestions.suggest {
        val suggestions = mutableListOf<String>()
        val iterator = RECIPE_REGISTRY.iterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()
            if (it.currentArg == "" || recipe.key.key.startsWith(it.currentArg)) {
                suggestions += recipe.key.key
            }
        }
        suggestions.toTypedArray()
    })))
    .executesPlayer(PlayerExecutionInfo {
        val player = it.sender()
        val editGui = EditRecipeGui(player, it.args().getUnchecked<String>("key")!!)
        editGui.open()
    })

fun registerCommands() {
    CommandAPICommand("customcraft")
        .withSubcommand(openCommand)
        .withSubcommand(editCommand)
        .shouldUnregister()
        .register()
}

private val shouldUnregisterCommandStringList = mutableListOf<String>()

private fun CommandAPICommand.shouldUnregister(): CommandAPICommand {
    shouldUnregisterCommandStringList += name
    return this
}

fun unregisterCommands() {
    shouldUnregisterCommandStringList.forEach(CommandAPI::unregister)
}


