package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material
import org.bukkit.entity.Player

object PartyPage: Page {
    override val display = "パーティー"
    override val icon = Material.IRON_HORSE_ARMOR
    override fun getItem(player: Player) = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}