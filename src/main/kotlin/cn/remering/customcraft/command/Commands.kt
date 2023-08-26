package cn.remering.customcraft.command

import cn.remering.customcraft.OPEN_PERMISSION
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerExecutionInfo


private val openCommand = CommandAPICommand("open")
    .withPermission(OPEN_PERMISSION)
    .executesPlayer(PlayerExecutionInfo {
        it.sender().openWorkbench(null, true)
    })

fun registerCommands() {
    CommandAPICommand("customcraft")
        .withSubcommand(openCommand)
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


