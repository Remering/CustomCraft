package cn.remering.customcraft.recipe

import cn.remering.customcraft.predicate.RecipePredicate
import org.bukkit.Keyed
import org.bukkit.configuration.serialization.ConfigurationSerializable

interface Recipe: ConfigurationSerializable, Keyed, Comparable<Recipe> {
    val predicate: RecipePredicate
    val priority: Int
        get() = 0

    override fun compareTo(other: Recipe) = priority - other.priority
}
