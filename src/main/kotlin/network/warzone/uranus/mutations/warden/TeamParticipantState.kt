package network.warzone.uranus.mutations.warden

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.Location
import tc.oc.pgm.api.match.Match
import tc.oc.pgm.api.party.Competitor
import tc.oc.pgm.api.player.MatchPlayer
import tc.oc.pgm.api.player.ParticipantState
import tc.oc.pgm.util.named.NameStyle
import java.util.Optional
import java.util.UUID

class TeamParticipantState(
    private val competitor: Competitor,
    private val location: Location
) : ParticipantState {

    private val id = UUID.nameUUIDFromBytes("uranus:${competitor.match.id}:${competitor.id}:warden".toByteArray())

    override fun getMatch(): Match = competitor.match

    override fun getParty(): Competitor = competitor

    override fun getId(): UUID = id

    override fun getLocation(): Location = location.clone()

    override fun isDead(): Boolean = false

    override fun isVanished(): Boolean = false

    override fun getNick(): String? = null

    override fun getPlayer(): Optional<MatchPlayer> = Optional.empty()

    override fun canInteract(): Boolean = competitor.isParticipating

    override fun getName(style: NameStyle): Component = competitor.getName(style)

    override fun getNameLegacy(): String = "${competitor.nameLegacy} Warden"

    override fun audience(): Audience = Audience.empty()

}

