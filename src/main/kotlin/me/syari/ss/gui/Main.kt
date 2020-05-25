package me.syari.ss.gui

import me.syari.ss.core.auto.Event
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object {
        internal lateinit var guiPlugin: JavaPlugin
    }

    override fun onEnable() {
        guiPlugin = this
        Event.register(this, EventListener)
    }
}