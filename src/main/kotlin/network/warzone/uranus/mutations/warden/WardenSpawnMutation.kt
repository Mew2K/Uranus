package network.warzone.uranus.mutations.warden

import network.warzone.uranus.mutations.Mutation
import network.warzone.uranus.mutations.MutationResult
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import tc.oc.pgm.api.match.Match
import tc.oc.pgm.api.party.Competitor
import tc.oc.pgm.api.player.MatchPlayer
import tc.oc.pgm.spawns.Spawn
import tc.oc.pgm.spawns.SpawnMatchModule
import tc.oc.pgm.teams.Team
import tc.oc.pgm.tracker.TrackerMatchModule
import tc.oc.pgm.tracker.info.MobInfo
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import java.util.Optional
import java.util.UUID

class WardenSpawnMutation : Mutation {

    override val id: String = "warden"
    override val name: String = "Warden Spawn"

    private val wardensByMatch = mutableMapOf<String, MutableSet<UUID>>()

    override fun isAvailable(sender: CommandSender): Boolean = wardenType() != null

    override fun isEnabled(match: Match): Boolean {
        val wardenIds = wardensByMatch[match.id] ?: return false
        wardenIds.removeIf { match.world.getEntity(it) == null }
        return wardenIds.isNotEmpty()
    }

    override fun enable(match: Match): MutationResult {
        val wardenType = wardenType()
            ?: return MutationResult(false, "$name is only available on servers that support Wardens.")

        if (isEnabled(match)) {
            return MutationResult(false, "$name is already enabled.")
        }

        val spawns = match.getModule(SpawnMatchModule::class.java)
            ?: return MutationResult(false, "This map does not expose PGM team spawns.")
        val tracker = match.getModule(TrackerMatchModule::class.java)
            ?: return MutationResult(false, "PGM's tracker module is not loaded for this match.")

        val spawned = mutableSetOf<UUID>()
        val teams = match.competitors.filterIsInstance<Team>()
        for (team in teams) {
            val location = findSpawnLocation(match, spawns, team) ?: continue
            val warden = match.world.spawnEntity(location, wardenType)
            if (warden is LivingEntity) {
                tracker.entityTracker.trackEntity(warden, MobInfo(warden, TeamParticipantState(team, location)))
            }
            spawned.add(warden.uniqueId)
        }

        if (spawned.isEmpty()) {
            return MutationResult(false, "No team spawn locations were found for $name.")
        }

        wardensByMatch[match.id] = spawned
        return MutationResult(true, "Enabled $name and spawned ${spawned.size} wardens.")
    }

    override fun disable(match: Match): MutationResult {
        val removed = killWardens(match)
        wardensByMatch.remove(match.id)
        return MutationResult(true, "Disabled $name and killed $removed wardens.")
    }

    private fun findSpawnLocation(match: Match, spawns: SpawnMatchModule, team: Competitor): Location? {
        val player = teamSpawnQuery(match, team)
        val spawn = spawns.getSpawns(player).choose(match) ?: return null
        return spawn.getSpawn(player)
    }

    private fun List<Spawn>.choose(match: Match): Spawn? {
        if (isEmpty()) return null
        return this[match.random.nextInt(size)]
    }

    private fun killWardens(match: Match): Int {
        val wardenType = wardenType() ?: return 0
        var killed = 0
        for (entity in match.world.entities) {
            if (entity.type != wardenType) continue

            if (entity is LivingEntity) {
                entity.health = 0.0
            } else {
                entity.remove()
            }
            killed++
        }
        return killed
    }

    private fun teamSpawnQuery(match: Match, team: Competitor): MatchPlayer {
        val handler = InvocationHandler { proxy, method, args ->
            when (method.name) {
                "getMatch" -> match
                "getParty", "getCompetitor" -> team
                "getId" -> UUID.nameUUIDFromBytes("uranus:${match.id}:${team.id}:spawn-query".toByteArray())
                "getBukkit", "getPlayer", "getNick", "getSpectatorTarget" -> null
                "getState", "getParticipantState" -> TeamParticipantState(team, match.world.spawnLocation)
                "getLocation" -> match.world.spawnLocation
                "getWorld" -> match.world
                "getEntityType" -> org.bukkit.entity.Player::class.java
                "isParticipating", "isAlive", "isVisible", "canInteract", "canSee" -> true
                "isObserving", "isDead", "isFrozen", "isLegacy" -> false
                "getProtocolVersion" -> Int.MAX_VALUE
                "getNameLegacy", "getPrefixedName" -> "${team.nameLegacy} spawn"
                "getName" -> team.name
                "getFilterableParent" -> team
                "getFilterableChildren", "getFilterableDescendants", "getSpectators" -> emptyList<Any>()
                "getInventory" -> null
                "getSettings" -> null
                "getActivity" -> null
                "getLastActive" -> java.time.Instant.now()
                "audience" -> net.kyori.adventure.audience.Audience.empty()
                "pointers" -> net.kyori.adventure.pointer.Pointers.empty()
                "equals" -> proxy === args?.firstOrNull()
                "hashCode" -> team.hashCode()
                "toString" -> "WardenSpawnQuery(${team.nameLegacy})"
                else -> {
                    if (method.returnType == Boolean::class.javaPrimitiveType) false
                    else if (method.returnType == Int::class.javaPrimitiveType) 0
                    else if (method.returnType == Optional::class.java) Optional.empty<Any>()
                    else null
                }
            }
        }

        return Proxy.newProxyInstance(
            MatchPlayer::class.java.classLoader,
            arrayOf(MatchPlayer::class.java),
            handler
        ) as MatchPlayer
    }

    private fun wardenType(): EntityType? {
        return runCatching { EntityType.valueOf("WARDEN") }.getOrNull()
    }

}
