package network.warzone.uranus.voice

import de.maxhenkel.voicechat.api.VoicechatApi
import de.maxhenkel.voicechat.api.VoicechatConnection
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.VoicechatServerApi
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent
import network.warzone.uranus.UranusPlugin
import network.warzone.uranus.voice.commands.MuteCommand
import network.warzone.uranus.voice.commands.UnmuteCommand
import network.warzone.uranus.voice.commands.parsers.VoicechatPlayerParser
import network.warzone.uranus.voice.listeners.VoiceChatListener
import org.bukkit.entity.Player

object UranusVoicePlugin : VoicechatPlugin {

    val plugin get() = UranusPlugin.get()

    lateinit var server: VoicechatServerApi

    override fun getPluginId(): String = "uranus_voice_plugin"

    override fun initialize(api: VoicechatApi) {
        this.registerCommands()
    }

    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent(
            VoicechatServerStartedEvent::class.java,
            this::onServerStart
        )

        VoiceChatListener.registerEvents(registration)
    }

    private fun onServerStart(event: VoicechatServerStartedEvent) {
        server = event.voicechat
    }

    fun isConnected(player: Player): Boolean {
        return server.getConnectionOf(player.uniqueId) != null
    }

    private fun registerCommands() {
        plugin.commandGraph.registerParser(VoicechatConnection::class.java, VoicechatPlayerParser())

        plugin.commandGraph.register(MuteCommand())
        plugin.commandGraph.register(UnmuteCommand())
    }

}