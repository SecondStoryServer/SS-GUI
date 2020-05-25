package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material
import org.bukkit.entity.Player

object OtherPage: Page {
    override val display = "その他"
    override val icon = Material.COMPASS
    override fun getItem(player: Player) = mapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>()
}