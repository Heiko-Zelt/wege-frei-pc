package de.heikozelt.wegefrei.model

class ValidationException(val validationErrors: MutableList<String>) : Throwable() {

    override fun toString(): String {
        return validationErrors.joinToString("\n")
    }
}
