package de.heikozelt.wegefrei.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VehicleMakesComboBoxModelTest {

    @Test
    fun constr() {
        val m = VehicleMakesComboBoxModel()
        assertEquals(78, m.size)
    }

    @Test
    fun setFilter() {
        val m = VehicleMakesComboBoxModel()
        m.setFilter("X") // Lexus & Vauxhall
        assertEquals(2, m.size)
        m.setFilter("vo") // Volkswagen & Volvo
        assertEquals(2, m.size)
    }

    @Test
    fun setFilter_empty() {
        val m = VehicleMakesComboBoxModel()
        m.setFilter("") // Lexus & Vauxhall
        assertEquals(78, m.size)
    }
}