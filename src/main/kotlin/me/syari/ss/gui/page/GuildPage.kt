package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material

object GuildPage: Page {
    override val display = "ギルド"
    override val icon = Material.ARMOR_STAND
    override val item = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}