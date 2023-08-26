package cn.remering.customcraft.predicate.player

import cn.remering.customcraft.predicate.*
import org.bukkit.NamespacedKey

private const val PERMISSION_PATH = "permission"
private const val NAMESPACED_KEY = "player-has-permission"
class PlayerHasPermissionPredicate (
    private val permission: String
): RecipePredicate<PlayerHasPermissionPredicate> {
    @Suppress("DEPRECATION")
    companion object: AbstractRecipePredicateBuilder<PlayerHasPermissionPredicate>(
        NamespacedKey(CUSTOM_CRAFT_NAMESPACE, NAMESPACED_KEY)
    ) {
        override fun build(map: Map<String, Any>): PlayerHasPermissionPredicate? {
            if (!validateKey(map)) return null
            val permission = map[PERMISSION_PATH] as String? ?: return null
            return PlayerHasPermissionPredicate(permission)
        }


    }

    override fun validate(): String? = null


    override fun test(t: RecipeInfo) = t.human.hasPermission(permission)

    override fun serialize(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        putKey(map)
        map[PERMISSION_PATH] = permission
        return map
    }
}