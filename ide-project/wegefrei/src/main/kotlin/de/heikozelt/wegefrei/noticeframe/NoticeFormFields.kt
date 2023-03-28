package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.durationInMinutesFormatted
import de.heikozelt.wegefrei.email.combobox.RecipientComboBox
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.gui.*
import de.heikozelt.wegefrei.maps.MiniMap
import de.heikozelt.wegefrei.model.Photo
import de.heikozelt.wegefrei.model.SelectedPhotosListDataEvent
import de.heikozelt.wegefrei.model.SelectedPhotosListModel
import de.heikozelt.wegefrei.model.VehicleColor
import org.jxmapviewer.viewer.TileFactory
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener
import javax.swing.text.AbstractDocument


/**
 * Panel mit den Formular-Feldern
 *
 * Anordnung der Check Boxes:
 * <pre>
 * [] Fahrzeug war verlassen        [] mit Behinderung [] Umweltplakette fehlt/ungültig
 * [] Warnblinkanlage eingeschaltet [] mit Gefährdung  [] HU-Plakette abgelaufen
 * </pre>
 * todo Prio 4: Feature: Rechtschreibprüfung insbesondere für Hinweis-Textarea
 * todo Prio 1: Bug: HU-Fälligkeit wird nicht mehr angezeigt, wenn Nachricht gesendet und wieder geöffnet wird.
 */
