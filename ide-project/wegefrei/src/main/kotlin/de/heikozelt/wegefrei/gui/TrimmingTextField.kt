package de.heikozelt.wegefrei.gui

import javax.swing.JTextField

class TrimmingTextField(columns: Int): JTextField(columns) {

    init {
        addFocusListener(TrimmingTextFieldFocusListener())
    }

}