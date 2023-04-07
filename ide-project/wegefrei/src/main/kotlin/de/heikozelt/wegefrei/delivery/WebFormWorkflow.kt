package de.heikozelt.wegefrei.delivery

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.gui.showValidationErrors
import java.time.ZonedDateTime

class WebFormWorkflow(private var webForm: WebForm, private var dbRepo: DatabaseRepo): Thread() {

    init {
        webForm.setSuccessfullySentCallback(::successfullySentCallback)
        webForm.setFailedCallback(::failedCallback)
    }

    /**
     * In Hintergrund-Thread ausführen, denn das Ausfüllen des Formulars dauert relativ lange und
     * dann wird auch noch gewartet, bis die Anwenderin auf Absenden klickt.
     */
    override fun run() {
        val errors = webForm.validate()
        if(errors.isEmpty()) {
            webForm.sendNotice()
        } else {
            showValidationErrors(errors)
        }
    }

    fun validateAndSend() {
        start()
    }

    fun successfullySentCallback(noticeId: Int, sentTime: ZonedDateTime) {
        dbRepo.updateNoticeFinalizedAndSent(noticeId, sentTime)
        // todo: Fenster schließen und Status in NoticesFrame updaten
    }

    fun failedCallback(noticeId: Int) {
        dbRepo.updateNoticeSendFailed(noticeId)
    }
}