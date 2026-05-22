package network.warzone.uranus

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tc.oc.pgm.command.parsers.PlayerParser
import tc.oc.pgm.command.util.CommandGraph
import tc.oc.pgm.lib.org.incendo.cloud.minecraft.extras.MinecraftHelp
import tc.oc.pgm.lib.org.incendo.cloud.parser.ArgumentParser
import tc.oc.pgm.util.Audience
import java.lang.reflect.Type


class UranusCommandGraph(plugin: UranusPlugin) : CommandGraph<UranusPlugin>(plugin) {

    override fun createHelp(): MinecraftHelp<CommandSender?>? =
        MinecraftHelp.create(
            "/uranus help",
            manager,
            Audience::get
        )

    override fun setupInjectors() {
    }

    override fun setupParsers() {
        registerParser(Player::class.java, PlayerParser())
    }

    override fun registerCommands() {
        // Default commands
        register(plugin.mutationManager.command)
    }

    public override fun register(command: Any) {
        super.register(command)
    }

    public override fun <T: Any> registerParser(type: Type, parser: ArgumentParser<CommandSender, T>) {
        super.registerParser(type, parser)
    }


}
