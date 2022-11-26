package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.gui.RecipientListCellRenderer
import de.heikozelt.wegefrei.model.RecipientComboBoxModel
import de.heikozelt.wegefrei.mua.EmailAddressEntity
import org.slf4j.LoggerFactory
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JComboBox
import javax.swing.text.JTextComponent

// todo Prio 4: Synonyme: D = BRD = Deutschland = Germany
// todo Prio 2: maximal 3 Buchstaben bei der Eingabe erlauben (sonst meckert die Datenbank)
// außerdem beugt es Verwechslungen mit Kfz-Kennzeichen vor
class RecipientComboBox(dbRepo: DatabaseRepo): JComboBox<EmailAddressEntity?>() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val mod = RecipientComboBoxModel(dbRepo)

    init {
        val keyListener = object: KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                log.debug("keyReleased()")
                mod.setFilter(getEditorText())
                hidePopup()
                showPopup()
            }
        }

        model = mod
        setRenderer(RecipientListCellRenderer())
        setEditable(true)
        maximumRowCount = 15
        val editComp = editor.editorComponent
        if(editComp is JTextComponent) {
            log.debug("editorComponent is JTextComponent")
            editComp.addKeyListener(keyListener)
        }
    }

    fun getEditorText(): String {
        editor.editorComponent?.let { comp ->
            if (comp is JTextComponent) {
                val doc = comp.document
                return doc.getText(0, doc.length)
            }
        }
        return ""
    }

    fun setEditorText(text: String) {
        editor.editorComponent?.let { comp ->
            if (comp is JTextComponent) {
                val doc = comp.document
                doc.remove(0, doc.length)
                doc.insertString(0, text, null)
            }
        }
    }

    /**
     * todo: bei Freitext-Eingabe, prüfen ob ein @-Zeichen enthalten ist,
     * wenn ja EmailAddressEntity mit Adresse und ohne Namen zurückgeben.
     * sonst Formular-Validierungs-Warnung ausgeben.
     */
    fun getValue(): EmailAddressEntity? {
        selectedItem?.let {
            return if(it is EmailAddressEntity) {
                return it
            } else {
                null
            }
        }
        return EmailAddressEntity(getEditorText())
    }

    /**
     * Deserialisierung.
     * Eine Zeichenkette setzt den Status der ComboBox.
     * null --> [0] = { "", null }
     * "" --> { "", null }
     * " " --> { "", null }
     * "AND" --> { "AND, "Andorra"}
     * "Wunderland" --> editor text = "Wunderland"
     */
    fun setValue(recipient: EmailAddressEntity?) {
        if(recipient == null) {
            selectedIndex = 0
        } else {
            val emailAddress = mod.find(recipient)
            if (emailAddress == null) {
                setEditorText(recipient.asText())
            } else {
                selectedItem = emailAddress
            }
        }
    }

    fun loadData() {
        mod.loadData()
    }
}