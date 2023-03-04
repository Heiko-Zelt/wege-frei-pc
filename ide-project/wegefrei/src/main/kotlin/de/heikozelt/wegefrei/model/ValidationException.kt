package de.heikozelt.wegefrei.model

class ValidationException(val validationErrors: MutableList<String>) : Throwable() {
}
