package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.EmailUserAgent
import de.heikozelt.wegefrei.gui.CharPredicateDocFilter
import de.heikozelt.wegefrei.gui.PatternVerifier
import de.heikozelt.wegefrei.gui.TrimmingTextField
import de.heikozelt.wegefrei.json.EmailServerConfig
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.json.Tls
import de.heikozelt.wegefrei.model.EmailMessage
import org.slf4j.LoggerFactory
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*
import javax.swing.text.AbstractDocument


class SettingsFormFields(private val settingsFrame: SettingsFrame): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val emailTextField = TrimmingTextField(MAX_COLUMNS)
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
    private val tlsComboBox = JComboBox(tlsValues)
    private val smtpConnectButton = JButton("Test-E-Mail senden")

    private val lookAndFeelNames = Settings.lookAndFeelNames().toTypedArray()
    private val lookAndFeelComboBox = JComboBox(lookAndFeelNames)
    private val photosDirLabel = JLabel()
    private val photosDirButton = JButton("Ordner auswählen")
    private val databaseDirLabel = JLabel()
    private val databaseDirButton = JButton("Ordner auswählen")

    init {
        log.debug("init")
        layout = GridBagLayout()

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = Insets(0, 5, 0, 0)
        constraints.anchor = GridBagConstraints.WEST
        constraints.weightx = 1.0
        constraints.weighty = 0.1
        constraints.gridy = 0
        constraints.gridx = 0
        constraints.gridwidth = 2
        val witnessLabel = JLabel("<html><b>Zeugendaten</b></html>")
        witnessLabel.horizontalAlignment = SwingConstants.CENTER
        add(witnessLabel, constraints)

        constraints.gridy++
        constraints.gridx=0
        constraints.gridwidth = 1
        constraints.weightx = LEFT_WEIGHT
        val givenNameLabel = JLabel("Vorname:")
        add(givenNameLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        givenNameTextField.name = "givenNameTextField"
        add(givenNameTextField, constraints)

        constraints.gridy++
        constraints.gridx=0
        constraints.gridwidth = 1
        constraints.weightx = LEFT_WEIGHT
        val surnameLabel = JLabel("Nachname:")
        add(surnameLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        surnameTextField.name = "surnameTextField"
        add(surnameTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val streetLabel = JLabel("Straße & Hausnummer:")
        add(streetLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        add(streetTextField, constraints)

        constraints.fill = GridBagConstraints.NONE
        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val zipCodeLabel = JLabel("PLZ:")
        add(zipCodeLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        add(zipCodeTextField, constraints)

        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val townLabel = JLabel("Ort:")
        add(townLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        add(townTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val phoneNumberLabel = JLabel("Telefonnummer:")
        add(phoneNumberLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        add(phoneNumberTextField, constraints)

        constraints.gridy++
        constraints.gridx=0
        constraints.gridwidth = 1
        constraints.weightx = LEFT_WEIGHT
        val emailLabel = JLabel("E-Mail-Adresse:")
        add(emailLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        emailTextField.name = "emailTextField"
        emailTextField.inputVerifier = PatternVerifier.emailAddressVerifier
        add(emailTextField, constraints)

        constraints.insets = Insets(16, 5, 0, 0)
        constraints.gridy++
        constraints.gridx = 0
        constraints.gridwidth = 2
        constraints.weightx = 1.0
        val emailServerLabel = JLabel("<html><b>E-Mail-Server</b></html>")
        emailServerLabel.horizontalAlignment = SwingConstants.CENTER
        add(emailServerLabel, constraints)

        constraints.insets = Insets(0, 5, 0, 0)
        constraints.gridwidth = 1
        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val smtpHostLabel = JLabel("SMTP-Host:")
        add(smtpHostLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        add(smtpHostTextField, constraints)

        constraints.fill = GridBagConstraints.NONE
        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val smtpPortLabel = JLabel("SMTP-Port:")
        add(smtpPortLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        val portDoc = smtpPortTextField.document
        if (portDoc is AbstractDocument) {
            portDoc.documentFilter = CharPredicateDocFilter.onlyDigitsDocFilter
        }
        add(smtpPortTextField, constraints)

        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val smtpUserNameLabel = JLabel("SMTP-Benutzername:")
        add(smtpUserNameLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        add(smtpUserNameTextField, constraints)

        constraints.fill = GridBagConstraints.NONE
        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val tlsLabel = JLabel("Verschlüsselung:")
        add(tlsLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        tlsComboBox.name = "tlsComboBox"
        add(tlsComboBox, constraints)

        constraints.gridy++
        constraints.fill = GridBagConstraints.NONE
        smtpConnectButton.addActionListener { sendTestEmail() }
        constraints.gridx = 1
        add(smtpConnectButton, constraints)

        constraints.insets = Insets(16, 5, 0, 0)
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.gridy++
        constraints.gridx = 0
        constraints.gridwidth = 2
        constraints.weightx = 1.0
        val technicalLabel = JLabel("<html><b>Sonstiges</b></html>")
        technicalLabel.horizontalAlignment = SwingConstants.CENTER
        add(technicalLabel, constraints)

        constraints.insets = Insets(0, 5, 0, 0)
        constraints.fill = GridBagConstraints.NONE
        constraints.gridwidth = 1
        constraints.gridy++
        constraints.weightx = LEFT_WEIGHT
        val lookAndFeelLabel = JLabel("Look and Feel:")
        add(lookAndFeelLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        add(lookAndFeelComboBox, constraints)

        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val photosDirDecoLabel = JLabel("Fotos-Ordner:")
        add(photosDirDecoLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        add(photosDirLabel, constraints)
        photosDirButton.addActionListener { DirectoryChooser(photosDirLabel, "Fotos-Ordner") }
        constraints.gridy++
        add(photosDirButton, constraints)

        constraints.gridy++
        constraints.gridx = 0
        constraints.weightx = LEFT_WEIGHT
        val databaseDirDecoLabel = JLabel("Datenbank-Ordner:")
        add(databaseDirDecoLabel, constraints)
        constraints.weightx = RIGHT_WEIGHT
        constraints.gridx = 1
        add(databaseDirLabel, constraints)
        databaseDirButton.addActionListener { DirectoryChooser(databaseDirLabel, "Datenbank-Ordner") }
        constraints.gridy++
        constraints.gridx = 1
        add(databaseDirButton, constraints)
    }

    /**
     * Mapping von Settings-Objekt auf Werte der Formular-Felder
     */
    fun load(settings: Settings) {
        emailTextField.text = settings.witness.emailAddress
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
        tlsComboBox.selectedItem = tlsValues[tlsIndex]

        lookAndFeelComboBox.selectedItem = lookAndFeelNames.find { it == settings.lookAndFeel }
        photosDirLabel.text = settings.photosDirectory
        databaseDirLabel.text = settings.databaseDirectory
    }

    /**
     * Mapping von Werten der Formular-Felder auf Settings-Objekt
     */
    fun save(settings: Settings) {

        settings.witness.emailAddress = emailTextField.text.trim()
        settings.witness.givenName = givenNameTextField.text.trim()
        settings.witness.surname = surnameTextField.text.trim()
        settings.witness.street = streetTextField.text.trim()
        settings.witness.zipCode = zipCodeTextField.text.trim()
        settings.witness.town = townTextField.text.trim()
        settings.witness.telephoneNumber = phoneNumberTextField.text.trim()

        settings.emailServerConfig.smtpHost = smtpHostTextField.text.trim()
        settings.emailServerConfig.smtpPort = smtpPortTextField.text.toInt()
        settings.emailServerConfig.smtpUserName = smtpUserNameTextField.text.trim()
        settings.emailServerConfig.tls = Tls.values()[tlsComboBox.selectedIndex]

        val selectedLook = lookAndFeelComboBox.selectedItem
        settings.lookAndFeel = if(selectedLook is String) {
            selectedLook
        } else {
            log.warn("No String selected as look and feel.")
            ""
        }
        settings.photosDirectory = photosDirLabel.text
        settings.databaseDirectory = databaseDirLabel.text
    }

    /**
     * Erst mal die Test-E-Mail anzeigen.
     * Nur wenn die Anwender_in auf Ok klickt, wirklich absenden.
     */
    private fun sendTestEmail() {
        log.debug("sendTestEmail()")

        val fullName = "${givenNameTextField.text.trim()} ${surnameTextField.text.trim()}"
        val senderName = fullName.ifBlank { TEST_MAIL_FROM_NAME }
        val senderEmailAddress = emailTextField.text.trim()

        val emailServerConfig = EmailServerConfig(
            smtpHostTextField.text.trim(),
            smtpPortTextField.text.trim().toInt(),
            smtpUserNameTextField.text.trim(),
            Tls.values()[tlsComboBox.selectedIndex]
        )

        val eMessage = EmailMessage(
            senderName,
            senderEmailAddress,
            senderName,
            senderEmailAddress,
            TEST_MAIL_SUBJECT,
            TEST_MAIL_CONTENT
        )
        val emailUserAgent = EmailUserAgent()
        emailUserAgent.setEmailServerConfig(emailServerConfig)
        emailUserAgent.sendMailAfterConfirmation(eMessage)
    }



    companion object {
        const val MAX_COLUMNS = 15
        const val LEFT_WEIGHT = 0.05
        const val RIGHT_WEIGHT = 0.95
        const val MAIL_USER_AGENT = "Wege frei! https://github.com/Heiko-Zelt/wege-frei-pc"
        const val TEST_MAIL_SUBJECT = "Wege frei! Test-E-Mail"
        const val TEST_MAIL_FROM_NAME = "Wege frei!"
        const val TEST_MAIL_CONTENT = "<html><h1>Dies ist ein Test</h1>\n<p>Diese E-Mail-Nachricht wurde automatisch von der Wege frei!-Anwendung generiert.</p></html>"
    }
}