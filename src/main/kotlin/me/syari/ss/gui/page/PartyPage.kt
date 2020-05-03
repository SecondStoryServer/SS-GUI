package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material

object PartyPage: Page {
    override val display = "パーティー"
    override val icon = Material.IRON_HORSE_ARMOR
    override val item = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}