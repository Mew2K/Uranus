package network.warzone.uranus.mutations

import org.bukkit.command.CommandSender
import tc.oc.pgm.api.match.Match

interface Mutation {

    val id: String
    val name: String

    fun isAvailable(sender: CommandSender): Boolean = true

    fun isEnabled(match: Match): Boolean

    fun enable(match: Match): MutationResult

    fun disable(match: Match): MutationResult

}

data class MutationResult(
    val success: Boolean,
    val message: String
)

