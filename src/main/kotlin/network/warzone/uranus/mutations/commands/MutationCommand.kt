package network.warzone.uranus.mutations.commands

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title.title
import network.warzone.uranus.mutations.MutationManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import tc.oc.pgm.api.PGM
import tc.oc.pgm.api.match.Match
import tc.oc.pgm.api.match.MatchPhase
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Argument
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Command
import tc.oc.pgm.lib.org.incendo.cloud.annotations.CommandDescription
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Permission
import tc.oc.pgm.util.Audience
import java.time.Duration

class MutationCommand(private val mutations: MutationManager) {

    @Command("mutation warden")
    @CommandDescription("Toggle the Warden spawn mutation")
    @Permission("uranus.mutation.warden")
    fun toggleWarden(sender: CommandSender) {
        handleMutation(sender, "warden", null)
    }

    @Command("mutation warden <state>")
    @CommandDescription("Enable or disable the Warden spawn mutation")
    @Permission("uranus.mutation.warden")
    fun setWarden(sender: CommandSender, @Argument("state") state: String) {
        handleMutation(sender, "warden", parseState(sender, state) ?: return)
    }

    @Command("mutation rage")
    @CommandDescription("Toggle the Rage mutation")
    @Permission("uranus.mutation.rage")
    fun toggleRage(sender: CommandSender) {
        handleMutation(sender, "rage", null)
    }

    @Command("mutation rage <state>")
    @CommandDescription("Enable or disable the Rage mutation")
    @Permission("uranus.mutation.rage")
    fun setRage(sender: CommandSender, @Argument("state") state: String) {
        handleMutation(sender, "rage", parseState(sender, state) ?: return)
    }

    @Command("mutation speedster")
    @CommandDescription("Toggle the Speedster mutation")
    @Permission("uranus.mutation.speedster")
    fun toggleSpeedster(sender: CommandSender) {
        handleMutation(sender, "speedster", null)
    }

    @Command("mutation speedster <state>")
    @CommandDescription("Enable or disable the Speedster mutation")
    @Permission("uranus.mutation.speedster")
    fun setSpeedster(sender: CommandSender, @Argument("state") state: String) {
        handleMutation(sender, "speedster", parseState(sender, state) ?: return)
    }

    private fun handleMutation(sender: CommandSender, mutationId: String, enabled: Boolean?) {
        val mutation = mutations.get(mutationId) ?: return
        val match = getMatch(sender) ?: run {
            Audience.get(sender).sendWarning(text("No active PGM match was found.", NamedTextColor.RED))
            return
        }

        if (!mutation.isAvailable(sender)) {
            Audience.get(sender).sendWarning(text("${mutation.name} is not available on this server version.", NamedTextColor.RED))
            return
        }

        val shouldEnable = enabled ?: !mutation.isEnabled(match)
        if (shouldEnable && match.phase != MatchPhase.RUNNING) {
            Audience.get(sender).sendWarning(text("Mutations cannot be enabled before the match starts!", NamedTextColor.RED))
            return
        }

        val result = if (shouldEnable) {
            mutation.enable(match)
        } else {
            mutation.disable(match)
        }

        if (result.success && result.enabled && result.mutation != null) {
            announceEnabled(match, result.mutation.name)
        } else {
            Audience.get(sender).sendWarning(text(result.message, if (result.success) NamedTextColor.GREEN else NamedTextColor.RED))
        }
    }

    private fun getMatch(sender: CommandSender): Match? {
        return PGM.get().matchManager.getMatch(sender)
    }

    private fun parseState(sender: CommandSender, state: String): Boolean? {
        return when (state.lowercase()) {
            "on", "enable", "enabled", "true", "yes" -> true
            "off", "disable", "disabled", "false", "no" -> false
            else -> {
                Audience.get(sender).sendWarning(text("Use on or off.", NamedTextColor.RED))
                null
            }
        }
    }

    private fun announceEnabled(match: Match, mutationName: String) {
        val legacy = LegacyComponentSerializer.legacySection()
        val title = title(
            legacy.deserialize("\u00A73\u00A7l\u00A7k[]\u00A7r \u00A73\u00A7lMutation \u00A7k[]"),
            text(mutationName, NamedTextColor.GREEN),
            net.kyori.adventure.title.Title.Times.times(
                Duration.ofMillis(500),
                Duration.ofSeconds(5),
                Duration.ofMillis(750)
            )
        )
        val sound = Sound.sound(
            Key.key("minecraft:entity.ender_dragon.growl"),
            Sound.Source.MASTER,
            1f,
            1f
        )
        val message = text("[Mutations] ", NamedTextColor.DARK_AQUA)
            .append(text("The ", NamedTextColor.GRAY))
            .append(text(mutationName, NamedTextColor.AQUA))
            .append(text(" mutation has been enabled!", NamedTextColor.GRAY))

        Bukkit.getOnlinePlayers().forEach {
            it.showTitle(title)
            it.playSound(sound)
            it.sendMessage(message)
        }
    }

}
