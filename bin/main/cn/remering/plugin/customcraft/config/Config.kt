package cn.remering.plugin.customcraft.config

import cn.remering.plugin.customcraft.CustomCraft
import java.nio.file.Path

const val CONFIG_PATH = "config.yml"

object Config {
    private val path: Path = CustomCraft.self.dataFolder.toPath().resolve(CONFIG_PATH)
    fun load() {
        CustomCraft.self.saveResource(CONFIG_PATH, false)
    }
}