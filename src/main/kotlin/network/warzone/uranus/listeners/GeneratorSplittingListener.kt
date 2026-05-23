package network.warzone.uranus.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPickupItemEvent
import tc.oc.pgm.api.PGM
import tc.oc.pgm.spawner.Spawner

object GeneratorSplittingListener : Listener {

    @EventHandler
    fun onItemPickup(event: PlayerPickupItemEvent) {
        val item = event.item

        val metadata = item.getMetadata(Spawner.METADATA_KEY).firstOrNull {
            it.owningPlugin == PGM.get()
        }

        if (metadata == null) {
            return
        }

        // Give the same amount of items to all nearby players
        item.world.players
            .filter { it != event.player }
            .filter { it.location.distanceSquared(item.location) <= 2.25 }
            .forEach {
                it.inventory.addItem(item.itemStack)
            }

    }

}
