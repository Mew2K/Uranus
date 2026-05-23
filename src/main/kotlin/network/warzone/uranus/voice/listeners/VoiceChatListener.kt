package network.warzone.uranus.voice.listeners

import de.maxhenkel.voicechat.api.VoicechatConnection
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent
import de.maxhenkel.voicechat.api.events.PlayerDisconnectedEvent
import network.warzone.uranus.voice.util.bukkit
import network.warzone.uranus.voice.util.matchPlayer
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import tc.oc.pgm.teams.Team
import java.util.*

object VoiceChatListener : Listener {

    val connections = mutableMapOf<UUID, VoicechatConnection>()
    private val mutedPlayers = mutableSetOf<UUID>()

    fun registerEvents(registration: EventRegistration) {
        registration.registerEvent(MicrophonePacketEvent::class.java, this::onMic)
        registration.registerEvent(PlayerConnectedEvent::class.java, this::onConnection)
        registration.registerEvent(PlayerDisconnectedEvent::class.java, this::onDisconnect)

//        Bukkit.getPluginManager().registerEvents(VoiceChatListener, UranusPlugin.get())
    }

    fun onConnection(event: PlayerConnectedEvent) {
        val player = event.connection.player
        connections[player.uuid] = event.connection

        val bukkitPlayer = event.connection.bukkit() ?: return

        sendTitle(
            bukkitPlayer,
            "\u00A7eVoice Chat Connected",
            "\u00A77Please be aware that your microphone might be on!"
        )
    }

    fun onDisconnect(event: PlayerDisconnectedEvent) {
        val player = event.playerUuid
        connections.remove(player)
    }

    fun onMic(event: MicrophonePacketEvent) {
        if (isMuted(event.senderConnection?.player?.uuid ?: return)) {
            event.cancel()
            return
        }

        val sender = event.senderConnection?.matchPlayer() ?: return
        val receiver = event.receiverConnection?.matchPlayer() ?: return

        if (sender.isObserving && receiver.isParticipating) {
            event.cancel()
            return
        }

        if (receiver.party is Team) {
            if (receiver.party != sender.party) {
                event.cancel()
            }
        }
    }

    fun isMuted(player: UUID): Boolean {
        return mutedPlayers.contains(player)
    }

    fun mutePlayer(player: UUID) {
        mutedPlayers.add(player)
    }

    fun unmutePlayer(player: UUID) {
        mutedPlayers.remove(player)
    }

    private fun sendTitle(player: Player, title: String, subtitle: String) {
        val titleSent = runCatching {
            val method = player.javaClass.getMethod(
                "sendTitle",
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            method.invoke(player, title, subtitle, 20, 100, 20)
        }.isSuccess

        if (titleSent) return

        val legacyTitleSent = runCatching {
            val method = player.javaClass.getMethod("sendTitle", String::class.java, String::class.java)
            method.invoke(player, title, subtitle)
        }.isSuccess

        if (!legacyTitleSent) {
            player.sendMessage("$title \u00A7r$subtitle")
        }
    }

}
