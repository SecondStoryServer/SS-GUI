package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.chest.ItemChest
import me.syari.ss.item.chest.PlayerChestData.Companion.chestData
import org.bukkit.Material
import org.bukkit.entity.Player

object ItemPage: Page {
    override val display = "アイテム"
    override val icon = Material.LEATHER

    override fun getItem(player: Player): Map<Int, Pair<CustomItemStack, (() -> Unit)?>> {
        return mutableMapOf<Int, Pair<CustomItemStack, (() -> Unit)?>>().also { map ->
            val chestData = player.chestData
            val generalChest = chestData.general
            val generalChestIcon = CustomItemStack.create(Material.RED_TULIP, "&6一般")
            for (page in 1..generalChest.maxPage) {
                map[page + 11] = generalChestIcon to { openGeneralChest(player, generalChest, page) }
            }
            val equipChest = chestData.equip
            val equipChestIcon = CustomItemStack.create(Material.IRON_CHESTPLATE, "&6装備")
            for (page in 1..equipChest.maxPage) {
                map[page + 20] = equipChestIcon to { openEquipChest(player, equipChest, page) }
            }
            val compassChest = chestData.compass
            val compassChestIcon = CustomItemStack.create(Material.COMPASS, "&6コンパス")
            for (page in 1..compassChest.maxPage) {
                map[page + 29] = compassChestIcon to { openCompassChest(player, compassChest, page) }
            }
        }
    }

    private fun openGeneralChest(player: Player, generalChest: ItemChest.General, page: Int) {
        generalChest.checkSort()

    }

    private fun openEquipChest(player: Player, equipChest: ItemChest.Equip, page: Int) {
        equipChest.checkSort()

    }

    private fun openCompassChest(player: Player, compassChest: ItemChest.Compass, page: Int) {

    }
}