class NoticeFormFields(
    private val noticeFrame: NoticeFrame,
    private val selectedPhotosListModel: SelectedPhotosListModel,
    private val dbRepo: DatabaseRepo,
    private val tileFactory: TileFactory
) : JPanel(), ListDataListener /* SelectedPhotosObserver */ {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    //private val countrySymbolComboBox = JComboBox(CountrySymbol.COUNTRY_SYMBOLS)
    private val countryComboBox = CountryComboBox()
    private val licensePlateTextField = TrimmingTextField(10)

    //private val vehicleMakeComboBox = JComboBox(ListVehicleMakes.VEHICLE_MAKES)
    private val vehicleMakeComboBox = VehicleMakeComboBox()
    private val colorComboBox = JComboBox(VehicleColor.COLORS)
    private val vehicleTypeComboBox = VehicleTypeComboBox()
    private val miniMap = MiniMap(noticeFrame, selectedPhotosListModel, tileFactory)
    private var streetTextField = TrimmingTextField(30)
    private var zipCodeTextField = TrimmingTextField(5)
    private var townTextField = TrimmingTextField(30)
    private var locationDescriptionTextField = TrimmingTextField(40)
    private var quarterTextField = TrimmingTextField(30)

    private var offenseComboBox = OffenseComboBox()

    //private var offenseComboBox = JComboBox(Offense.selectableOffenses())
    private val observationDateTextField = JTextField(10)
    private val observationTimeTextField = TrimmingTextField(8)
    private val endDateTextField = JTextField(10)
    private val endTimeTextField = TrimmingTextField(8)
    private val calculatedDurationLabel = JLabel("")
    private val abandonedCheckBox = JCheckBox("Fahrzeug war verlassen")
    private val obstructionCheckBox = JCheckBox("mit Behinderung")
    private val endangeringCheckBox = JCheckBox("mit Gefährdung")
    private val environmentalStickerCheckBox = JCheckBox("Umweltplakette fehlt/ungültig")
    private val vehicleInspectionStickerCheckBox = JCheckBox("HU-Plakette abgelaufen")
    private val warningLightsCheckBox = JCheckBox("Warnblinkanlage eingeschaltet")
    private val inspectionMonthYearLabel = JLabel("HU-Fälligkeit Monat/Jahr:")
    private val inspectionMonthTextField = JTextField(2)
    private val monthYearSeparatorLabel = JLabel("/")
    private val inspectionYearTextField = JTextField(4)

    private val emailDeliveryRadioButton = JRadioButton("E-Mail")
    private val webFormDeliveryRadioButton = JRadioButton("Web-Formular")
    private val deliveryTypeGroup = ButtonGroup()

    // Idealfall: Addresse wird automatisch eingetragen, Ausnahmefall Benutzer wählt aus Adressbuch
    // todo Prio 3: Auswahl des Empfängers aus Addressbuch (Button öffnet "AddressChooser")
    // todo Prio 3: eine Adresse aus der Datenbank anhand der GeoPosition vorschlagen
    private val recipientComboBox = RecipientComboBox(dbRepo)
    private val noteTextArea = JTextArea(2, 40)

    //private var noticeEntity: NoticeEntity? = null

    init {
        log.debug("init")

        // GUI components
        val licensePlateLabel = JLabel("<html>Landes- & Kfz-Kennzeichen:</html>")
        //countryComboBox.renderer = CountrySymbolListCellRenderer()
        val licensePlateDoc = licensePlateTextField.document
        if (licensePlateDoc is AbstractDocument) {
            licensePlateDoc.documentFilter = UppercaseDocumentFilter()
        }
        val vehicleMakeLabel = JLabel("Fahrzeugmarke & Farbe:")
        //vehicleMakeComboBox.isEditable = true
        val vehicleTypeLabel = JLabel("Fahrzeugart:")

        colorComboBox.renderer = ColorListCellRenderer()
        colorComboBox.maximumRowCount = VehicleColor.COLORS.size
        miniMap.toolTipText = "Bitte positionieren Sie den roten Pin."
        val coordinatesLabel = JLabel("Koordinaten:")
        val streetLabel = JLabel("<html>Straße & Hausnummer:<sup>(*)</sup></html>")
        streetTextField.toolTipText = "z.B. Taunusstraße 7"
        val zipCodeTownLabel = JLabel("<html>PLZ:<sup>(*)</sup>, Ort:<sup>(*)</sup></html>")
        zipCodeTextField.toolTipText = "z.B. 65183"
        val quarterLabel = JLabel("<html>Stadtteil:<sup>(*)</sup></html>")
        zipCodeTextField.toolTipText = "z.B. Altstadt-Nord"
        val locationDescriptionLabel = JLabel("<html>Tatort <sup>(*):</sup></html>")
        townTextField.toolTipText = "z.B. Wiesbaden"
        locationDescriptionTextField.toolTipText = "z.B. Bushaltestelle Kochbrunnen"

        // todo Prio 1: Auto-complete
        // Benutzer gibt ein Wortbestandteil ein. Die Einträge im PullDownMenü werden gefiltert.
        val offenseLabel = JLabel("<html>Verstoß:<sup>*</sup></html>")
        //offenseComboBox.renderer = OffenseListCellRenderer()

        val observationDateTimeLabel = JLabel("<html>Beobachtungsdatum<sup>*</sup>, Uhrzeit:<sup>*</sup></html>")

        val observationDateDoc = observationDateTextField.document
        if (observationDateDoc is AbstractDocument) {
            observationDateDoc.documentFilter = CharPredicateDocFilter.dateDocFilter
        }

        val endDateTimeLabel = JLabel("Endedatum, Uhrzeit:")

        observationDateTextField.toolTipText = "z.B. 31.12.2021"
        observationDateTextField.inputVerifier = PatternVerifier.dateVerifier
        val observationTimeDoc = observationTimeTextField.document
        if (observationTimeDoc is AbstractDocument) {
            observationTimeDoc.documentFilter = CharPredicateDocFilter.timeDocFilter
        }
        observationTimeTextField.toolTipText = "z.B. 23:59:59"
        observationTimeTextField.inputVerifier = PatternVerifier.timeVerifier

        val durationLabel = JLabel("Dauer:")
        durationLabel.toolTipText = "abgerundet"
        vehicleInspectionStickerCheckBox.addChangeListener {
            val src = it.source as JCheckBox
            inspectionMonthYearLabel.isVisible = src.isSelected
            inspectionMonthTextField.isVisible = src.isSelected
            monthYearSeparatorLabel.isVisible = src.isSelected
            inspectionYearTextField.isVisible = src.isSelected
        }
        vehicleInspectionStickerCheckBox.toolTipText = "Hauptuntersuchung/\"TÜV\""
        val inspectionMonthDoc = inspectionMonthTextField.document
        if (inspectionMonthDoc is AbstractDocument) {
            inspectionMonthDoc.documentFilter = CharPredicateDocFilter.onlyDigitsDocFilter
        }
        inspectionMonthTextField.toolTipText = "Ganzzahl 1-12"
        inspectionMonthTextField.inputVerifier = PatternVerifier.inspectionMonthVerifier
        inspectionMonthTextField.isVisible = false
        val inspectionYearDoc = inspectionYearTextField.document
        if (inspectionYearDoc is AbstractDocument) {
            inspectionYearDoc.documentFilter = CharPredicateDocFilter.onlyDigitsDocFilter
        }
        inspectionYearTextField.toolTipText = "Ganzzahl 4-stellig"
        inspectionYearTextField.inputVerifier = PatternVerifier.inspectionYearVerifier
        inspectionYearTextField.isVisible = false

        val deliveryTypeLabel = JLabel("<html>Zustellung:<sup>*</sup></html>")
        deliveryTypeGroup.add(emailDeliveryRadioButton)
        deliveryTypeGroup.add(webFormDeliveryRadioButton)

        val recipientLabel = JLabel("<html>Empfänger:<sup>*</sup></html>")
        recipientComboBox.toolTipText = "z.B. verwarngeldstelle@wiesbaden.de"
        recipientComboBox.inputVerifier = PatternVerifier.eMailVerifier
        val noteLabel = JLabel("Hinweis:")
        noteTextArea.toolTipText = "z.B. Behinderung / Gefährdung beschreiben"

        // layout
        val lay = GroupLayout(this)
        lay.autoCreateGaps = true
        lay.autoCreateContainerGaps = true

        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup( // labels & form fields
                    lay.createSequentialGroup()
                        .addGroup( // labels
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(licensePlateLabel)
                                .addComponent(vehicleMakeLabel)
                                .addComponent(vehicleTypeLabel)
                                .addComponent(coordinatesLabel)
                                .addComponent(streetLabel)
                                .addComponent(zipCodeTownLabel)
                                .addComponent(quarterLabel)
                                .addComponent(locationDescriptionLabel)
                                .addComponent(offenseLabel)
                                .addComponent(observationDateTimeLabel)
                                .addComponent(endDateTimeLabel)
                                .addComponent(durationLabel)
                                .addComponent(deliveryTypeLabel)
                                .addComponent(recipientLabel)
                                .addComponent(inspectionMonthYearLabel)
                                .addComponent(noteLabel)
                        )
                        .addGroup( // form fields
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(countryComboBox)
                                        .addComponent(licensePlateTextField)
                                )
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(vehicleMakeComboBox)
                                        .addComponent(colorComboBox)
                                )
                                .addComponent(vehicleTypeComboBox)
                                .addComponent(miniMap)
                                .addComponent(streetTextField)
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(zipCodeTextField)
                                        .addComponent(townTextField)
                                )
                                .addComponent(locationDescriptionTextField)
                                .addComponent(quarterTextField)
                                .addComponent(offenseComboBox)
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(observationDateTextField)
                                        .addComponent(observationTimeTextField)
                                )
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(endDateTextField)
                                        .addComponent(endTimeTextField)
                                )
                                .addComponent(calculatedDurationLabel)
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(inspectionMonthTextField)
                                        .addComponent(monthYearSeparatorLabel)
                                        .addComponent(inspectionYearTextField)
                                )
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(emailDeliveryRadioButton)
                                        .addComponent(webFormDeliveryRadioButton)
                                )
                                .addComponent(recipientComboBox)
                                .addComponent(noteTextArea)
                        )
                )
                .addGroup( // check boxes
                    lay.createSequentialGroup()
                        .addGroup( // 1st column of check boxes
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(abandonedCheckBox)
                                .addComponent(warningLightsCheckBox)

                        )
                        .addGroup( // 2nd column of check boxes
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(obstructionCheckBox)
                                .addComponent(endangeringCheckBox)
                        )
                        .addGroup( // 3rd column of check boxes
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(environmentalStickerCheckBox)
                                .addComponent(vehicleInspectionStickerCheckBox)
                        )
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(licensePlateLabel).addComponent(countryComboBox)
                        .addComponent(licensePlateTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(vehicleMakeLabel).addComponent(vehicleMakeComboBox).addComponent(colorComboBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(vehicleTypeLabel).addComponent(vehicleTypeComboBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(coordinatesLabel).addComponent(miniMap)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(streetLabel).addComponent(streetTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(zipCodeTownLabel).addComponent(zipCodeTextField).addComponent(townTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(quarterLabel).addComponent(quarterTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(locationDescriptionLabel).addComponent(locationDescriptionTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(offenseLabel).addComponent(offenseComboBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(observationDateTimeLabel).addComponent(observationDateTextField)
                        .addComponent(observationTimeTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(endDateTimeLabel).addComponent(endDateTextField)
                        .addComponent(endTimeTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(durationLabel).addComponent(calculatedDurationLabel)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(abandonedCheckBox).addComponent(obstructionCheckBox)
                        .addComponent(environmentalStickerCheckBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(warningLightsCheckBox).addComponent(endangeringCheckBox)
                        .addComponent(vehicleInspectionStickerCheckBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(inspectionMonthYearLabel).addComponent(inspectionMonthTextField)
                        .addComponent(monthYearSeparatorLabel).addComponent(inspectionYearTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(deliveryTypeLabel).addComponent(emailDeliveryRadioButton).addComponent(webFormDeliveryRadioButton)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(recipientLabel).addComponent(recipientComboBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(noteLabel).addComponent(noteTextArea)
                )
        )
        layout = lay
        components.filterIsInstance<JTextField>().forEach(Styles::restrictHeight)
        components.filterIsInstance<JComboBox<*>>().forEach(Styles::restrictSize)
        components.filterIsInstance<JLabel>().forEach(Styles::restrictSize)
        Styles.restrictSize(licensePlateTextField)
        Styles.restrictSize(zipCodeTextField)
        Styles.restrictSize(observationDateTextField)
        Styles.restrictSize(observationTimeTextField)
        Styles.restrictSize(endDateTextField)
        Styles.restrictSize(endTimeTextField)
        Styles.restrictSize(inspectionYearTextField)
        Styles.restrictSize(inspectionMonthTextField)

        enableOrDisableEditing(false)
    }

    /**
     * Initialisieren der einzelnen Eingabe-Felder
     * Mapping von Notice zu GUI-Components
     */
    fun initWithNotice(noticeEntity: NoticeEntity) {
        //this.noticeEntity = noticeEntity

        noticeEntity.getGeoPosition()?.let {
            miniMap.setOffensePosition(it)
        }
        countryComboBox.setValue(noticeEntity.countrySymbol)
        licensePlateTextField.text = noticeEntity.licensePlate
        vehicleMakeComboBox.setValue(noticeEntity.vehicleMake)
        vehicleTypeComboBox.setValue(noticeEntity.vehicleType)
        colorComboBox.selectedItem = VehicleColor.fromColorName(noticeEntity.color)
        streetTextField.text = noticeEntity.street
        zipCodeTextField.text = noticeEntity.zipCode
        townTextField.text = noticeEntity.town
        locationDescriptionTextField.text = noticeEntity.locationDescription
        offenseComboBox.setValue(noticeEntity.offense)
        observationDateTextField.text = blankOrDateString(noticeEntity.observationTime)
        observationTimeTextField.text = blankOrTimeString(noticeEntity.observationTime)
        endDateTextField.text = blankOrDateString(noticeEntity.endTime)
        endTimeTextField.text = blankOrTimeString(noticeEntity.endTime)
        calculatedDurationLabel.text = noticeEntity.getDurationFormatted()
        obstructionCheckBox.isSelected = noticeEntity.obstruction
        endangeringCheckBox.isSelected = noticeEntity.endangering
        environmentalStickerCheckBox.isSelected = noticeEntity.environmentalStickerMissing
        vehicleInspectionStickerCheckBox.isSelected = noticeEntity.vehicleInspectionExpired
        inspectionMonthYearLabel.isVisible = inspectionYearTextField.text.isNotBlank()
        inspectionYearTextField.isVisible = inspectionYearTextField.text.isNotBlank()
        inspectionYearTextField.text = blankOrShortString(noticeEntity.vehicleInspectionYear)
        inspectionMonthTextField.isVisible = inspectionMonthTextField.text.isNotBlank()
        inspectionMonthTextField.text = blankOrByteString(noticeEntity.vehicleInspectionMonth)
        abandonedCheckBox.isSelected = noticeEntity.vehicleAbandoned
        warningLightsCheckBox.isSelected = noticeEntity.warningLights
        recipientComboBox.loadData()
        recipientComboBox.setValue(noticeEntity.getRecipient())
        noteTextArea.text = noticeEntity.note

        val enab = !(noticeEntity.isFinalized())
        log.debug("enable? $enab")
        enableOrDisableEditing(enab)
    }

    /**
     * Mapping der Werte der GUI-Komponenten zu Meldung und Validierung der Eingaben.
     * Es wird nicht geprüft, ob Pflichtfelder ausgefüllt sind,
     * sondern nur ob ausgefüllte Felder im Format korrekt sind/geparst werden können/auf Datenbankfelder abgebildet werden können.
     * @return Liste mit Validierungsfehlern oder leere Liste, wenn Felder leer oder korrekt ausgefüllt sind.
     * todo Prio 1: endTime wird falsch gespeichert
     */
    fun validateAndMap(n: NoticeEntity): List<String> {
        // Normalerweise sollte vorher setNotice() aufgerufen worden sein.
        // Aber falls nicht, wird ein neues Notice-Objekt instanziiert.
        //val n = noticeEntity ?: NoticeEntity.createdNow()

        val errors = mutableListOf<String>()

        n.countrySymbol = countryComboBox.getValue()
        n.licensePlate = trimmedOrNull(licensePlateTextField.text)
        n.vehicleMake = vehicleMakeComboBox.getValue()
        n.vehicleType = vehicleTypeComboBox.getValue()

        val selectedColor = colorComboBox.selectedObjects[0] as VehicleColor
        n.color = if (selectedColor.color == null) {
            null
        } else {
            selectedColor.colorName
        }

        n.street = trimmedOrNull(streetTextField.text)
        n.zipCode = trimmedOrNull(zipCodeTextField.text)
        n.town = trimmedOrNull(townTextField.text)
        n.locationDescription = trimmedOrNull(locationDescriptionTextField.text)
        n.offense = offenseComboBox.getValue()

        // Problem: 2 Eingabefelder für Datum und Uhrzeit, aber nur ein Datenbankfeld
        // Lösung: Beide müssen ausgefüllt sein oder keins. Nur zusammen speichern oder gar nicht.
        // Ähnlich wie Längengrad und Breitengrad. Diese müssen auch zusammen oder gar nicht gespeichert werden.

        val format = DateTimeFormatter.ofPattern("d.M.yyyy")
        val obsDateTxt = observationDateTextField.text
        val obsTimeTxt = observationTimeTextField.text
        if(obsTimeTxt.isBlank() && obsDateTxt.isNotBlank()) {
            errors.add("Beobachtungsdatum angegeben, aber keine Uhrzeit.")
        }
        if(obsTimeTxt.isNotBlank() && obsDateTxt.isBlank()) {
            errors.add("Beobachtungsuhrzeit angegeben, aber kein Datum.")
        }
        var obsDat: LocalDate? = null
        if(obsDateTxt.isNotBlank()) {
            try {
                obsDat = LocalDate.parse(obsDateTxt, format)
            } catch (ex: DateTimeParseException) {
                errors.add("Beobachtungsdatum muss im Format Tag.Monat.Jahr angegeben sein.")
            }
        }
        var obsTim: LocalTime? = null
        if(obsTimeTxt.isNotBlank()) {
            try {
                val tFormat = DateTimeFormatter.ofPattern("H:m:s")
                obsTim = LocalTime.parse(obsTimeTxt, tFormat)
            } catch (ex: DateTimeParseException) {
                errors.add("Uhrzeit muss im Format Stunde:Minute:Sekunden angegeben sein.")
            }
        }
        n.observationTime = if(obsDat == null || obsTim == null) {
            null
        } else {
            ZonedDateTime.of(obsDat, obsTim, ZoneId.systemDefault())
        }

        var endDateTxt = endDateTextField.text
        val endTimeTxt = endTimeTextField.text
        if(endTimeTxt.isBlank() && endDateTxt.isNotBlank()) {
            errors.add("Enddatum angegeben, aber keine Uhrzeit.")
        }
        log.debug("endTimeTxt: $endTimeTxt")
        if(endTimeTxt.isNotBlank() && endDateTxt.isBlank()) {
            endDateTxt = obsTimeTxt
        }
        var endDat: LocalDate? = null
        if(endDateTxt.isNotBlank()) {
            try {
                endDat = LocalDate.parse(endDateTxt, format)
            } catch (ex: DateTimeParseException) {
                errors.add("Enddatum muss im Format Tag.Monat.Jahr angegeben sein.")
            }
        }
        var endTim: LocalTime? = null
        if(endTimeTxt.isNotBlank()) {
            try {
                val tFormat = DateTimeFormatter.ofPattern("H:m:s")
                endTim = LocalTime.parse(endTimeTxt, tFormat)
            } catch (ex: DateTimeParseException) {
                errors.add("Enduhrzeit muss im Format Stunde:Minute:Sekunden angegeben sein.")
            }
        }
        n.endTime = if(endDat == null || endTim == null) {
            null
        } else {
            ZonedDateTime.of(endDat, endTim, ZoneId.systemDefault())
        }
        log.debug("observationTime: ${n.observationTime}")
        log.debug("endTime: ${n.endTime}")

        //n.duration = intOrNull(durationTextField.text)
        n.obstruction = obstructionCheckBox.isSelected
        n.endangering = endangeringCheckBox.isSelected
        n.environmentalStickerMissing = environmentalStickerCheckBox.isSelected
        n.vehicleInspectionExpired = vehicleInspectionStickerCheckBox.isSelected
        n.vehicleInspectionYear = if (n.vehicleInspectionExpired) {
            shortOrNull(inspectionYearTextField.text)
        } else {
            null
        }
        n.vehicleInspectionMonth = if (n.vehicleInspectionExpired) {
            byteOrNull(inspectionMonthTextField.text)
        } else {
            null
        }
        n.vehicleAbandoned = abandonedCheckBox.isSelected
        n.warningLights = warningLightsCheckBox.isSelected

        val recipient = recipientComboBox.getValue()
        n.recipientEmailAddress = recipient?.address
        n.recipientName = recipient?.name

        n.note = trimmedOrNull(noteTextArea.text)
        return errors
    }

    fun getMiniMap(): MiniMap {
        return miniMap
    }

    fun setStreet(street: String) {
        streetTextField.text = street
    }

    fun setZipCode(zipCode: String) {
        zipCodeTextField.text = zipCode
    }

    fun setTown(town: String) {
        townTextField.text = town
    }

    /**
     * keine weitere Bearbeitung mehr zulassen,
     * wenn die Meldung bereits versendet wurde.
     */
    fun enableOrDisableEditing(enab: Boolean) {
        /*
        var enab = false
        noticeEntity?.let {
            enab = !it.isSent()
        }
         */
        //val enab = (notice != null) && !notice.isSent()
        countryComboBox.isEnabled = enab
        licensePlateTextField.isEnabled = enab
        vehicleMakeComboBox.isEnabled = enab
        colorComboBox.isEnabled = enab
        streetTextField.isEnabled = enab
        zipCodeTextField.isEnabled = enab
        townTextField.isEnabled = enab
        locationDescriptionTextField.isEnabled = enab
        offenseComboBox.isEnabled = enab
        observationDateTextField.isEnabled = enab
        observationTimeTextField.isEnabled = enab
        //durationTextField.isEnabled = enab
        obstructionCheckBox.isEnabled = enab
        endangeringCheckBox.isEnabled = enab
        environmentalStickerCheckBox.isEnabled = enab
        vehicleInspectionStickerCheckBox.isEnabled = enab
        inspectionYearTextField.isEnabled = enab
        inspectionMonthTextField.isEnabled = enab
        abandonedCheckBox.isEnabled = enab
        recipientComboBox.isEnabled = enab
        warningLightsCheckBox.isEnabled = enab
        noteTextArea.isEnabled = enab
    }

    fun selectedPhoto(photo: Photo) {
        if (photo.getDateTime() != null) {
            updateDateTimeAndDuration()
        }
    }

    fun unselectedPhoto(photo: Photo) {
        if (photo.getDateTime() != null) {
            updateDateTimeAndDuration()
        }
    }

    fun replacedPhotoSelection(photos: TreeSet<Photo>) {
        if (photos.size != 0) {
            updateDateTimeAndDuration()
        }
    }

    private fun updateDateTimeAndDuration() {
        val newStartTime = selectedPhotosListModel.getStartTime()
        observationDateTextField.text = blankOrDateString(newStartTime)
        observationTimeTextField.text = blankOrTimeString(newStartTime)
        val newEndTime = selectedPhotosListModel.getEndTime()
        endDateTextField.text = blankOrDateString(newEndTime)
        endTimeTextField.text = blankOrTimeString(newEndTime)
        // todo Prio 1: format in minutes
        calculatedDurationLabel.text = durationInMinutesFormatted(selectedPhotosListModel.getStartTime(), selectedPhotosListModel.getEndTime())
    }

    companion object {
        /**
         * trimms String and returns null if blank
         * to be used before storing a String to the database
         * examples:
         * <ul>
         *   <li>null -> null</li>
         *   <li>"" -> null</li>
         *   <li>" " -> null</li>
         *   <li>" Hello " -> "Hello"</li>
         *   <li>"Hello\n" -> "Hello"</li>
         * </ul>
         */
        fun trimmedOrNull(str: String?): String? {
            return if (str == null) {
                null
            } else {
                val trimmed = str.trim()
                if (trimmed == "") {
                    null
                } else {
                    trimmed
                }
            }
        }

        /**
         * <ul>
         *   <li>null -> null</li>
         *   <li>"" -> null</li>
         *   <li>" " -> null</li>
         *   <li>"1" -> 1</li>
         *   <li>"01" -> 1</li>
         * </ul>
         */
        fun intOrNull(str: String?): Int? {
            return if (str.isNullOrBlank()) {
                null
            } else {
                str.toInt()
            }
        }

        fun shortOrNull(str: String?): Short? {
            return if (str.isNullOrBlank()) {
                null
            } else {
                str.toShort()
            }
        }

        fun byteOrNull(str: String?): Byte? {
            return if (str.isNullOrBlank()) {
                null
            } else {
                str.toByte()
            }
        }

        fun blankOrString(str: String?): String {
            return str ?: ""
        }

        fun blankOrIntString(i: Int?): String {
            return i?.toString() ?: ""
        }

        fun blankOrShortString(s: Short?): String {
            return s?.toString() ?: ""
        }

        fun blankOrByteString(b: Byte?): String {
            return b?.toString() ?: ""
        }

        fun blankOrDateString(zdt: ZonedDateTime?): String {
            return if (zdt == null) {
                ""
            } else {
                val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                fmt.format(zdt)
            }
        }

        fun blankOrTimeString(zdt: ZonedDateTime?): String {
            return if (zdt == null) {
                ""
            } else {
                val fmt = DateTimeFormatter.ofPattern("HH:mm:ss")
                fmt.format(zdt)
            }
        }
    }

    override fun intervalAdded(e: ListDataEvent?) {
        if (e is SelectedPhotosListDataEvent) {
            e.photos.forEach { selectedPhoto(it) }
        }
    }

    override fun intervalRemoved(e: ListDataEvent?) {
        if (e is SelectedPhotosListDataEvent) {
            e.photos.forEach { unselectedPhoto(it) }
        }
    }

    override fun contentsChanged(e: ListDataEvent?) {
        // ignore
    }
}