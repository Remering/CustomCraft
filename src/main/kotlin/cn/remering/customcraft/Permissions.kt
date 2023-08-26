package cn.remering.customcraft


private const val PERMISSION_PREFIX = "customcraft"

private fun permission(node: String): String = "$PERMISSION_PREFIX.$node"

private fun commandPermission(node: String) = permission("command.$node")

val OPEN_PERMISSION = commandPermission("open")