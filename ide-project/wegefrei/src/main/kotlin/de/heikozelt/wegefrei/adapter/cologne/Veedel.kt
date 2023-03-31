package de.heikozelt.wegefrei.adapter.cologne

/**
 * Stadtteile von Köln in der Schreibweise der Werte der Combo-Box im Web-Formular.
 * Veedel ist Kölsch für Stadtteil/Viertel. 86 Kölner Veedel – En Stadt – E Jeföhl.
 * https://formular-server.de/Koeln_FS/findform?shortname=32-F68_file_AnzVerkW&formtecid=3&areashortname=send_html
 */
class Veedel {
    companion object {
        var veedels = arrayOf(
            "Altstadt/Nord", "Altstadt/Süd",
            "Bayenthal", "Bickendorf", "Bilderstöckchen", "Blumenberg", "Bocklemünd/Mengenich",
            "Braunsfeld", "Brück", "Buchforst", "Buchheim",
            "Chorweiler", "Dellbrück", "Deutz", "Dünnwald",
            "Ehrenfeld", "Eil", "Elsdorf", "Ensen", "Esch/Auweiler",
            "Finkenberg", "Flittard", "Fühlingen",
            "Godorf", "Gremberghoven", "Grengel",
            "Hahnwald", "Heimersdorf", "Höhenberg", "Höhenhaus", "Holweide", "Humboldt/Gremberg",
            "Immendorf", "Junkersdorf", "Kalk", "Klettenberg",
            "Langel", "Libur", "Lind", "Lindenthal", "Lindweiler", "Longerich", "Lövenich",
            "Marienburg", "Mauenheim", "Merheim", "Merkenich", "Meschenich", "Mülheim", "Müngersdorf",
            "Neubrück", "Neuehrenfeld", "Neustadt/Nord", "Neustadt/Süd", "Niehl", "Nippes",
            "Ossendorf", "Ostheim", "Pesch", "Poll", "Porz",
            "Raderberg", "Raderthal", "Rath/Heumar", "Riehl", "Rodenkirchen", "Roggendorf/Thenhoven", "Rondorf",
            "Seeberg", "Stammheim", "Sülz", "Sürth", "Urbach", "Vingst", "Vogelsang", "Volkhoven/Weiler",
            "Wahn", "Wahnheide", "Weiden", "Weidenpesch", "Weiß", "Westhoven", "Widdersdorf", "Worringen",
            "Zollstock", "Zündorf"
        )
    }
}