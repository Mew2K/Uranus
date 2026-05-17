package network.warzone.uranus.voice.commands.parsers

import de.maxhenkel.voicechat.api.VoicechatConnection
import network.warzone.uranus.voice.UranusVoicePlugin
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tc.oc.pgm.command.parsers.PlayerParser
import tc.oc.pgm.lib.org.incendo.cloud.context.CommandContext
import tc.oc.pgm.lib.org.incendo.cloud.context.CommandInput
import tc.oc.pgm.lib.org.incendo.cloud.parser.ArgumentParseResult
import tc.oc.pgm.lib.org.incendo.cloud.parser.ArgumentParser
import tc.oc.pgm.lib.org.incendo.cloud.suggestion.BlockingSuggestionProvider

class VoicechatPlayerParser : ArgumentParser<CommandSender, VoicechatConnection>, BlockingSuggestionProvider.Strings<CommandSender> {

    val playerParser = PlayerParser()

    fun notConnectedException(player: Player) =
        ArgumentParseResult.failure<VoicechatConnection>(
            Exception("Player ${player.name} is not connected to the voice chat.")
        )

    override fun parse(
        commandContext: CommandContext<CommandSender>,
        commandInput: CommandInput
    ): ArgumentParseResult<VoicechatConnection> {
        val player = this.playerParser.parse(commandContext, commandInput).parsedValue()
        if (player.isPresent && UranusVoicePlugin.isConnected(player.get())) {
            val connection = UranusVoicePlugin.server.getConnectionOf(player.get().uniqueId)
            return if (connection != null) {
                ArgumentParseResult.success(connection)
            } else {
                notConnectedException(player.get())
            }
        } else {
            return notConnectedException(player.get())
        }
    }

    override fun stringSuggestions(context: CommandContext<CommandSender?>, input: CommandInput): MutableList<String> {
        val players = this.playerParser.stringSuggestions(context, input)
        return players.filter { playerName ->
            val player = Bukkit.getPlayer(playerName)
            player != null && UranusVoicePlugin.isConnected(player)
        }.toMutableList()
    }
}