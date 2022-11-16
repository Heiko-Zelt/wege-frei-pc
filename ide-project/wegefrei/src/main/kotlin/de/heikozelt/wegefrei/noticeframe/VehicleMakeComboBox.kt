package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.VehicleMakesComboBoxModel
import org.slf4j.LoggerFactory
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JComboBox
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

class VehicleMakeComboBox: JComboBox<String?>() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val mod = VehicleMakesComboBoxModel()

    fun updateModel(filter: String) {
        mod.setFilter(filter)
        //showPopup()
    }

    init {
        model = mod

        val docListener = object: DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = changed(e)
            override fun removeUpdate(e: DocumentEvent?) = changed(e)
            override fun changedUpdate(e: DocumentEvent?) { }

            private fun changed(e: DocumentEvent?) {
                log.debug("event: $e")
                log.debug("event.type ${e?.type}")
                if (e != null) {
                    val doc = e.document
                    val filter = doc.getText(0, doc.length)
                    updateModel(filter)
                }
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

        val keyListener = object: KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                log.debug("keyReleased()")
                mod.setFilter(getEditorText())
                //maximumRowCount = 15
                hidePopup()
                showPopup()
            }
        }

        setEditable(true)
        maximumRowCount = 15
        //mod.setFilter("vo")

        val editComp = editor.editorComponent
        if(editComp is JTextComponent) {
            log.debug("editorComponent is is JTextComponent")
            //editComp.document = PlainDocument()
            //editComp.document.addDocumentListener(docListener)
            editComp.addKeyListener(keyListener)
        }
    }

}