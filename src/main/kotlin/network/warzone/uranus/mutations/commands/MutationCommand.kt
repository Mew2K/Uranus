package network.warzone.uranus.mutations.commands

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import network.warzone.uranus.mutations.MutationManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tc.oc.pgm.api.PGM
import tc.oc.pgm.api.match.Match
import tc.oc.pgm.api.match.MatchPhase
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Argument
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Command
import tc.oc.pgm.lib.org.incendo.cloud.annotations.CommandDescription
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Permission
import tc.oc.pgm.util.Audience

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
        val title = "\u00A73\u00A7l\u00A7k[]\u00A7r \u00A73\u00A7lMutation \u00A7k[]"
        val subtitle = "\u00A7a$mutationName"
        val message = "\u00A73[Mutations] \u00A77The \u00A7b$mutationName \u00A77mutation has been enabled!"

        Bukkit.getOnlinePlayers().forEach {
            sendTitle(it, title, subtitle)
            playSound(it, "minecraft:entity.ender_dragon.growl")
            it.sendMessage(message)
        }
    }

    private fun sendTitle(player: Player, title: String, subtitle: String) {
        val playerClass = player.javaClass
        val titleSent = runCatching {
            val method = playerClass.getMethod(
                "sendTitle",
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            method.invoke(player, title, subtitle, 10, 100, 15)
        }.isSuccess

        if (titleSent) return

        val legacyTitleSent = runCatching {
            val method = playerClass.getMethod("sendTitle", String::class.java, String::class.java)
            method.invoke(player, title, subtitle)
        }.isSuccess

        if (!legacyTitleSent) {
            player.sendMessage("$title \u00A7r$subtitle")
        }
    }

    private fun playSound(player: Player, sound: String) {
        runCatching {
            val method = player.javaClass.getMethod(
                "playSound",
                org.bukkit.Location::class.java,
                String::class.java,
                Float::class.javaPrimitiveType,
                Float::class.javaPrimitiveType
            )
            method.invoke(player, player.location, sound, 1f, 1f)
        }
    }

}
