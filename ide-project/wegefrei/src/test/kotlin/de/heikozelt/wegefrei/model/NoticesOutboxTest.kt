package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.json.Witness
import de.heikozelt.wegefrei.model.NoticesOutbox
import org.junit.jupiter.api.Test

class NoticesOutboxTest {

    @Test
    fun buildMailContent() {
        val noticeEntity = NoticeEntity()
        val witness = Witness()
        val content = NoticesOutbox.buildMailContent(noticeEntity, witness)
        print(content)
        // todo asserts
    }
}