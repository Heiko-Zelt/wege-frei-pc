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

    private val filteredList = mutableListOf<VehicleMake>()

    init {
        filteredList.addAll(VEHICLE_MAKES)
    }

    /**
     * needed for autocomplete/filtered combo box
     * @param syllable part of a word
     */
    fun setFilter(syllable: String) {
        selectedItem = syllable
        log.debug("setFilter(syllable=$syllable)")
        val oldSize = filteredList.size
        val normalized = syllable.lowercase()
        filteredList.clear()
        filteredList.addAll(VEHICLE_MAKES.filter { it.subStringMatches(normalized) })
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
        return filteredList[index].toString()
    }

    companion object {
        val VEHICLE_MAKES = arrayOf(
            VehicleMake(""),
            VehicleMake("Abarth"),
            VehicleMake("Adria"),
            VehicleMake("Alfa Romeo"),
            VehicleMake("Aston Martin"),
            VehicleMake("Audi"),
            VehicleMake("Bentley"),
            VehicleMake("BMW"),
            VehicleMake("Bugatti"),
            VehicleMake("Cadillac"),
            VehicleMake("Chevrolet"),
            VehicleMake("Chrysler"),
            VehicleMake("Citroën", arrayOf("citroen")),
            VehicleMake("Crysler"),
            VehicleMake("Cupra"),
            VehicleMake("Dacia"),
            VehicleMake("Daewoo"),
            VehicleMake("DAF"),
            VehicleMake("Deutz-Fahr"),
            VehicleMake("Daihatsu"),
            VehicleMake("Dodge"),
            VehicleMake("DS"),
            VehicleMake("Ducati"),
            VehicleMake("Fendt"),
            VehicleMake("Ferrari"),
            VehicleMake("Fiat"),
            VehicleMake("Ford"),
            VehicleMake("Harley-Davidson"),
            VehicleMake("Honda"),
            VehicleMake("Hymer"),
            VehicleMake("Hyundai"),
            VehicleMake("Isuzu"),
            VehicleMake("Iveco"),
            VehicleMake("Jaguar"),
            VehicleMake("Jeep"),
            VehicleMake("John Deere"),
            VehicleMake("Kässbohrer", arrayOf("kassbohrer", "kaessbohrer")),
            VehicleMake("Kawasaki"),
            VehicleMake("Kia"),
            VehicleMake("KTM"),
            VehicleMake("Lada"),
            VehicleMake("Lamborghini"),
            VehicleMake("Lancia"),
            VehicleMake("Land Rover"),
            VehicleMake("Lexus"),
            VehicleMake("Lotus"),
            VehicleMake("Lynk & Co"),
            VehicleMake("MAN"),
            VehicleMake("Maserati"),
            VehicleMake("Mazda"),
            VehicleMake("Mercedes"),
            VehicleMake("MG"),
            VehicleMake("Mini"),
            VehicleMake("Mitsubishi"),
            VehicleMake("Nissan"),
            VehicleMake("Opel"),
            VehicleMake("Peugeot"),
            VehicleMake("Piaggio"),
            VehicleMake("Polestar"),
            VehicleMake("Porsche"),
            VehicleMake("Renault"),
            VehicleMake("Rolls-Royce"),
            VehicleMake("Saab"),
            VehicleMake("Scania"),
            VehicleMake("Seat"),
            VehicleMake("Setra"),
            VehicleMake("Škoda", arrayOf("skoda")),
            VehicleMake("Smart"),
            VehicleMake("SsangYong"),
            VehicleMake("Subaru"),
            VehicleMake("Suzuki"),
            VehicleMake("Tesla"),
            VehicleMake("Toyota"),
            VehicleMake("Trabant", arrayOf("trabi", "trabbi")),
            VehicleMake("Vauxhall"),
            VehicleMake("Volkswagen", arrayOf("vw")),
            VehicleMake("Volvo"),
            VehicleMake("Yamaha"),
            VehicleMake("Wartburg")
        )
    }
}