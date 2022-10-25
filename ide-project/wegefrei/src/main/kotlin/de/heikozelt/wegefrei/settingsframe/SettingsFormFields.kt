package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.json.Settings
import org.slf4j.LoggerFactory
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class SettingsFormFields(private val settingsFrame: SettingsFrame): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val emailTextField = JTextField(30)
    private val streetTextField = JTextField(30)
    private val zipCodeTextField = JTextField(5)
    private val townTextField = JTextField(30)
    private val phoneNumberTextField = JTextField(20)

    private val smtpHostTextField = JTextField(30)
    private val smtpPortTextField = JTextField(6)
    private val smtpUserNameTextField = JTextField(30)
    private val tlsValues = arrayOf("Klartext (nicht empfohlen)", "TLS-verschlüsselt", "StartTLS-verschlüsselt")
    private val tlsComboBox = JComboBox(tlsValues)

    private val lookAndFeelNames = Settings.lookAndFeelNames().toTypedArray()
    private val lookAndFeelComboBox = JComboBox(lookAndFeelNames)
    // todo Prio 3: File chooser dialog
    private val photosDirTextField = JTextField(30)
    private val databaseDirTextField = JTextField(30)

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

    fun load(settings: Settings) {
        lookAndFeelComboBox.selectedItem = lookAndFeelNames.find { it == settings.lookAndFeel }
    }

    fun save(settings: Settings) {
        val selected = lookAndFeelComboBox.selectedItem
        if(selected == null) {
            log.warn("No String selected as look and feel.")
        } else {
            settings.lookAndFeel = lookAndFeelComboBox.selectedItem as String
        }
    }
}