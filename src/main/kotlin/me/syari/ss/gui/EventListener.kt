package me.syari.ss.gui

import me.syari.ss.core.auto.Event
import me.syari.ss.core.inventory.event.NaturalInventoryOpenEvent
import me.syari.ss.gui.page.Page.Companion.updatePageItem
import org.bukkit.event.EventHandler

object EventListener: Event {
    @EventHandler
    fun on(e: NaturalInventoryOpenEvent) {
        val player = e.player
        updatePageItem(player)
    }
}