package de.heikozelt.wegefrei.noticeframe

import javax.swing.JComboBox

class VehicleTypeComboBox: JComboBox<String>(vehicleTypes) {

    fun getValue(): String? {
        val x = selectedItem.toString()
        return x.ifBlank { null }
    }

    fun setValue(v: String?) {
        val found = vehicleTypes.find {type -> type == v }
        if ( found != null ) {
            selectedItem = found
        }
    }

    companion object {
        val vehicleTypes =
            arrayOf("", "Pkw", "Lkw", "Lkw mit Anhänger", "Motorrad", "Bus", "Anhänger ohne Zugfahrzeug", "Sonstiges")
    }
}