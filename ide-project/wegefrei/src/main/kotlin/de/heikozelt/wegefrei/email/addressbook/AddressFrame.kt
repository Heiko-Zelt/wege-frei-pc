package de.heikozelt.wegefrei.email.addressbook

import de.heikozelt.wegefrei.db.DatabaseRepo
import de.heikozelt.wegefrei.email.EmailAddressEntity
import de.heikozelt.wegefrei.gui.Styles
import org.slf4j.LoggerFactory
import java.awt.Dimension
import javax.swing.*

class AddressFrame(
    private val addressBookFrame: AddressBookFrame,
    private val dbRepo: DatabaseRepo,
    private val originalAddressEntity: EmailAddressEntity? = null
): JFrame() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val addressTextField = JTextField()
    private val nameTextField = JTextField()
    private val okButton = JButton("Ok")

    init {
        log.debug("init")
        title = if(originalAddressEntity == null) {
            "Neue Adresse"
        } else {
            "Adresse bearbeiten"
        }
        originalAddressEntity?.let {
            addressTextField.text = it.address
            nameTextField.text = it.name
        }

        val addressLabel = JLabel("E-Mail-Adresse:")
        val nameLabel = JLabel("Name:")

        okButton.addActionListener {
            val address = addressTextField.text
            val name = nameTextField.text
            val newAddressEntity = EmailAddressEntity(address, name)
            if(originalAddressEntity == null) {
                dbRepo.insertEmailAddress(newAddressEntity)
                addressBookFrame.addAddress(newAddressEntity)
            } else {
                if(address == originalAddressEntity.address) {
                    // todo unique key exception, display verification error at form fields
                    dbRepo.updateEmailAddress(newAddressEntity)
                    addressBookFrame.updateAddress(newAddressEntity)
                } else {
                    // todo unique key exception, display verification error at form fields
                    dbRepo.replaceEmailAddress(originalAddressEntity.address, newAddressEntity)
                    addressBookFrame.replaceAddress(originalAddressEntity.address, newAddressEntity)
                }
            }
            isVisible = false
            dispose()
        }

        val cancelButton = JButton("Abbrechen")
        cancelButton.addActionListener {
            isVisible = false
            dispose()
        }

        // layout:
        val lay = GroupLayout(contentPane)
        lay.autoCreateGaps = false
        lay.autoCreateContainerGaps = false
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    lay.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                             lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                 .addComponent(addressLabel)
                                 .addComponent(nameLabel)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(addressTextField)
                                .addComponent(nameTextField)
                        )
                        .addContainerGap()
                )
                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            GroupLayout.PREFERRED_SIZE,
                            Int.MAX_VALUE
                        )
                        .addComponent(okButton)
                        .addComponent(cancelButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addContainerGap()
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(addressLabel)
                        .addComponent(addressTextField)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(nameLabel)
                        .addComponent(nameTextField)
                )
                .addPreferredGap(
                    LayoutStyle.ComponentPlacement.UNRELATED,
                    GroupLayout.PREFERRED_SIZE,
                    Int.MAX_VALUE
                )
                .addGroup(
                    lay.createParallelGroup()
                        .addComponent(okButton)
                        .addComponent(cancelButton)
                )
        )
        lay.linkSize(SwingConstants.HORIZONTAL, okButton, cancelButton)
        layout = lay
        //components.filterIsInstance<JTextField>().forEach(Styles::restrictHeight)
        Styles.restrictHeight(addressTextField)
        Styles.restrictHeight(nameTextField)

        minimumSize = Dimension(250, 100)
        setSize(400, 150)
        isVisible = true
    }
}