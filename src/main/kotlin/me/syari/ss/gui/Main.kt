package me.syari.ss.gui

import me.syari.ss.core.auto.Event
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    override fun onEnable() {
        Event.register(this, EventListener)
    }
}