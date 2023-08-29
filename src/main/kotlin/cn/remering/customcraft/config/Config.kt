package cn.remering.customcraft.config

import cn.remering.customcraft.CustomCraft
import cn.remering.customcraft.recipe.Recipe
import cn.remering.customcraft.registry.RECIPE_REGISTRY
import org.bukkit.configuration.file.YamlConfiguration

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
        loadRecipes()
    }

    private fun loadRecipes() {
        recipeYamlConfig.getKeys(false).map {
            recipeYamlConfig[it]
        }.filterIsInstance<Recipe>()
        .forEach { RECIPE_REGISTRY.register(it) }
    }

    fun saveRecipes() {
        recipeYamlConfig = YamlConfiguration()
        RECIPE_REGISTRY.forEach {
            recipeYamlConfig.set(it.key.toString(), it)
        }
        recipeYamlConfig.save(recipePath.toFile())
    }
}
