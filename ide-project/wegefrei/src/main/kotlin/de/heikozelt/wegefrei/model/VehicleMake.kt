package de.heikozelt.wegefrei.model

/**
 * @param name Vehicle make correctly written
 * @param aliases in lower case
 */
class VehicleMake(private val name: String, aliases: Array<String> = arrayOf()) {

    /**
     * array of name and aliases all in lower case
     */
    private val nameAndAliases: Array<String>

    init {
        nameAndAliases = aliases + name.lowercase()
    }

    override fun toString(): String {
        return name
    }

    /**
     * "ka" matches "Kawasaki" and "Kässbohrer", but "kä" matches only "Kässbohrer"
     * @param syllable in lower case
     */
    fun subStringMatches(syllable: String): Boolean {
        return nameAndAliases.find { it.contains(syllable)} != null
    }
}