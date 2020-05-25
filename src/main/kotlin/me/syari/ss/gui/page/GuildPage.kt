package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material
import org.bukkit.entity.Player

object GuildPage: Page {
    override val display = "ギルド"
    override val icon = Material.ARMOR_STAND
    override fun getItem(player: Player) = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}