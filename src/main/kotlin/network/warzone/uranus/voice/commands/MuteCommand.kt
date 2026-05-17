package network.warzone.uranus.voice.commands

import de.maxhenkel.voicechat.api.VoicechatConnection
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
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
            sender.sendMessage(text("${player.name} is already muted in voice chat.", NamedTextColor.YELLOW))
            return
        }

        VoiceChatListener.mutePlayer(player.uniqueId)
        sender.sendMessage(text("Muted ${player.name} in voice chat.", NamedTextColor.GREEN))
        player.sendMessage(text("You have been muted in voice chat.", NamedTextColor.RED))
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
            sender.sendMessage(text("${player.name} is not muted in voice chat.", NamedTextColor.YELLOW))
            return
        }

        VoiceChatListener.unmutePlayer(player.uniqueId)
        sender.sendMessage(text("Unmuted ${player.name} in voice chat.", NamedTextColor.GREEN))
        player.sendMessage(text("You have been unmuted in voice chat.", NamedTextColor.GREEN))
        return
    }

}