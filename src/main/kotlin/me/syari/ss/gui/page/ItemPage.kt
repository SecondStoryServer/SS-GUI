package me.syari.ss.gui.page

import me.syari.ss.core.code.StringEditor.toUncolor
import me.syari.ss.core.inventory.CreateInventory.inventory
import me.syari.ss.core.inventory.CustomInventory
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.core.message.Message.action
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.scheduler.CustomScheduler.runLater
import me.syari.ss.gui.Main.Companion.guiPlugin
import me.syari.ss.item.chest.ItemChest
import me.syari.ss.item.chest.PlayerChestData.Companion.chestData
import me.syari.ss.item.holder.ItemHolder
import me.syari.ss.item.holder.ItemHolder.Companion.itemHolder
import me.syari.ss.item.itemRegister.equip.EnhancedEquipItem
import me.syari.ss.item.itemRegister.equip.armor.EnhancedArmorItem
import me.syari.ss.item.itemRegister.equip.weapon.EnhancedWeaponItem
import me.syari.ss.item.itemRegister.general.GeneralItemWithAmount
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag

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

    private data class SelectableGeneralItem(val item: GeneralItemWithAmount) {
        var selectCount = 0
            set(value) {
                field = when {
                    value < 0 -> 0
                    value < item.amount -> value
                    else -> item.amount
                }
            }
    }

    /* private */ fun openGeneralChest(player: Player, generalChest: ItemChest.General, page: Int) {
        val maxPage = generalChest.maxPage
        if (page !in 1..maxPage) return
        if (!generalChest.isSorted) generalChest.sort()
        val sortType = generalChest.sortType
        val itemList = generalChest.getList(page)?.map { SelectableGeneralItem(it) }
        val isReverse = generalChest.isReverse
        var confirmDump = false
        var protectDump = false
        inventory("&9&l通常", 6) {
            fun CustomInventory.updateItemList() {
                val selectList = mutableListOf<String>()
                itemList?.forEachIndexed { index, generalItem ->
                    val amount = generalItem.item.amount
                    val customItem = generalItem.item.data.itemStack.clone {
                        this.amount = amount
                        val selectCount = generalItem.selectCount
                        display += if (selectCount != 0) {
                            addEnchant(Enchantment.DURABILITY, 0)
                            addItemFlag(ItemFlag.HIDE_ENCHANTS)
                            selectList.add("&7- &a$display &a(x$selectCount)")
                            " &7x$amount &a(x$selectCount)"
                        } else {
                            " &7x$amount"
                        }
                    }
                    item(18 + index, customItem).event(ClickType.SHIFT_LEFT) {
                        generalItem.selectCount += 10
                    }.event(ClickType.LEFT) {
                        generalItem.selectCount += 1
                    }.event(ClickType.SHIFT_RIGHT) {
                        generalItem.selectCount -= 10
                    }.event(ClickType.RIGHT) {
                        generalItem.selectCount -= 1
                    }.event(ClickType.MIDDLE) {
                        generalItem.selectCount = 0
                    }.event {
                        updateItemList()
                    }
                }
                val dumpMessage = if (confirmDump) "&c本当に捨てますか？" else "&c選択したアイテムを捨てる"
                item(49, Material.LAVA_BUCKET, dumpMessage, selectList, shine = confirmDump).event {
                    if (protectDump) return@event
                    if (confirmDump) {
                        itemList?.forEach { generalItem ->
                            generalChest.remove(generalItem.item.data, generalItem.selectCount)
                            generalItem.selectCount = 0
                        }
                        openGeneralChest(player, generalChest, page)
                    } else {
                        confirmDump = true
                        updateItemList()
                        runLater(guiPlugin, 100) {
                            if (confirmDump) {
                                confirmDump = false
                                updateItemList()
                            }
                        }
                    }
                    protectDump = true
                    runLater(guiPlugin, 20) {
                        protectDump = false
                    }
                }
                open(player)
            }

            item(0..6, Material.GRAY_STAINED_GLASS_PANE)

            fun changeSort(changeTo: ItemChest.General.SortType) {
                if (sortType == changeTo) {
                    generalChest.isReverse = !isReverse
                } else {
                    generalChest.sortType = changeTo
                }
                openGeneralChest(player, generalChest, page)
            }

            item(7, Material.YELLOW_STAINED_GLASS_PANE, "&7並び替え &6種類", shine = sortType.isType).event {
                changeSort(ItemChest.General.SortType.Type)
            }
            item(8, Material.YELLOW_STAINED_GLASS_PANE, "&7並び替え &6レア度", shine = sortType.isRarity).event {
                changeSort(ItemChest.General.SortType.Rarity)
            }

            item(9..17, Material.BLACK_STAINED_GLASS_PANE)
            item(45, Material.ARROW, "&6<<<").event {
                openGeneralChest(player, generalChest, page - 1)
            }
            item(46..48, Material.BLACK_STAINED_GLASS_PANE)
            item(50..52, Material.BLACK_STAINED_GLASS_PANE)
            item(53, Material.ARROW, "&6>>>").event {
                openGeneralChest(player, generalChest, page + 1)
            }
            updateItemList()
        }
    }

    private enum class DisplayEquipType {
        Armor,
        ExtraWeapon;

        fun next(): DisplayEquipType {
            return when (this) {
                Armor -> ExtraWeapon
                ExtraWeapon -> Armor
            }
        }
    }

    private val displayEquipTypeList = mutableMapOf<UUIDPlayer, DisplayEquipType>()

    private var Player.displayEquipType
        get() = displayEquipTypeList.getOrDefault(UUIDPlayer(this), DisplayEquipType.Armor)
        set(value) {
            displayEquipTypeList[UUIDPlayer(this)] = value
        }

    private data class SelectableEquipItem(val item: EnhancedEquipItem) {
        var isSelected = false
    }

    /* private */ fun openEquipChest(player: Player, equipChest: ItemChest.Equip, page: Int) {
        val maxPage = equipChest.maxPage
        if (page !in 1..maxPage) return
        if (!equipChest.isSorted) equipChest.sort()
        val sortType = equipChest.sortType
        val isReverse = equipChest.isReverse
        val displayEquipType = player.displayEquipType
        val itemList = equipChest.getList(page)?.map { SelectableEquipItem(it) }
        var confirmDump = false
        var protectDump = false
        var overrideClickEvent: ((EnhancedEquipItem) -> Unit)? = null
        var overrideClickEventSlot: Int? = null
        inventory("&9&l装備", 6) {
            fun CustomInventory.updateItemList() {
                val itemHolder = player.itemHolder
                when (displayEquipType) {
                    DisplayEquipType.Armor -> {
                        val changeArmor = { armorSlot: ItemHolder.ArmorSlot ->
                            if (overrideClickEventSlot == armorSlot.slot && overrideClickEvent != null) {
                                overrideClickEvent = null
                                updateItemList()
                            } else {
                                overrideClickEventSlot = armorSlot.slot
                                overrideClickEvent = {
                                    if (it is EnhancedArmorItem && armorSlot.isAvailable(it.data)) {
                                        itemHolder.setArmorItem(armorSlot, it)
                                        overrideClickEvent = null
                                        updateItemList()
                                    } else {
                                        player.action("&c&l装備できないアイテムです")
                                    }
                                }
                            }
                        }

                        val emptyArmorSlot = ItemHolder.ArmorSlot.values().toMutableList()
                        itemHolder.allArmorItem.forEach { (armorSlot, it) ->
                            emptyArmorSlot.remove(armorSlot)
                            item(armorSlot.slot, it.itemStack.clone {
                                if (overrideClickEventSlot == armorSlot.slot) {
                                    addEnchant(Enchantment.DURABILITY, 0)
                                    addItemFlag(ItemFlag.HIDE_ENCHANTS)
                                }
                            }).event(ClickType.SHIFT_RIGHT, ClickType.SHIFT_LEFT) {
                                itemHolder.setArmorItem(armorSlot, null)
                                updateItemList()
                            }.event(ClickType.RIGHT, ClickType.LEFT) {
                                changeArmor.invoke(armorSlot)
                            }
                        }
                        emptyArmorSlot.forEach { armorSlot ->
                            val empty = CustomItemStack.create(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "").apply {
                                if (overrideClickEventSlot == armorSlot.slot) {
                                    addEnchant(Enchantment.DURABILITY, 0)
                                    addItemFlag(ItemFlag.HIDE_ENCHANTS)
                                }
                            }
                            item(armorSlot.slot, empty).event {
                                changeArmor.invoke(armorSlot)
                            }
                        }
                    }
                    DisplayEquipType.ExtraWeapon -> {
                        val changeExtraWeapon = { slot: Int ->
                            if (overrideClickEventSlot == slot && overrideClickEvent != null) {
                                overrideClickEvent = null
                                updateItemList()
                            } else {
                                overrideClickEventSlot = slot
                                overrideClickEvent = {
                                    if (it is EnhancedWeaponItem) {
                                        itemHolder.setExtraWeaponItem(slot, it)
                                        overrideClickEvent = null
                                        updateItemList()
                                    } else {
                                        player.action("&c&l装備できないアイテムです")
                                    }
                                }
                            }
                        }

                        val extraWeaponItem = itemHolder.allExtraWeaponItem
                        extraWeaponItem.forEach { (slot, it) ->
                            item(slot, it.itemStack.apply {
                                if (overrideClickEventSlot == slot) {
                                    addEnchant(Enchantment.DURABILITY, 0)
                                    addItemFlag(ItemFlag.HIDE_ENCHANTS)
                                }
                            }).event(ClickType.SHIFT_RIGHT, ClickType.SHIFT_LEFT) {
                                itemHolder.setExtraWeaponItem(slot, null)
                                updateItemList()
                            }.event(ClickType.RIGHT, ClickType.LEFT) {
                                changeExtraWeapon.invoke(slot)
                            }
                        }
                        val size = extraWeaponItem.size
                        val emptySlot = size..3
                        emptySlot.forEach { slot ->
                            val empty = CustomItemStack.create(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "").apply {
                                if (overrideClickEventSlot == slot) {
                                    addEnchant(Enchantment.DURABILITY, 0)
                                    addItemFlag(ItemFlag.HIDE_ENCHANTS)
                                }
                            }
                            item(slot, empty).event {
                                changeExtraWeapon.invoke(slot)
                            }
                        }
                    }
                }

                val selectList = mutableListOf<String>()
                itemList?.forEachIndexed { index, equipItem ->
                    item(18 + index, equipItem.item.itemStack.clone {
                        if (equipItem.isSelected) {
                            addEnchant(Enchantment.DURABILITY, 0)
                            addItemFlag(ItemFlag.HIDE_ENCHANTS)
                        }
                    }).event {
                        equipItem.isSelected = true
                        updateItemList()
                    }
                }
                val dumpMessage = if (confirmDump) "&c本当に捨てますか？" else "&c選択したアイテムを捨てる"
                item(49, Material.LAVA_BUCKET, dumpMessage, selectList, shine = confirmDump).event {
                    if (protectDump) return@event
                    if (confirmDump) {
                        itemList?.forEach { equipItem ->
                            equipChest.remove(equipItem.item)
                            equipItem.isSelected = false
                        }
                        openEquipChest(player, equipChest, page)
                    } else {
                        confirmDump = true
                        updateItemList()
                        runLater(guiPlugin, 100) {
                            if (confirmDump) {
                                confirmDump = false
                                updateItemList()
                            }
                        }
                    }
                    protectDump = true
                    runLater(guiPlugin, 20) {
                        protectDump = false
                    }
                }
                open(player)
            }

            item(4, material = Material.GRAY_STAINED_GLASS_PANE)

            fun changeSort(changeTo: ItemChest.Equip.SortType) {
                if (sortType == changeTo) {
                    equipChest.isReverse = !isReverse
                } else {
                    equipChest.sortType = changeTo
                }
                openEquipChest(player, equipChest, page)
            }

            item(5, Material.YELLOW_STAINED_GLASS_PANE, "&7並び替え &6種類", shine = sortType.isType).event {
                changeSort(ItemChest.Equip.SortType.Type)
            }
            item(6, Material.YELLOW_STAINED_GLASS_PANE, "&7並び替え &6エンハンス", shine = sortType.isEnhance).event {
                changeSort(ItemChest.Equip.SortType.Enhance)
            }
            item(7, Material.YELLOW_STAINED_GLASS_PANE, "&7並び替え &6レア度", shine = sortType.isRarity).event {
                changeSort(ItemChest.Equip.SortType.Rarity)
            }
            item(8, Material.YELLOW_STAINED_GLASS_PANE, "&7並び替え &6攻撃・防御", shine = sortType.isStatus).event {
                changeSort(ItemChest.Equip.SortType.Status)
            }

            item(9, Material.ORANGE_STAINED_GLASS_PANE, "&6装備切り替え").event {
                player.displayEquipType = displayEquipType.next()

            }

            item(10..17, Material.BLACK_STAINED_GLASS_PANE)
            item(45, Material.LIME_STAINED_GLASS_PANE, "&6<<<").event {
                openEquipChest(player, equipChest, page - 1)
            }
            item(46..48, Material.BLACK_STAINED_GLASS_PANE)
            item(50..52, Material.BLACK_STAINED_GLASS_PANE)
            item(53, Material.LIME_STAINED_GLASS_PANE, "&6>>>").event {
                openEquipChest(player, equipChest, page + 1)
            }
            updateItemList()
        }
    }

    /* private */ fun openCompassChest(player: Player, compassChest: ItemChest.Compass, page: Int) {
        val maxPage = compassChest.maxPage
        if (page !in 1..maxPage) return
        val displayMode = compassChest.displayMode
        val itemList = compassChest.getList(page)
        inventory("&9&lコンパス", 6) {
            item(0..6, Material.GRAY_STAINED_GLASS_PANE)

            fun changeDisplay(changeTo: ItemChest.Compass.DisplayMode) {
                if (displayMode != changeTo) {
                    compassChest.displayMode = changeTo
                }
                openCompassChest(player, compassChest, page)
            }

            item(7, Material.YELLOW_STAINED_GLASS_PANE, "&7表示 &6未所持/所持", shine = displayMode.isBoth).event {
                changeDisplay(ItemChest.Compass.DisplayMode.Both)
            }
            item(8, Material.YELLOW_STAINED_GLASS_PANE, "&7表示 &6所持", shine = displayMode.isOnlyHave).event {
                changeDisplay(ItemChest.Compass.DisplayMode.OnlyHave)
            }

            item(9..17, Material.BLACK_STAINED_GLASS_PANE)
            var index = 18
            itemList?.forEach { compassItem, has ->
                if (has) {
                    item(index, compassItem.itemStack)
                } else {
                    item(
                        index, Material.BARRIER, "&c&k${compassItem.display.toUncolor.replace("\\S+?".toRegex(), "?")}"
                    )
                }
                index++
            }
            item(45, Material.ARROW, "&6<<<").event {
                openCompassChest(player, compassChest, page - 1)
            }
            item(46..52, Material.BLACK_STAINED_GLASS_PANE)
            item(53, Material.ARROW, "&6>>>").event {
                openCompassChest(player, compassChest, page + 1)
            }
            open(player)
        }
    }
}