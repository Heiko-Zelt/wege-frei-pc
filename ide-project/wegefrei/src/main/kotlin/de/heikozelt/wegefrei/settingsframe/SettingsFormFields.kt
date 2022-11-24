package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.gui.CharPredicateDocFilter
import de.heikozelt.wegefrei.gui.PatternVerifier
import de.heikozelt.wegefrei.gui.Styles
import de.heikozelt.wegefrei.gui.TrimmingTextField
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.json.Tls
import de.heikozelt.wegefrei.mua.EmailAddressWithName
import de.heikozelt.wegefrei.mua.EmailMessage
import de.heikozelt.wegefrei.mua.EmailServerConfig
import de.heikozelt.wegefrei.mua.EmailUserAgent
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.util.*
import javax.swing.*
import javax.swing.text.AbstractDocument

/**
 * Teil von SettingsFrame.
 * Die vielen Formularfelder passen eventuell nicht alle in den JFrame,
 * deswegen Darstellung in einer ScrollPane.
 * todo: Zeug_in: männlich/weiblich/divers oder keine Angabe
 */
class SettingsFormFields : JPanel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    // GUI components
    private val emailAddressTextField = TrimmingTextField(MAX_COLUMNS)
    private val givenNameTextField = TrimmingTextField(MAX_COLUMNS)
    private val surnameTextField = TrimmingTextField(MAX_COLUMNS)
    private val streetTextField = TrimmingTextField(MAX_COLUMNS)
    private val zipCodeTextField = TrimmingTextField(6)
    private val townTextField = TrimmingTextField(MAX_COLUMNS)
    private val phoneNumberTextField = TrimmingTextField(MAX_COLUMNS)

    private val smtpHostTextField = TrimmingTextField(MAX_COLUMNS)
    private val smtpPortTextField = JTextField(6)
    private val smtpUserNameTextField = TrimmingTextField(MAX_COLUMNS)
    private val tlsValues = arrayOf("Klartext (nicht empfohlen)", "TLS-verschlüsselt", "StartTLS-verschlüsselt")
    private val encryptionComboBox = JComboBox(tlsValues)
    private val sendTestMailButton = JButton("Test-E-Mail senden")

    private val lookAndFeelNames = Settings.lookAndFeelNames().toTypedArray()
    private val lookAndFeelComboBox = JComboBox(lookAndFeelNames)
    private val photosDirField = JLabel()
    private val photosDirButton = JButton("Ordner auswählen")
    private val databaseDirField = JLabel()
    private val databaseDirButton = JButton("Ordner auswählen")

    init {
        /**
         * increase minimum height by constant factor
         */
        fun increaseHeight(component: JComponent) {
            component.minimumSize = Dimension(component.minimumSize.width, (component.minimumSize.height * 1.8).toInt())
        }

        /**
         * a specially formatted JLabel
         */
        class Heading(headingText: String): JLabel("<html><b>$headingText</b></html>") {
            init {
                horizontalAlignment = SwingConstants.CENTER
                verticalAlignment = SwingConstants.BOTTOM
                increaseHeight(this)
            }
        }

        log.debug("init")

        // GUI components
        val witnessHeading = Heading("Zeuge")
        val givenNameLabel = JLabel("Vorname:")
        givenNameTextField.name = "givenNameTextField"
        val surnameLabel = JLabel("Nachname:")
        surnameTextField.name = "surnameTextField"
        val streetLabel = JLabel("Straße & Hausnummer:")
        val zipCodeLabel = JLabel("PLZ:")
        val townLabel = JLabel("Ort:")
        val phoneNumberLabel = JLabel("Telefonnummer:")
        val emailAddressLabel = JLabel("E-Mail-Adresse:")
        emailAddressTextField.name = "emailTextField"
        emailAddressTextField.inputVerifier = PatternVerifier.emailAddressVerifier

        val emailServerHeading = Heading("E-Mail-Server")
        val smtpHostLabel = JLabel("SMTP-Host:")
        val smtpPortLabel = JLabel("SMTP-Port:")
        val portDoc = smtpPortTextField.document
        if (portDoc is AbstractDocument) {
            portDoc.documentFilter = CharPredicateDocFilter.onlyDigitsDocFilter
        }
        val smtpUserNameLabel = JLabel("SMTP-Benutzername:")
        val encryptionLabel = JLabel("Verschlüsselung:")
        encryptionComboBox.name = "tlsComboBox"
        sendTestMailButton.addActionListener { sendTestEmail() }

        val technicalHeading = Heading("Sonstiges")
        val lookAndFeelLabel = JLabel("Look and Feel:")
        val photosDirLabel = JLabel("Fotos-Ordner:")
        photosDirButton.addActionListener { DirectoryChooser(photosDirField, "Fotos-Ordner") }
        val databaseDirLabel = JLabel("Datenbank-Ordner:")
        databaseDirButton.addActionListener { DirectoryChooser(databaseDirField, "Datenbank-Ordner") }

        // layout:
        val lay = GroupLayout(this)
        lay.autoCreateGaps = true
        lay.autoCreateContainerGaps = true

        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup( // labels & form fields
                    lay.createSequentialGroup()
                        .addGroup( // labels
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(givenNameLabel)
                                .addComponent(surnameLabel)
                                .addComponent(streetLabel)
                                .addComponent(zipCodeLabel)
                                .addComponent(townLabel)
                                .addComponent(phoneNumberLabel)
                                .addComponent(emailAddressLabel)
                                .addComponent(smtpHostLabel)
                                .addComponent(smtpPortLabel)
                                .addComponent(smtpUserNameLabel)
                                .addComponent(encryptionLabel)
                                .addComponent(lookAndFeelLabel)
                                .addComponent(photosDirLabel)
                                .addComponent(databaseDirLabel)
                        )
                        .addGroup( // form fields
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(givenNameTextField)
                                .addComponent(surnameTextField)
                                .addComponent(streetTextField)
                                .addComponent(zipCodeTextField)
                                .addComponent(townTextField)
                                .addComponent(phoneNumberTextField)
                                .addComponent(emailAddressTextField)
                                .addComponent(smtpHostTextField)
                                .addComponent(smtpPortTextField)
                                .addComponent(smtpUserNameTextField)
                                .addComponent(encryptionComboBox)
                                .addComponent(sendTestMailButton)
                                .addComponent(lookAndFeelComboBox)
                                .addComponent(photosDirField)
                                .addComponent(photosDirButton)
                                .addComponent(databaseDirField)
                                .addComponent(databaseDirButton)
                        )
                )
                .addComponent(witnessHeading)
                .addComponent(emailServerHeading)
                .addComponent(technicalHeading)
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(witnessHeading)
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(givenNameLabel).addComponent(givenNameTextField)

                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(surnameLabel).addComponent(surnameTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(streetLabel).addComponent(streetTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(zipCodeLabel).addComponent(zipCodeTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(townLabel).addComponent(townTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(phoneNumberLabel).addComponent(phoneNumberTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(emailAddressLabel).addComponent(emailAddressTextField)
                )
                .addComponent(emailServerHeading)
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(smtpHostLabel).addComponent(smtpHostTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(smtpPortLabel).addComponent(smtpPortTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(smtpUserNameLabel).addComponent(smtpUserNameTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(encryptionLabel).addComponent(encryptionComboBox)
                )
                .addComponent(sendTestMailButton)
                .addComponent(technicalHeading)
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lookAndFeelLabel).addComponent(lookAndFeelComboBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(photosDirLabel).addComponent(photosDirField)
                )
                .addComponent(photosDirButton)
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(databaseDirLabel).addComponent(databaseDirField)
                )
                .addComponent(databaseDirButton)
                // Höhe von TextFields passt sich an. das sieht hässlich aus, Gap hilft leider nicht.
                //.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE)
        )
        layout = lay
        components.filterIsInstance<JTextField>().forEach(Styles::restrictHeight)
        components.filterIsInstance<JComboBox<*>>().forEach(Styles::restrictSize)
        Styles.restrictSize(zipCodeTextField)
        Styles.restrictSize(smtpPortTextField)
    }

    /**
     * Mapping von Settings-Objekt auf Werte der Formular-Felder
     */
    fun load(settings: Settings) {
        emailAddressTextField.text = settings.witness.emailAddress
        givenNameTextField.text = settings.witness.givenName
        surnameTextField.text = settings.witness.surname
        streetTextField.text = settings.witness.street
        zipCodeTextField.text = settings.witness.zipCode
        townTextField.text = settings.witness.town
        phoneNumberTextField.text = settings.witness.telephoneNumber

        smtpHostTextField.text = settings.emailServerConfig.smtpHost
        smtpPortTextField.text = settings.emailServerConfig.smtpPort.toString()
        smtpUserNameTextField.text = settings.emailServerConfig.smtpUserName
        val tlsIndex = Tls.values().indexOf(settings.emailServerConfig.tls)
        encryptionComboBox.selectedItem = tlsValues[tlsIndex]

        lookAndFeelComboBox.selectedItem = lookAndFeelNames.find { it == settings.lookAndFeel }
        photosDirField.text = settings.photosDirectory
        databaseDirField.text = settings.databaseDirectory
    }

    /**
     * Mapping von Werten der Formular-Felder auf Settings-Objekt
     */
    fun save(settings: Settings) {

        settings.witness.emailAddress = emailAddressTextField.text.trim()
        settings.witness.givenName = givenNameTextField.text.trim()
        settings.witness.surname = surnameTextField.text.trim()
        settings.witness.street = streetTextField.text.trim()
        settings.witness.zipCode = zipCodeTextField.text.trim()
        settings.witness.town = townTextField.text.trim()
        settings.witness.telephoneNumber = phoneNumberTextField.text.trim()

        settings.emailServerConfig.smtpHost = smtpHostTextField.text.trim()
        settings.emailServerConfig.smtpPort = smtpPortTextField.text.toInt()
        settings.emailServerConfig.smtpUserName = smtpUserNameTextField.text.trim()
        settings.emailServerConfig.tls = Tls.values()[encryptionComboBox.selectedIndex]

        val selectedLook = lookAndFeelComboBox.selectedItem
        settings.lookAndFeel = if (selectedLook is String) {
            selectedLook
        } else {
            log.warn("No String selected as look and feel.")
            ""
        }
        settings.photosDirectory = photosDirField.text
        settings.databaseDirectory = databaseDirField.text
    }

    /**
     * Erst mal die Test-E-Mail anzeigen.
     * Nur wenn die Anwender_in auf Ok klickt, wirklich absenden.
     */
    private fun sendTestEmail() {
        log.debug("sendTestEmail()")

        val fullName = "${givenNameTextField.text.trim()} ${surnameTextField.text.trim()}"
        val senderName = fullName.ifBlank { TEST_DEFAULT_MAIL_FROM_NAME }
        val senderEmailAddress = emailAddressTextField.text.trim()
        val from = EmailAddressWithName(senderEmailAddress, senderName)
        val tos = TreeSet<EmailAddressWithName>()
        tos.add(from)

        val emailServerConfig = EmailServerConfig(
            smtpHostTextField.text.trim(),
            smtpPortTextField.text.trim().toInt(),
            smtpUserNameTextField.text.trim(),
            Tls.values()[encryptionComboBox.selectedIndex]
        )

        val eMessage = EmailMessage(
            from,
            tos,
            TEST_MAIL_SUBJECT,
            TEST_MAIL_CONTENT
        )
        val emailUserAgent = EmailUserAgent()
        emailUserAgent.setEmailServerConfig(emailServerConfig)
        emailUserAgent.sendMailAfterConfirmation(eMessage)
    }

    companion object {
        const val MAX_COLUMNS = 15
        const val TEST_MAIL_SUBJECT = "Wege frei! Test-E-Mail"
        const val TEST_DEFAULT_MAIL_FROM_NAME = "Wege frei!"
        const val TEST_MAIL_CONTENT =
            "<html><h1>Dies ist ein Test</h1>\n<p>Diese E-Mail-Nachricht wurde automatisch von der Wege frei!-Anwendung generiert.</p></html>"
    }
}