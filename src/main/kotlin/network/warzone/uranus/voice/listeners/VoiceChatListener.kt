package network.warzone.uranus.voice.listeners

import de.maxhenkel.voicechat.api.VoicechatConnection
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent
import de.maxhenkel.voicechat.api.events.PlayerDisconnectedEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.title
import network.warzone.uranus.voice.util.bukkit
import network.warzone.uranus.voice.util.matchPlayer
import org.bukkit.event.Listener
import tc.oc.pgm.teams.Team
import java.time.Duration
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

        bukkitPlayer.showTitle(title(
                text("Voice Chat Connected", NamedTextColor.YELLOW),
                text("Please be aware that your microphone might be on!", NamedTextColor.GRAY),
                Title.Times.times(
                    Duration.ofSeconds(1),
                    Duration.ofSeconds(5),
                    Duration.ofSeconds(1)
                )
            ))
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

}