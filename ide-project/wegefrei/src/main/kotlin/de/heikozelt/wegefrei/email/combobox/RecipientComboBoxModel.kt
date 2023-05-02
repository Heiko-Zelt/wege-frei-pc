package de.heikozelt.wegefrei.email.combobox

import de.heikozelt.wegefrei.db.DatabaseRepo
import de.heikozelt.wegefrei.email.EmailAddressEntity
import org.slf4j.LoggerFactory
import java.lang.Math.min
import javax.swing.DefaultComboBoxModel

/**
 * Fahrzeugmarken
 * todo Prio 3: Logos als Icon hinzufügen
 * todo Prio 3: "--" anzeigen aber null speichern, siehe VehicleColor
 */
class RecipientComboBoxModel(private val dbRepo: DatabaseRepo): DefaultComboBoxModel<EmailAddressEntity>() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val allEmailAddresses = mutableListOf<EmailAddressEntity>()
    private val filteredEmailAddresses = mutableListOf<EmailAddressEntity>()

    fun loadData() {
        val result = dbRepo.findAllEmailAddresses()
        result?.let {
            allEmailAddresses.clear()
            allEmailAddresses.addAll(it)
        }
        setFilter("")
    }

    fun find(recipient: EmailAddressEntity): EmailAddressEntity? {
        return allEmailAddresses.find { recipient.address == it.address && recipient.name == it.name }
    }

    /**
     * needed for autocomplete/filtered combo box
     * @param: syllable part of a word
     */
    fun setFilter(syllable: String) {
        selectedItem = syllable
        log.debug("setFilter(syllable=$syllable)")
        val oldSize = filteredEmailAddresses.size
        val normalized = syllable.lowercase()
        filteredEmailAddresses.clear()
        filteredEmailAddresses.addAll(allEmailAddresses.filter {
            normalized in it.address.lowercase() || normalized in (it.name?.lowercase()?:"")
        })
        val newSize = filteredEmailAddresses.size
        log.debug("newSize = $newSize")
        //EventQueue.invokeLater {
            if (newSize != 0 || oldSize != 0) fireContentsChanged(this, 0, min(oldSize, newSize) - 1)
            if (newSize > oldSize) fireIntervalAdded(this, oldSize, newSize - 1)
            if (newSize < oldSize) fireIntervalRemoved(this, newSize, oldSize - 1)
        //}
    }

    override fun getSize(): Int {
        return filteredEmailAddresses.size
    }

    override fun getElementAt(index: Int): EmailAddressEntity {
        return filteredEmailAddresses[index]
    }

}