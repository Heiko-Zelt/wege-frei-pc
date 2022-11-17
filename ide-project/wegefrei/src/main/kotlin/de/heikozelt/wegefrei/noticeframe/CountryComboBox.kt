package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.gui.CountryListCellRenderer
import de.heikozelt.wegefrei.model.CountryComboBoxModel
import de.heikozelt.wegefrei.model.CountrySymbol
import org.slf4j.LoggerFactory
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JComboBox
import javax.swing.text.JTextComponent

// todo Synonyme: D = BRD = Deutschland = Germany
class CountryComboBox: JComboBox<CountrySymbol?>() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val mod = CountryComboBoxModel()

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
     *
     * val countrySymbol = CountrySymbol.fromAbbreviation(noticeEntity.countrySymbol)
     * countryComboBox.selectedItem = countrySymbol
     */
    fun getValue(): String? {
        selectedItem?.let {
            if(it is CountrySymbol) {
                return it.abbreviation.ifEmpty { null }
            }
        }
        return getEditorText()
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
    fun setValue(text: String?) {
        if(text == null) {
            selectedIndex = 0
        } else {
            val txt = text.trim()
            val lowerTxt = txt.lowercase()
            val countrySymbol = CountryComboBoxModel.COUNTRY_SYMBOLS.find { lowerTxt == it.abbreviation.lowercase() }
            if (countrySymbol == null) {
                setEditorText(txt)
            } else {
                selectedItem = countrySymbol
            }
        }
    }

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
        setRenderer(CountryListCellRenderer())
        setEditable(true)
        maximumRowCount = 15
        val editComp = editor.editorComponent
        if(editComp is JTextComponent) {
            log.debug("editorComponent is JTextComponent")
            editComp.addKeyListener(keyListener)
        }
    }
}