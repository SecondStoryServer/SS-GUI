package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.holder.ItemHolder.Companion.itemHolder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES
import org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS

interface Page {
    val display: String
    val icon: Material
    fun getItem(player: Player): Map<Int, Pair<CustomItemStack, (() -> Unit)?>>

    companion object {
        data class PlayerPageData(val uuidPlayer: UUIDPlayer) {
            private var page: Page = ItemPage
            private var clickEvent = mapOf<Int, () -> Unit>()

            private fun updatePage(player: Player, page: Page) {
                if (this.page == page) return
                this.page = page
                updateItem(player)
            }

            fun updateItem(player: Player) {
                val clickEvent = mutableMapOf<Int, () -> Unit>()
                val emptySlot = (0..40).toMutableSet()

                fun setItem(slot: Int, item: CustomItemStack?, event: (() -> Unit)?) {
                    val itemStack = if (item != null) {
                        if (event != null) {
                            clickEvent[slot] = event
                        }
                        emptySlot.remove(slot)
                        item.toOneItemStack
                    } else {
                        null
                    }
                    player.inventory.setItem(slot, itemStack)
                }

                page.getItem(player).forEach { (slot, pair) ->
                    setItem(slot, pair.first, pair.second)
                }
                val separateItem = CustomItemStack.create(Material.GRAY_STAINED_GLASS_PANE)
                listOf(11, 20, 29).forEach { slot ->
                    setItem(slot, separateItem, null)
                }
                pageList.forEach { (slot, page) ->
                    setItem(slot, CustomItemStack.create(page.icon).apply {
                        display = "&6" + page.display
                        if (page == this@PlayerPageData.page) {
                            addEnchant(Enchantment.ARROW_INFINITE, 1)
                            addItemFlag(HIDE_ENCHANTS, HIDE_ATTRIBUTES)
                        }
                    }) {
                        updatePage(player, page)
                    }
                }
                val itemHolder = player.itemHolder
                itemHolder.allNormalItem.forEach { (slot, item) ->
                    setItem(slot, item.itemStack, null)
                }
                itemHolder.allArmorItem.forEach { (armorSlot, item) ->
                    setItem(armorSlot.slot, item.itemStack) {
                        updatePage(player, StatusPage)
                    }
                }
                emptySlot.forEach { slot ->
                    setItem(slot, null, null)
                }

                this.clickEvent = clickEvent
            }

            fun runClickEvent(slot: Int) {
                clickEvent[slot]?.invoke()
            }
        }

        private val playerPage = mutableMapOf<UUIDPlayer, PlayerPageData>()

        private val pageList = mapOf(
            9 to ItemPage, 10 to StatusPage, 18 to PartyPage, 19 to GuildPage, 27 to FriendPage, 28 to OtherPage
        )

        val Player.page
            get() = UUIDPlayer(this).page

        private val UUIDPlayer.page
            get() = playerPage.getOrPut(this) { PlayerPageData(this) }
    }
}