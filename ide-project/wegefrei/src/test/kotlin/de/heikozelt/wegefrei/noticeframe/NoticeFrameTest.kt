package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.json.Witness
import org.junit.jupiter.api.Test

class NoticeFrameTest {

    @Test
    fun buildMailContent() {
        val notice = Notice()
        val witness = Witness()
        val content = NoticeFrame.buildMailContent(notice, witness)
        print(content)
    }
}