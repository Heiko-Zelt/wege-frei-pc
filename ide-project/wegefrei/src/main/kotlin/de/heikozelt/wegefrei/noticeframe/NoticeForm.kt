package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.model.SelectedPhotosListModel
import org.slf4j.LoggerFactory
import javax.swing.*

/**
 * Meldungsformular mit Eingabefeldern und Button-Leiste
 */
class NoticeForm(
    private val noticeFrame: NoticeFrame,
    selectedPhotosListModel: SelectedPhotosListModel,
    dbRepo: DatabaseRepo
) : JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val noticeFormFields = NoticeFormFields(noticeFrame, selectedPhotosListModel, dbRepo)
    private var noticeFormFieldsScrollPane = JScrollPane(noticeFormFields)
    private val deleteButton = JButton("Löschen")

    init {
        val okButton = JButton("Ok")
        okButton.addActionListener { noticeFrame.saveAndClose() }
        val cancelButton = JButton("Abbrechen")
        cancelButton.addActionListener { noticeFrame.cancelAndClose() }
        deleteButton.isVisible = false
        deleteButton.addActionListener { noticeFrame.deleteAndClose() }
        val sendButton = JButton("E-Mail absenden")
        sendButton.addActionListener { noticeFrame.sendNotice() }

        val lay = GroupLayout(this)
        lay.autoCreateGaps = false
        lay.autoCreateContainerGaps = false
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(noticeFormFieldsScrollPane)
                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            GroupLayout.PREFERRED_SIZE,
                            Int.MAX_VALUE
                        )
                        .addComponent(okButton)
                        .addComponent(cancelButton)
                        .addComponent(sendButton)
                        .addComponent(deleteButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(noticeFormFieldsScrollPane)
                .addGroup(
                    lay.createParallelGroup()
                        .addComponent(okButton)
                        .addComponent(cancelButton)
                        .addComponent(sendButton)
                        .addComponent(deleteButton)
                )
        )
        layout = lay
    }

    /**
     * delegiert und aktualisiert die Buttons-Leiste
     */
    fun setNotice(noticeEntity: NoticeEntity) {
        noticeFormFields.setNotice(noticeEntity)

        // Nur beim Bearbeiten einer bereits existierenden Meldung einen Löschen-Button anzeigen.
        if(noticeEntity.id != null) {
            deleteButton.isVisible = true
        }

        // todo Prio 2: Text des Senden-Buttons ändern in "E-Mail erneut senden", wenn bereits gesendet.
    }

    /**
     * delegiert nur
     */
    fun getNotice(): NoticeEntity {
        return noticeFormFields.getNotice()
    }

    fun getNoticeFormFields(): NoticeFormFields {
        return noticeFormFields
    }

    fun enableOrDisableEditing() {
        noticeFormFields.enableOrDisableEditing()
    }
}