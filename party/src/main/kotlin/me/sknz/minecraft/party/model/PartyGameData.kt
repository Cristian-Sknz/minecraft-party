package me.sknz.minecraft.party.model

data class PartyGameData(
    val players: List<PartyPlayer>,
    var game: Int,
) {

    enum class PartyGame {
        WORKSHOP;

        companion object {
            operator fun get(value: String?): PartyGame? {
                return entries.find { it.name.equals(value, true) }
            }
        }
    }

    val games = PartyGame.entries.shuffled()
}