package me.syari.ss.gui.vanilla

import me.syari.ss.core.Main.Companion.console
import me.syari.ss.core.config.CreateConfig.config
import me.syari.ss.core.config.CustomConfig
import me.syari.ss.core.config.dataType.ConfigDataType
import me.syari.ss.core.item.InventoryBase64
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.gui.Main.Companion.guiPlugin
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

data class VanillaInventoryPlayer(val uuidPlayer: UUIDPlayer) {
    private var nullableConfig: CustomConfig? = null
    private var contentsCache: Array<ItemStack?>? = null

    private fun getConfigOrCreate(): CustomConfig {
        return nullableConfig ?: {
            config(guiPlugin, console, "VanillaInventory/${uuidPlayer}.yml").apply {
                nullableConfig = this
            }
        }.invoke()
    }

    fun load(): Array<ItemStack?>? {
        return if (contentsCache == null) {
            val base64 = getConfigOrCreate().get("base64", ConfigDataType.STRING, false) ?: return null
            val inventory = InventoryBase64.getInventoryFromBase64(base64)
            inventory.contents.apply {
                contentsCache = this
            }
        } else {
            contentsCache
        }
    }

    fun save(playerInventory: PlayerInventory) {
        val contents = playerInventory.contents
        if (contents.filterNotNull().isNotEmpty()) {
            val base64 = InventoryBase64.toBase64(contents)
            getConfigOrCreate().set("base64", base64, true)
        } else {
            nullableConfig?.let {
                it.delete()
                nullableConfig = null
            }
        }
        contentsCache = contents
    }
}