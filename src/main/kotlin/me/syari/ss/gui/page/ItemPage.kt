package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material

object ItemPage: Page {
    override val display = "アイテム"
    override val icon = Material.LEATHER
    override val item = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}