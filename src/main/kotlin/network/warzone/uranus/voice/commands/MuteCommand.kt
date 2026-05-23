package network.warzone.uranus.voice.commands

import de.maxhenkel.voicechat.api.VoicechatConnection
import network.warzone.uranus.voice.listeners.VoiceChatListener
import network.warzone.uranus.voice.util.bukkit
import org.bukkit.command.CommandSender
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Command
import tc.oc.pgm.lib.org.incendo.cloud.annotations.CommandDescription
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Permission

class MuteCommand {

    @Command("vcmute")
    @CommandDescription("Mute a player in voice chat")
    @Permission("uranus.voicechat.mute")
    fun mutePlayer(
        sender: CommandSender,
        connection: VoicechatConnection
    ) {
        val player = connection.bukkit()!!

        if (VoiceChatListener.isMuted(player.uniqueId)) {
            sender.sendMessage("\u00A7e${player.name} is already muted in voice chat.")
            return
        }

        VoiceChatListener.mutePlayer(player.uniqueId)
        sender.sendMessage("\u00A7aMuted ${player.name} in voice chat.")
        player.sendMessage("\u00A7cYou have been muted in voice chat.")
        return
    }

}

class UnmuteCommand {

    @Command("vcunmute")
    @CommandDescription("Unmute a player in voice chat")
    @Permission("uranus.voicechat.mute")
    fun unmutePlayer(
        sender: CommandSender,
        connection: VoicechatConnection
    ) {
        val player = connection.bukkit()!!

        if (!VoiceChatListener.isMuted(player.uniqueId)) {
            sender.sendMessage("\u00A7e${player.name} is not muted in voice chat.")
            return
        }

        VoiceChatListener.unmutePlayer(player.uniqueId)
        sender.sendMessage("\u00A7aUnmuted ${player.name} in voice chat.")
        player.sendMessage("\u00A7aYou have been unmuted in voice chat.")
        return
    }

}
