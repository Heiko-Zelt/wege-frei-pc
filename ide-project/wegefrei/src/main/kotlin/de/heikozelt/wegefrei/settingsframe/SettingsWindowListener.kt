package de.heikozelt.wegefrei.settingsframe

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class SettingsWindowListener(private val settingsFrame: SettingsFrame): WindowAdapter() {
    override fun windowClosing(e: WindowEvent?) {
        settingsFrame.mayAskMayClose()
    }
}