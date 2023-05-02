package de.heikozelt.wegefrei.email.addressbook

import de.heikozelt.wegefrei.db.DatabaseRepo
import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.email.EmailAddressEntity
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*
import javax.swing.ListSelectionModel.SINGLE_SELECTION

class AddressBookFrame(private val app: WegeFrei, private val databaseRepo: DatabaseRepo): JFrame("Adressbuch - Wege frei!") {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val tableModel = AddressBookTableModel()
    private val addressesTable = JTable(tableModel)

    init {
        log.debug("init")

        val editButton = JButton("Bearbeiten")
        editButton.isEnabled = false
        val deleteButton = JButton("Löschen")
        deleteButton.isEnabled = false

        val tabMouseListener = object: MouseAdapter() {
            override fun mouseClicked(me: MouseEvent) {
                if (me.clickCount == 2) { // double click
                    val rowIndex = addressesTable.selectedRow
                    val emailAddressEntity = tableModel.getAddressAt(rowIndex)
                    openAddressFrame(emailAddressEntity)
                }
            }
        }

        // Es kann nur eine Adresse bearbeitet werden.
        // Beim Löschen wäre Mehrfachauswahl sinnvoll, aber zusätzlicher Programmieraufwand.
        addressesTable.selectionModel.selectionMode = SINGLE_SELECTION
        addressesTable.selectionModel.addListSelectionListener {
            val isSelected = addressesTable.selectedRow != -1
            log.debug("selectedRow: ${addressesTable.selectedRow}")
            editButton.isEnabled = isSelected
            deleteButton.isEnabled = isSelected
        }
        addressesTable.addMouseListener (tabMouseListener)

        val scrollPane = JScrollPane(addressesTable)
        val newButton = JButton("Neue Addresse erfassen")
        newButton.addActionListener {
            AddressFrame(this, databaseRepo)
        }
        editButton.addActionListener {
            val index = addressesTable.selectedRow
            val addressEntity = tableModel.getAddressAt(index)
            openAddressFrame(addressEntity)
        }
        deleteButton.addActionListener {
            val index = addressesTable.selectionModel.leadSelectionIndex
            val address = tableModel.getAddressAt(index).address
            databaseRepo.deleteEmailAddress(address)
            tableModel.removeAt(index)
        }

        // layout:
        val lay = GroupLayout(contentPane)
        lay.autoCreateGaps = false
        lay.autoCreateContainerGaps = false
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane)
                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            GroupLayout.PREFERRED_SIZE,
                            Int.MAX_VALUE
                        )
                        .addComponent(newButton)
                        .addComponent(editButton)
                        .addComponent(deleteButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(scrollPane)
                .addGroup(
                    lay.createParallelGroup()
                        .addComponent(newButton)
                        .addComponent(editButton)
                        .addComponent(deleteButton)
                )
        )
        layout = lay

        minimumSize = Dimension(250, 250)
        setSize(600, 600)
        isVisible = true

        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                isVisible = false
                dispose()
                app.addressBookFrameClosed()
            }
        })
    }

    fun loadAddresses() {
        tableModel.loadAddresses(databaseRepo)
    }

    fun openAddressFrame(address: EmailAddressEntity) {
        AddressFrame(this, databaseRepo, address)
    }

    fun addAddress(newAddressEntity: EmailAddressEntity) {
        val index = tableModel.rowCount
        tableModel.addAddress(newAddressEntity)
        addressesTable.changeSelection(index, index, false, false)
    }

    fun updateAddress(newAddressEntity: EmailAddressEntity) {
        tableModel.updateAddress(newAddressEntity)
    }

    fun replaceAddress(oldAddress: String, newAddressEntity: EmailAddressEntity) {
        tableModel.replaceAddress(oldAddress, newAddressEntity)
    }
}