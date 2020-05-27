package me.syari.ss.gui.page

import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.core.player.UUIDPlayer
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

            fun updateItem(player: Player) {
                val clickEvent = mutableMapOf<Int, () -> Unit>()
                val emptySlot = (9..35).toMutableSet()

                fun setItem(slot: Int, item: CustomItemStack, event: (() -> Unit)?) {
                    player.inventory.setItem(slot, item.toOneItemStack)
                    if (event != null) {
                        clickEvent[slot] = event
                    }
                    emptySlot.remove(slot)
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
                        this.page = page
                        updateItem(player)
                    }
                }
                emptySlot.forEach { slot ->
                    player.inventory.setItem(slot, null)
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