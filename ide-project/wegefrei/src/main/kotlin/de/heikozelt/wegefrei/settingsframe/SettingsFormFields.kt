package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.docfilters.OnlyDigitsDocFilter
import de.heikozelt.wegefrei.gui.TrimmingTextField
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.json.Tls
import org.slf4j.LoggerFactory
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.text.AbstractDocument

class SettingsFormFields(private val settingsFrame: SettingsFrame): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val emailTextField = TrimmingTextField(30)
    private val streetTextField = TrimmingTextField(30)
    private val zipCodeTextField = TrimmingTextField(5)
    private val townTextField = TrimmingTextField(30)
    private val phoneNumberTextField = TrimmingTextField(20)

    private val smtpHostTextField = TrimmingTextField(30)
    private val smtpPortTextField = JTextField(6)
    private val smtpUserNameTextField = TrimmingTextField(30)
    private val tlsValues = arrayOf("Klartext (nicht empfohlen)", "TLS-verschlüsselt", "StartTLS-verschlüsselt")
    private val tlsComboBox = JComboBox(tlsValues)

    private val lookAndFeelNames = Settings.lookAndFeelNames().toTypedArray()
    private val lookAndFeelComboBox = JComboBox(lookAndFeelNames)
    // todo Prio 3: File chooser dialog
    private val photosDirTextField = TrimmingTextField(30)
    private val databaseDirTextField = TrimmingTextField(30)

    init {
        log.debug("init")
        layout = GridBagLayout()

        val constraints = GridBagConstraints()
        constraints.insets = Insets(0, 5, 0, 0)
        constraints.anchor = GridBagConstraints.WEST
        constraints.weightx = 0.5
        constraints.weighty = 0.1
        constraints.gridy = 0
        constraints.gridx = 0
        constraints.gridwidth = 1

        val witnessLabel = JLabel("<html><b>Zeugendaten</b></html>")
        add(witnessLabel, constraints)

        constraints.gridy++
        val emailLabel = JLabel("E-Mail-Adresse:")
        add(emailLabel, constraints)
        constraints.gridx = 1
        emailTextField.name = "emailTextField"
        add(emailTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val streetLabel = JLabel("Straße & Hausnummer:")
        add(streetLabel, constraints)
        constraints.gridx = 1
        add(streetTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val zipCodeLabel = JLabel("PLZ:")
        add(zipCodeLabel, constraints)
        constraints.gridx = 1
        add(zipCodeTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val townLabel = JLabel("Ort:")
        add(townLabel, constraints)
        constraints.gridx = 1
        add(townTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val phoneNumberLabel = JLabel("Telefonnummer:")
        add(phoneNumberLabel, constraints)
        constraints.gridx = 1
        add(phoneNumberTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val emailServerLabel = JLabel("<html><b>E-Mail-Server</b></html>")
        add(emailServerLabel, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val smtpHostLabel = JLabel("SMTP-Host:")
        add(smtpHostLabel, constraints)
        constraints.gridx = 1
        add(smtpHostTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val smtpPortLabel = JLabel("SMTP-Port:")
        add(smtpPortLabel, constraints)
        constraints.gridx = 1
        val portDoc = smtpPortTextField.document
        if (portDoc is AbstractDocument) {
            portDoc.documentFilter = OnlyDigitsDocFilter()
        }
        add(smtpPortTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val smtpUserNameLabel = JLabel("SMTP-Benutzername:")
        add(smtpUserNameLabel, constraints)
        constraints.gridx = 1
        add(smtpUserNameTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val tlsLabel = JLabel("Verschlüsselung:")
        add(tlsLabel, constraints)
        constraints.gridx = 1
        tlsComboBox.name = "tlsComboBox"
        add(tlsComboBox, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val technicalLabel = JLabel("<html><b>Sonstiges</b></html>")
        add(technicalLabel, constraints)

        constraints.gridy++
        val lookAndFeelLabel = JLabel("Look and Feel:")
        add(lookAndFeelLabel, constraints)
        constraints.gridx = 1
        add(lookAndFeelComboBox, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val photosDirLabel = JLabel("Fotos-Verzeichnis:")
        add(photosDirLabel, constraints)
        constraints.gridx = 1
        add(photosDirTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        val databaseDirLabel = JLabel("Datenbank-Verzeichnis:")
        add(databaseDirLabel, constraints)
        constraints.gridx = 1
        add(databaseDirTextField, constraints)
    }

    /**
     * Mapping von Settings-Objekt auf Werte der Formular-Felder
     */
    fun load(settings: Settings) {
        emailTextField.text = settings.witness.emailAddress
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
        photosDirTextField.text = settings.photosDirectory
        databaseDirTextField.text = settings.databaseDirectory
    }

    /**
     * Mapping von Werten der Formular-Felder auf Settings-Objekt
     */
    fun save(settings: Settings) {
        settings.witness.emailAddress = emailTextField.text.trim()
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
        settings.photosDirectory = photosDirTextField.text.trim()
        settings.databaseDirectory = databaseDirTextField.text.trim()
    }
}