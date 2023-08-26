package cn.remering.customcraft.predicate.player

import cn.remering.customcraft.predicate.*
import org.bukkit.NamespacedKey

private const val PERMISSION_PATH = "permission"
private const val NAMESPACED_KEY = "player-has-permission"
class PlayerHasPermissionPredicate private constructor(
    private val permission: String
): RecipePredicate {
    companion object: AbstractRecipePredicateBuilder<PlayerHasPermissionPredicate>(
        NamespacedKey(CUSTOM_CRAFT_NAMESPACE, NAMESPACED_KEY)
    ) {
        override fun serialize(map: Map<String, Any>): PlayerHasPermissionPredicate? {
            val keyStr = map[KEY_PATH] ?: return null
            if (keyStr != key.toString()) return null
            val permission = map[PERMISSION_PATH] as String? ?: return null
            return PlayerHasPermissionPredicate(permission)
        }

        override fun deserialize(predicate: PlayerHasPermissionPredicate): Map<String, Any> {
            val map = mutableMapOf<String, Any>()
            map[KEY_PATH] = key.toString()
            map[PERMISSION_PATH] = predicate.permission
            return map
        }
    }

    override fun validate(): String? = null

    override fun test(t: RecipeInfo) = t.human.hasPermission(permission)
}