package network.warzone.uranus.mutations

import network.warzone.uranus.UranusPlugin
import network.warzone.uranus.mutations.commands.MutationCommand
import network.warzone.uranus.mutations.warden.WardenSpawnMutation
import tc.oc.pgm.api.match.Match

class MutationManager(plugin: UranusPlugin) {

    private val mutations = listOf<Mutation>(
        WardenSpawnMutation()
    ).associateBy { it.id }

    val command = MutationCommand(this)

    fun get(id: String): Mutation? = mutations[id.lowercase()]

    fun disableAll(match: Match) {
        mutations.values
            .filter { it.isEnabled(match) }
            .forEach { it.disable(match) }
    }

}

