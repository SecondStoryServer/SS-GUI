package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material

object StatusPage: Page {
    override val display = "ステータス"
    override val icon = Material.BLAZE_POWDER
    override val item = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}