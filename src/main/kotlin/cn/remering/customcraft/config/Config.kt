package cn.remering.customcraft.config

import cn.remering.customcraft.CustomCraft
import cn.remering.customcraft.predicate.logic.AndPredicate
import cn.remering.customcraft.predicate.player.PlayerHasPermissionPredicate
import cn.remering.customcraft.recipe.Recipe
import cn.remering.customcraft.recipe.SimpleRecipe
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.nio.file.Path

const val CONFIG_PATH = "config.yml"
const val RECIPE_PATH = "recipe.yml"

object Config {
    private val configPath = CustomCraft.self.dataFolder.toPath().resolve(CONFIG_PATH)
    private val recipePath = CustomCraft.self.dataFolder.toPath().resolve(RECIPE_PATH)
    private lateinit var recipeYamlConfig: YamlConfiguration
    fun load() {
        CustomCraft.self.saveResource(CONFIG_PATH, false)
        CustomCraft.self.saveResource(RECIPE_PATH, false)
        recipeYamlConfig = YamlConfiguration.loadConfiguration(recipePath.toFile())

        val keys = recipeYamlConfig.getKeys(false)
        println(keys)
        keys.map { recipeYamlConfig.get(it) }
            .filterIsInstance<Recipe>()
            .forEach { CustomCraft.self.logger.info(it.toString()) }
    }
}
