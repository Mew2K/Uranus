package network.warzone.uranus.voice.util

import de.maxhenkel.voicechat.api.VoicechatConnection
import network.warzone.uranus.voice.UranusVoicePlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import tc.oc.pgm.api.PGM
import tc.oc.pgm.api.match.Match
import tc.oc.pgm.api.player.MatchPlayer

fun MatchPlayer.getVoiceChatConnection() =
    this.bukkit.getVoiceChatConnection()

fun Player.getVoiceChatConnection() =
    UranusVoicePlugin.server.getConnectionOf(this.uniqueId)

fun VoicechatConnection.bukkit(): Player? {
    if (this.player is Player)
        return this.player as Player

    val uuid = this.player?.uuid ?: return null
    return Bukkit.getPlayer(uuid)
}

fun VoicechatConnection.matchPlayer(match: Match): MatchPlayer? {
    val player = this.bukkit() ?: return null
    return match.getPlayer(player)
}

fun VoicechatConnection.matchPlayer(): MatchPlayer? {
    val player = this.bukkit() ?: return null
    val match = PGM.get().matchManager.getMatch(player)
    match ?: return null
    return match.getPlayer(player)
}