package me.syari.ss.gui

import me.syari.ss.core.auto.Event
import me.syari.ss.gui.page.Page.Companion.page
import me.syari.ss.item.vanilla.VanillaInventory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object EventListener: Event {
    @EventHandler
    fun on(e: PlayerJoinEvent) {
        val player = e.player
        if (VanillaInventory.isEnableGameMode(player.gameMode)) {
            VanillaInventory.load(player)
        } else {
            player.page.updateItem(player)
        }
    }

    @EventHandler
    fun on(e: PlayerQuitEvent) {
        val player = e.player
        if (VanillaInventory.isEnableGameMode(player.gameMode)) {
            VanillaInventory.save(player)
        }
    }

    @EventHandler
    fun on(e: PlayerGameModeChangeEvent) {
        val player = e.player
        val gameMode = player.gameMode
        val newGameMode = e.newGameMode
        if (VanillaInventory.isEnableGameMode(gameMode)) {
            VanillaInventory.save(player)
        }
        if (VanillaInventory.isEnableGameMode(newGameMode)) {
            VanillaInventory.load(player)
        } else {
            player.page.updateItem(player)
        }
    }

    @EventHandler
    fun on(e: InventoryClickEvent) {
        if (e.clickedInventory?.type != InventoryType.PLAYER) return
        val player = e.whoClicked as Player
        if (VanillaInventory.isEnableGameMode(player.gameMode)) return
        e.isCancelled = true
        player.page.runClickEvent(e.slot)
    }
}