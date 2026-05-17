package network.warzone.uranus.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import tc.oc.pgm.api.PGM
import tc.oc.pgm.spawner.Spawner

object GeneratorSplittingListener : Listener {

    @EventHandler
    fun onItemPickup(event: EntityPickupItemEvent) {
        val item = event.item

        val metadata = item.getMetadata(Spawner.METADATA_KEY).firstOrNull {
            it.owningPlugin == PGM.get()
        }

        if (metadata == null) {
            return
        }

        // Give the same amount of items to all nearby players
        item.location.getNearbyPlayers(1.5)
            .filter { it != event.entity }
            .forEach {
                it.inventory.addItem(item.itemStack)
            }

    }

}