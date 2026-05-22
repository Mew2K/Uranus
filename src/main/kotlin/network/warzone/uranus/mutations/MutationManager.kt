package network.warzone.uranus.mutations

import network.warzone.uranus.UranusPlugin
import network.warzone.uranus.mutations.commands.MutationCommand
import network.warzone.uranus.mutations.warden.WardenSpawnMutation
import org.bukkit.event.Listener
import tc.oc.pgm.api.match.Match

class MutationManager(plugin: UranusPlugin) {

    private val wardenSpawnMutation = WardenSpawnMutation()

    private val mutations = listOf<Mutation>(wardenSpawnMutation).associateBy { it.id }

    val command = MutationCommand(this)

    init {
        mutations.values
            .filterIsInstance<Listener>()
            .forEach(plugin::registerEvents)
    }

    fun get(id: String): Mutation? = mutations[id.lowercase()]

    fun disableAll(match: Match) {
        mutations.values
            .filter { it.isEnabled(match) }
            .forEach { it.disable(match) }
    }

}
