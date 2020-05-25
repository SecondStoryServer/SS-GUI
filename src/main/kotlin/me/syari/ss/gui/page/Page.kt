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

    fun setPage(player: Player) {
        playerPage[UUIDPlayer(player)] = this
        updateItem(player)
    }

    private fun updateItem(player: Player) {
        fun setItem(slot: Int, item: CustomItemStack) {
            player.inventory.setItem(slot, item.toOneItemStack)
        }

        getItem(player).forEach { (slot, pair) ->
            setItem(slot, pair.first)
        }
        listOf(11, 20, 29).forEach { slot ->
            setItem(slot, separateItem)
        }
        pageList.forEach { (slot, page) ->
            setItem(slot, CustomItemStack.create(page.icon).apply {
                display = "&6" + page.display
                if (page == this@Page) {
                    addEnchant(Enchantment.ARROW_INFINITE, 1)
                    addItemFlag(HIDE_ENCHANTS, HIDE_ATTRIBUTES)
                }
            })
        }
    }

    companion object {
        private val playerPage = mutableMapOf<UUIDPlayer, Page>()

        private val pageList = mapOf(
            9 to ItemPage, 10 to StatusPage, 18 to PartyPage, 19 to GuildPage, 20 to FriendPage, 21 to OtherPage
        )

        private val defaultPage = ItemPage

        private val Player.page get() = playerPage.getOrDefault(UUIDPlayer(this), defaultPage)

        fun updatePageItem(player: Player) {
            player.page.updateItem(player)
        }

        val separateItem = CustomItemStack.create(Material.GRAY_STAINED_GLASS_PANE, "")
    }
}