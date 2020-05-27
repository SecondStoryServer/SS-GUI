package me.syari.ss.gui

import me.syari.ss.core.auto.Event
import me.syari.ss.gui.page.Page.Companion.page
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerJoinEvent

object EventListener: Event {
    @EventHandler
    fun on(e: PlayerJoinEvent) {
        val player = e.player
        player.page.updateItem(player)
    }

    @EventHandler
    fun on(e: InventoryClickEvent) {
        if (e.clickedInventory?.type != InventoryType.PLAYER) return
        e.isCancelled = true
        val player = e.whoClicked as Player
        player.page.runClickEvent(e.slot)
    }
}