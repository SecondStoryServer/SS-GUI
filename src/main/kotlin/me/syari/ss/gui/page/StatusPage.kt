package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material
import org.bukkit.entity.Player

object StatusPage: Page {
    override val display = "ステータス"
    override val icon = Material.BLAZE_POWDER
    override fun getItem(player: Player) = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}