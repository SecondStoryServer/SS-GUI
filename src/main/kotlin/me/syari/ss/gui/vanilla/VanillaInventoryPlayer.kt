package me.syari.ss.gui.vanilla

import me.syari.ss.core.Main.Companion.console
import me.syari.ss.core.config.CreateConfig
import me.syari.ss.core.config.dataType.ConfigDataType
import me.syari.ss.core.item.InventoryBase64
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.gui.Main.Companion.guiPlugin
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

data class VanillaInventoryPlayer(val uuidPlayer: UUIDPlayer) {
    private val config = CreateConfig.config(guiPlugin, console, "VanillaInventory/${uuidPlayer}.yml")
    private var contentsCache: Array<ItemStack?>? = null

    fun load(): Array<ItemStack?>? {
        return if (contentsCache == null) {
            val base64 = config.get("base64", ConfigDataType.STRING, false) ?: return null
            val inventory = InventoryBase64.getInventoryFromBase64(base64)
            inventory.contents.apply {
                contentsCache = this
            }
        } else {
            contentsCache
        }
    }

    fun save(playerInventory: PlayerInventory) {
        val base64 = InventoryBase64.toBase64(playerInventory)
        config.set("base64", base64, true)
        contentsCache = playerInventory.contents
    }
}