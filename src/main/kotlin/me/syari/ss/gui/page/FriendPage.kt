package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material

object FriendPage: Page {
    override val display = "フレンド"
    override val icon = Material.PLAYER_HEAD
    override val item = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}