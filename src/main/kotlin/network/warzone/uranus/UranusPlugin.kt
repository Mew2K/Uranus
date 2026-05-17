package network.warzone.uranus

import de.maxhenkel.voicechat.api.BukkitVoicechatService
import network.warzone.uranus.voice.UranusVoicePlugin
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class UranusPlugin : JavaPlugin() {

    companion object {
        private lateinit var instance: UranusPlugin
        fun get() = instance
    }

    lateinit var commandGraph: UranusCommandGraph

    override fun onEnable() {
        this.loadCommandManager()
        this.loadVoiceChatIntegration()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun loadCommandManager() {
        this.commandGraph = UranusCommandGraph(this)
    }

    fun loadVoiceChatIntegration() {
        if (server.pluginManager.isPluginEnabled("voicechat")) {
            val service = server.servicesManager.load(BukkitVoicechatService::class.java)
            service?.registerPlugin(UranusVoicePlugin)
        }
    }

    fun registerEvents(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, this)
    }
}
