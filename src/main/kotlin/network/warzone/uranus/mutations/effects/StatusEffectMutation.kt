package network.warzone.uranus.mutations.effects

import network.warzone.uranus.mutations.Mutation
import network.warzone.uranus.mutations.MutationResult
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import tc.oc.pgm.api.match.Match
import tc.oc.pgm.spawns.events.ParticipantSpawnEvent

abstract class StatusEffectMutation(
    override val id: String,
    override val name: String,
    private val effects: List<MutationEffect>
) : Mutation, Listener {

    private val enabledMatches = mutableSetOf<String>()

    override fun isAvailable(sender: CommandSender): Boolean {
        return effects.all { it.type() != null }
    }

    override fun isEnabled(match: Match): Boolean {
        return match.id in enabledMatches
    }

    override fun enable(match: Match): MutationResult {
        if (isEnabled(match)) {
            return MutationResult(false, "$name is already enabled.")
        }

        enabledMatches.add(match.id)
        match.participants.forEach { applyEffects(it.bukkit) }
        return MutationResult(true, "Enabled $name.", this, true)
    }

    override fun disable(match: Match): MutationResult {
        enabledMatches.remove(match.id)
        match.participants.forEach { removeEffects(it.bukkit) }
        return MutationResult(true, "Disabled $name.")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onParticipantSpawn(event: ParticipantSpawnEvent) {
        if (!isEnabled(event.match)) return

        val player = event.player.bukkit
        player.server.scheduler.runTaskLater(
            network.warzone.uranus.UranusPlugin.get(),
            Runnable { applyEffects(player) },
            1L
        )
    }

    private fun applyEffects(player: Player) {
        effects.forEach { mutationEffect ->
            val type = mutationEffect.type() ?: return@forEach
            player.addPotionEffect(
                PotionEffect(type, INFINITE_DURATION, mutationEffect.amplifier),
                true
            )
        }
    }

    private fun removeEffects(player: Player) {
        effects.forEach { mutationEffect ->
            val type = mutationEffect.type() ?: return@forEach
            player.removePotionEffect(type)
        }
    }

    data class MutationEffect(
        private val names: List<String>,
        val amplifier: Int
    ) {
        fun type(): PotionEffectType? {
            return names.firstNotNullOfOrNull(PotionEffectType::getByName)
        }
    }

    companion object {
        private const val INFINITE_DURATION = Int.MAX_VALUE
    }

}
