package cn.remering.customcraft.registry

import cn.remering.customcraft.recipe.Recipe
import cn.remering.customcraft.predicate.RecipePredicateBuilder
import cn.remering.customcraft.predicate.player.PlayerHasPermissionPredicate
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.Registry

interface RegistrableRegistry<T: Keyed>: Registry<T> {
    fun register(value: T): Boolean
    fun unregister(key: NamespacedKey): Boolean
    fun unregister(value: T): Boolean
}

interface UniqueKeyRegistry<T: Keyed>: RegistrableRegistry<T> {
    fun contains(key: NamespacedKey): Boolean
}

class MapRegistry<T: Keyed>(
    private val storage: MutableMap<NamespacedKey, T> = mutableMapOf()
): UniqueKeyRegistry<T> {

    override fun unregister(value: T): Boolean {
        if (!contains(value.key)) return false
        return storage.remove(value.key, value)
    }

    override fun register(value: T): Boolean {
        if (contains(value.key)) return false
        storage[value.key] = value
        return true
    }

    override fun contains(key: NamespacedKey) = storage.containsKey(key)

    override fun unregister(key: NamespacedKey) = storage.remove(key) == null

    override fun iterator() = storage.values.iterator()

    override fun get(key: NamespacedKey) = storage[key]
}

val RECIPE_PREDICATE_BUILDER_REGISTRY = MapRegistry<RecipePredicateBuilder<*>>()

fun registerRecipePredicateBuilders() {
    RECIPE_PREDICATE_BUILDER_REGISTRY.register(PlayerHasPermissionPredicate)
}

val RECIPE_REGISTER = MapRegistry<Recipe>()