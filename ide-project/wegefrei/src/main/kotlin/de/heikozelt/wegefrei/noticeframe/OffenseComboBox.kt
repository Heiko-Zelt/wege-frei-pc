package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Offense
import org.slf4j.LoggerFactory
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox

class OffenseComboBox: JComboBox<Offense>(Offense.selectableOffenses()) {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    fun updateModel(filter: String) {
        dataModel?.let { m ->
            if (m is DefaultComboBoxModel) {
                m.removeAllElements()
                m.addAll(Offense.offensesFiltered(filter))
            }
        }
        showPopup()
    }

    init {
        /*
        val docListener = object: DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = changed(e)
            override fun removeUpdate(e: DocumentEvent?) = changed(e)
            override fun changedUpdate(e: DocumentEvent?) = changed(e)

            private fun changed(e: DocumentEvent?) {
                if (e != null) {
                    val doc = e.document
                    val filter = doc.getText(0, doc.length)
                    updateModel(filter)
                }
            }
        }
        */

        renderer = OffenseListCellRenderer()

        /*
        editor = BasicComboBoxEditor()
        isEditable = true

        val editComp = editor.editorComponent
        if(editComp is JTextComponent) {
            log.debug("editorComponent is is JTextComponent")
            editComp.document = PlainDocument()
            editComp.document.addDocumentListener(docListener)
        }
         */
    }

}