package network.warzone.uranus

import de.maxhenkel.voicechat.api.BukkitVoicechatService
import network.warzone.uranus.mutations.MutationManager
import network.warzone.uranus.voice.UranusVoicePlugin
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import tc.oc.pgm.api.PGM

class UranusPlugin : JavaPlugin() {

    companion object {
        private lateinit var instance: UranusPlugin
        fun get() = instance
    }

    lateinit var commandGraph: UranusCommandGraph
    lateinit var mutationManager: MutationManager

    override fun onEnable() {
        instance = this
        this.loadMutations()
        this.loadCommandManager()
        this.loadVoiceChatIntegration()
    }

    override fun onDisable() {
        if (::mutationManager.isInitialized) {
            runCatching {
                PGM.get().matchManager.matches.forEachRemaining(mutationManager::disableAll)
            }
        }
    }

    fun loadCommandManager() {
        this.commandGraph = UranusCommandGraph(this)
    }

    fun loadMutations() {
        this.mutationManager = MutationManager(this)
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
