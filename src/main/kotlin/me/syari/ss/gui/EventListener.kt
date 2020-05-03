package me.syari.ss.gui

import me.syari.ss.core.auto.Event
import me.syari.ss.gui.page.Page
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent

object EventListener: Event {
    @EventHandler
    fun on(e: PlayerJoinEvent){
        val player = e.player
        Page.firstPage.apply(player)
    }
}