package de.heikozelt.wegefrei.email.addressbook

import de.heikozelt.wegefrei.db.DatabaseRepo
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

    fun removeAt(index: Int) {
        addresses.removeAt(index)
        fireTableRowsDeleted(index, index)
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

    fun getAddressAt(rowIndex: Int): EmailAddressEntity {
        return addresses[rowIndex]
    }

    private fun indexOf(address: String): Int {
        return addresses.indexOfFirst { it.address == address }
    }

    fun updateAddress(newAddressEntity: EmailAddressEntity) {
        val rowIndex = indexOf(newAddressEntity.address)
        addresses[rowIndex].name = newAddressEntity.name
        fireTableRowsUpdated(rowIndex, rowIndex)
    }

    fun replaceAddress(oldAddress: String, newAddressEntity: EmailAddressEntity) {
        val rowIndex = indexOf(oldAddress)
        addresses[rowIndex] = newAddressEntity
        fireTableRowsUpdated(rowIndex, rowIndex)
    }

    companion object {
        val COLUMN_NAMES = arrayOf("E-Mail-Adresse", "Name")
    }
}