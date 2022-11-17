package de.heikozelt.wegefrei.model

import org.slf4j.LoggerFactory
import java.lang.Math.min
import javax.swing.DefaultComboBoxModel

/**
 * Fahrzeugmarken
 * todo Prio 3: Logos als Icon hinzufügen
 * todo Prio 3: "--" anzeigen aber null speichern, siehe VehicleColor
 */
class VehicleMakesComboBoxModel: DefaultComboBoxModel<String>() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val filteredList = mutableListOf<String>()

    init {
        filteredList.addAll(VEHICLE_MAKES)
    }

    /**
     * needed for autocomplete/filtered combo box
     * @param: syllable part of a word
     */
    fun setFilter(syllable: String) {
        selectedItem = syllable
        log.debug("setFilter(syllable=$syllable)")
        val oldSize = filteredList.size
        val normalized = syllable.lowercase()
        filteredList.clear()
        filteredList.addAll(VEHICLE_MAKES.filter { normalized in it.lowercase() })
        //VEHICLE_MAKES.forEach { if(normalized in it) filteredVehicleMakes.add(it) }
        val newSize = filteredList.size
        log.debug("newSize = $newSize")
        //EventQueue.invokeLater {
            if (newSize != 0 || oldSize != 0) fireContentsChanged(this, 0, min(oldSize, newSize) - 1)
            if (newSize > oldSize) fireIntervalAdded(this, oldSize, newSize - 1)
            if (newSize < oldSize) fireIntervalRemoved(this, newSize, oldSize - 1)
        //}
    }

    override fun getSize(): Int {
        return filteredList.size
    }

    override fun getElementAt(index: Int): String {
        return filteredList[index]
    }

    companion object {
        val VEHICLE_MAKES = arrayOf(
            "",
            "Abarth", "Alfa Romeo", "Aston Martin", "Audi",
            "Bentley", "BMW", "Bugatti",
            "Cadillac", "Chevrolet", "Chrysler", "Citroën", "Crysler",
            "Dacia", "Daewoo", "Daihatsu", "Dodge", "DS", "Ducati",
            "Ferrari", "Fiat", "Ford",
            "Harley-Davidson", "Honda", "Hyundai",
            "Isuzu", "Jaguar", "Jeep", "Kawasaki", "Kia", "KTM",
            "Lada", "Lamborghini", "Lancia", "Land Rover", "Lexus", "Lotus",
            "Maserati", "Mazda", "Mercedes", "MG", "Mini", "Mitsubishi", "Nissan", "Opel",
            "Peugeot", "Piaggio", "Polestar", "Porsche", "Renault", "Rolls-Royce",
            "Saab", "Scania", "Seat", "Škoda", "Smart", "SsangYong", "Subaru", "Suzuki",
            "Tesla", "Toyota", "Trabant",
            "Vauxhall", "Volkswagen", "Volvo", "Yamaha", "Wartburg"
        )
    }
}