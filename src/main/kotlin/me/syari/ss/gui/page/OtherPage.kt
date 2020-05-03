package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material

object OtherPage: Page {
    override val display = "その他"
    override val icon = Material.COMPASS
    override val item = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}