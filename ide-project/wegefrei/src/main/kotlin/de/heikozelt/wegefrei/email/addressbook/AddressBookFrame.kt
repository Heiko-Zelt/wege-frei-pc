package de.heikozelt.wegefrei.email.addressbook

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.WegeFrei
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

class AddressBookFrame(private val app: WegeFrei, private val databaseRepo: DatabaseRepo): JFrame("Adressbuch - Wege frei!") {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val tableModel = AddressBookTableModel()

    init {
        log.debug("init")

        val addressesTable = JTable(tableModel)

        val scrollPane = JScrollPane(addressesTable)
        val newButton = JButton("neue Addresse erfassen")
        newButton.addActionListener {
            AddressFrame()
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
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(scrollPane)
                .addGroup(
                    lay.createParallelGroup()
                        .addComponent(newButton)
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
}