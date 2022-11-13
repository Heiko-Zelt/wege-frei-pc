package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.json.Witness
import org.junit.jupiter.api.Test

class NoticeEntityFrameTest {

    @Test
    fun buildMailContent() {
        val noticeEntity = NoticeEntity()
        val witness = Witness()
        val content = NoticeFrame.buildMailContent(noticeEntity, witness)
        print(content)
    }
}