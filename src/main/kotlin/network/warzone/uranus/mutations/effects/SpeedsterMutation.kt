package network.warzone.uranus.mutations.effects

class SpeedsterMutation : StatusEffectMutation(
    id = "speedster",
    name = "Speedster",
    effects = listOf(
        MutationEffect(
            names = listOf("SPEED"),
            amplifier = 3
        ),
        MutationEffect(
            names = listOf("JUMP", "JUMP_BOOST"),
            amplifier = 2
        )
    )
)

