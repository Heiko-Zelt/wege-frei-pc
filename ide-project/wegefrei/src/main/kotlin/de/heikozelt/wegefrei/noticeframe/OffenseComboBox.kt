package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.OffenseComboBoxModel
import org.slf4j.LoggerFactory
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JComboBox
import javax.swing.text.JTextComponent

class OffenseComboBox: JComboBox<String?>() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val mod = OffenseComboBoxModel()

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
     * Serialisierung.
     * Der Status der ComboBox wird in einer Zeichenkette gespeichert.
     *
     * todo Groß-Kleinschreibung automatisch korrigieren
     * todo Sonstige Rechtschreibkorrekturen beim Bearbeiten
     */
    fun getValue(): String? {
        selectedItem?.let {
            if(it is String) {
                return it.ifEmpty { null }
            }
        }
        return getEditorText()
    }

    /**
     * Deserialisierung.
     * Eine Zeichenkette setzt den Status der ComboBox.
     * null --> [0] = [""]
     * "" --> [""]
     * " " --> [""]
     * "Halten/Parken auf Gehweg" --> ["Halten/Parken auf Gehweg"]
     * "Beschädigung eines Pollers" --> editor text = "Beschädigung eines Pollers"
     */
    fun setValue(text: String?) {
        if(text == null) {
            selectedIndex = 0
        } else {
            val txt = text.trim()
            val lowerTxt = txt.lowercase()
            val sItem = OffenseComboBoxModel.OFFENSES.find { lowerTxt == it.lowercase() }
            if (sItem == null) {
                setEditorText(txt)
            } else {
                selectedItem = sItem
            }
        }
    }

    init {
        model = mod
        renderer = OffenseListCellRenderer()

        val keyListener = object: KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                log.debug("keyReleased()")
                mod.setFilter(getEditorText())
                hidePopup()
                showPopup()
            }
        }

        setEditable(true)
        maximumRowCount = 15

        val editComp = editor.editorComponent
        if(editComp is JTextComponent) {
            log.debug("editorComponent is JTextComponent")
            editComp.addKeyListener(keyListener)
        }
    }

}