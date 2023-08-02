package me.sknz.minecraft.party.states

data class PartyGameData(
    val players: List<PartyPlayer>,
    var game: Int,
) {

    enum class PartyGame {
        WORKSHOP;
    }

    val games = PartyGame.entries.shuffled()
}