package de.heikozelt.wegefrei.scanframe

import de.heikozelt.wegefrei.db.DatabaseRepo
import de.heikozelt.wegefrei.jobs.ScanWorker
import java.awt.Dimension
import javax.swing.*

/**
 * Layout:
 * <pre>
 * Fortschritt:
 * ########..............
 * Dateinamen lesen|Metadaten lesen|Indexieren|Fertig.
 * (Abbrechen)|(Ok)
 * </pre>
 */
class ScanFrame: JFrame() {
    private val progressBar = JProgressBar(0, 100)
    private val statusLabel = JLabel("Laden...")
    private val cancelButton = JButton("Abbrechen")
    private val okButton = JButton("Ok")

    init {
        val progressLabel = JLabel("Fortschritt:")
        okButton.isVisible = false
        okButton.addActionListener { this.dispose() }

        title = "Fotos scannen - Wege frei!"
        size = Dimension(400, 150)
        val lay = GroupLayout(contentPane)
        lay.autoCreateGaps = true
        lay.autoCreateContainerGaps = true
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(progressLabel)
                .addComponent(progressBar)
                .addComponent(statusLabel)
                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addComponent(okButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(progressLabel)
                .addComponent(progressBar)
                .addComponent(statusLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE)
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(cancelButton)
                        .addComponent(okButton)
                )
        )
        layout = lay
        isVisible = true
    }

    fun scan(photosDir: String, databaseRepo: DatabaseRepo) {
            val worker = ScanWorker(this, photosDir, databaseRepo)
            worker.execute()
    }

    fun updateProgressBar(progress: Int) {
        progressBar.model.value = progress
    }

    fun updatePhase(phase: ScanWorker.Companion.IntermediateResult) {
        statusLabel.text = when(phase) {
            ScanWorker.Companion.IntermediateResult.START -> "Initialisierung..."
            ScanWorker.Companion.IntermediateResult.FILE_NAME -> "Dateinamen lesen..."
            ScanWorker.Companion.IntermediateResult.META_DATA -> "Metadaten lesen..."
            ScanWorker.Companion.IntermediateResult.INDEX_ENTRY -> "Indexieren..."
        }
    }

    fun done() {
        progressBar.model.value = 100
        statusLabel.text = "Fertig."
        cancelButton.isVisible = false
        okButton.isVisible = true
    }
}