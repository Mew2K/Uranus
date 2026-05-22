package network.warzone.uranus.mutations.effects

class RageMutation : StatusEffectMutation(
    id = "rage",
    name = "Rage",
    effects = listOf(
        MutationEffect(
            names = listOf("INCREASE_DAMAGE", "STRENGTH"),
            amplifier = 254
        )
    )
)

