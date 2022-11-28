package de.heikozelt.wegefrei.email.addressbook

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.email.EmailAddressEntity
import javax.swing.table.AbstractTableModel

class AddressBookTableModel(): AbstractTableModel() {

    private val addresses = mutableListOf<EmailAddressEntity>() // = arrayListOf()

    fun loadAddresses(databaseRepo: DatabaseRepo) {
        val resultList = databaseRepo.findAllEmailAddresses()
        resultList?.let {
            addresses.clear()
            addresses.addAll(it)
        }
    }

    fun addAddress(newAddressEntity: EmailAddressEntity) {
        val index = addresses.size
        addresses.add(newAddressEntity)
        fireTableRowsInserted(index, index)
    }

    override fun getRowCount(): Int {
        return addresses.size
    }

    override fun getColumnCount(): Int {
        return 2
    }

    override fun getColumnName(column: Int): String {
        return COLUMN_NAMES[column]
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): String? {
        val entity = addresses[rowIndex]
        return when (columnIndex) {
            0 -> entity.address
            1 -> entity.name
            else -> throw IndexOutOfBoundsException()
        }
    }

    companion object {
        val COLUMN_NAMES = arrayOf("E-Mail-Adresse", "Name")
    }
}