package de.heikozelt.wegefrei.gui

/**
 * todo: Prio 3: ermöglichen mehrere E-Mail-Adressen durch Komma getrennt anzugeben
 * Aufbau einer E-Mail-Adresse: "irgendwer@irgendeine_domain"
 */
class EMailVerifier : PatternVerifier("^(.+@.+)?$")
