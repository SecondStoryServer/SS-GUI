package me.syari.ss.gui

import me.syari.ss.core.auto.Event
import me.syari.ss.core.command.create.CreateCommand.createCommand
import me.syari.ss.core.command.create.CreateCommand.element
import me.syari.ss.core.command.create.CreateCommand.tab
import me.syari.ss.core.command.create.ErrorMessage
import me.syari.ss.gui.page.ItemPage.openCompassChest
import me.syari.ss.gui.page.ItemPage.openEquipChest
import me.syari.ss.gui.page.ItemPage.openGeneralChest
import me.syari.ss.item.chest.PlayerChestData.Companion.chestData
import me.syari.ss.item.general.potion.HealPotion
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object {
        internal lateinit var guiPlugin: JavaPlugin
    }

    override fun onEnable() {
        guiPlugin = this
        Event.register(this, EventListener)
        test()
    }

    private fun test() {
        createCommand(this,
            "test-gui",
            "SS-GUI-Test",
            tab { _, _ -> element("get-item", "open") },
            tab("open") { _, _ -> element("general", "equip", "compass") }) { sender, args ->
            if (sender !is Player) return@createCommand sendError(ErrorMessage.OnlyPlayer)
            val chestData = sender.chestData
            when (args.whenIndex(0)) {
                "get-item" -> {
                    sendWithPrefix("get")
                    HealPotion.Size.values().forEachIndexed { index, size ->
                        chestData.general.add(HealPotion(size), index * 10 + 5)
                    }
                }
                "open" -> {
                    sendWithPrefix("open")
                    when (args.whenIndex(1)) {
                        "general" -> openGeneralChest(sender, chestData.general, 1)
                        "equip" -> openEquipChest(sender, chestData.equip, 1)
                        "compass" -> openCompassChest(sender, chestData.compass, 1)
                        else -> sendWithPrefix("not found")
                    }
                }
            }
        }
    }
}