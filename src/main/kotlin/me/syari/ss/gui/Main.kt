package me.syari.ss.gui

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.core.auto.Event
import me.syari.ss.core.command.create.CreateCommand.createCommand
import me.syari.ss.core.command.create.CreateCommand.element
import me.syari.ss.core.command.create.CreateCommand.tab
import me.syari.ss.core.command.create.ErrorMessage
import me.syari.ss.gui.page.ItemPage.openCompassChest
import me.syari.ss.gui.page.ItemPage.openEquipChest
import me.syari.ss.gui.page.ItemPage.openGeneralChest
import me.syari.ss.item.ItemRarity
import me.syari.ss.item.chest.PlayerChestData.Companion.chestData
import me.syari.ss.item.compass.CompassItem
import me.syari.ss.item.equip.weapon.EnhancedWeaponItem
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType
import me.syari.ss.item.general.potion.HealPotion
import org.bukkit.Location
import org.bukkit.Material
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
        val testSword = WeaponItem.create(
            WeaponType.Sword,
            "test-sword",
            Material.IRON_SWORD,
            "&atest-sword",
            "テスト用",
            ItemRarity.Rare,
            ElementType.Wood,
            2F,
            0.5F,
            2F
        )
        testSword.register()
        val testWand = WeaponItem.create(
            WeaponType.Wand,
            "test-wand",
            Material.WOODEN_HOE,
            "&dtest-wand",
            "テスト用",
            ItemRarity.UltraRare,
            ElementType.Dark,
            10F,
            0F,
            3F
        )
        testWand.register()
        val nullLocation = Location(null, 0.0, 0.0, 0.0)
        val compassItem1 = CompassItem("compass-1", "コンパス１", nullLocation)
        val compassItem2 = CompassItem("compass-2", "コンパス２", nullLocation)
        val compassItem3 = CompassItem("compass-3", "コンパス３", nullLocation)
        compassItem1.register()
        compassItem2.register()
        compassItem3.register()
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
                    chestData.equip.add(EnhancedWeaponItem(testSword, 50))
                    chestData.equip.add(EnhancedWeaponItem(testSword, 50))
                    chestData.equip.add(EnhancedWeaponItem(testSword, 30))
                    chestData.equip.add(EnhancedWeaponItem(testWand, 100))
                    chestData.equip.add(EnhancedWeaponItem(testWand, 70))
                    chestData.equip.add(EnhancedWeaponItem(testWand, 20))
                    chestData.compass.add(compassItem1)
                    chestData.compass.add(compassItem3)
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