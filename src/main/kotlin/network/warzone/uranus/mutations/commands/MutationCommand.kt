package network.warzone.uranus.mutations.commands

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import network.warzone.uranus.mutations.MutationManager
import org.bukkit.command.CommandSender
import tc.oc.pgm.api.PGM
import tc.oc.pgm.api.match.Match
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Argument
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Command
import tc.oc.pgm.lib.org.incendo.cloud.annotations.CommandDescription
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Permission

class MutationCommand(private val mutations: MutationManager) {

    @Command("mutation warden")
    @CommandDescription("Toggle the Warden spawn mutation")
    @Permission("uranus.mutation.warden")
    fun toggleWarden(sender: CommandSender) {
        handleWarden(sender, null)
    }

    @Command("mutation warden <state>")
    @CommandDescription("Enable or disable the Warden spawn mutation")
    @Permission("uranus.mutation.warden")
    fun setWarden(sender: CommandSender, @Argument("state") state: String) {
        val enabled = when (state.lowercase()) {
            "on", "enable", "enabled", "true", "yes" -> true
            "off", "disable", "disabled", "false", "no" -> false
            else -> {
                sender.sendMessage(text("Use on or off.", NamedTextColor.RED))
                return
            }
        }
        handleWarden(sender, enabled)
    }

    private fun handleWarden(sender: CommandSender, enabled: Boolean?) {
        val mutation = mutations.get("warden") ?: return
        val match = getMatch(sender) ?: run {
            sender.sendMessage(text("No active PGM match was found.", NamedTextColor.RED))
            return
        }

        if (!mutation.isAvailable(sender)) {
            sender.sendMessage(text("${mutation.name} is not available on this server version.", NamedTextColor.RED))
            return
        }

        val shouldEnable = enabled ?: !mutation.isEnabled(match)
        val result = if (shouldEnable) {
            mutation.enable(match)
        } else {
            mutation.disable(match)
        }

        sender.sendMessage(text(result.message, if (result.success) NamedTextColor.GREEN else NamedTextColor.RED))
    }

    private fun getMatch(sender: CommandSender): Match? {
        return PGM.get().matchManager.getMatch(sender)
    }

}
