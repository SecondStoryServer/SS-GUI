package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material
import org.bukkit.entity.Player

object FriendPage: Page {
    override val display = "フレンド"
    override val icon = Material.PLAYER_HEAD
    override fun getItem(player: Player) = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}