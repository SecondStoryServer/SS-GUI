package me.syari.ss.gui.vanilla

import me.syari.ss.core.player.UUIDPlayer
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

object VanillaInventory {
    fun isEnableGameMode(gameMode: GameMode): Boolean {
        return gameMode in listOf(GameMode.CREATIVE, GameMode.SPECTATOR)
    }

    private val inventoryPlayerMap = mutableMapOf<UUIDPlayer, VanillaInventoryPlayer>()

    private val OfflinePlayer.vanillaInventory
        get() = UUIDPlayer(this).vanillaInventory

    private val UUIDPlayer.vanillaInventory
        get() = inventoryPlayerMap.getOrPut(this) { VanillaInventoryPlayer(this) }

    fun load(player: Player) {
        val inventoryPlayer = player.vanillaInventory
        val contents = inventoryPlayer.load() ?: return
        player.inventory.contents = contents
    }

    fun save(player: Player) {
        val inventoryPlayer = player.vanillaInventory
        inventoryPlayer.save(player.inventory)
    }
}