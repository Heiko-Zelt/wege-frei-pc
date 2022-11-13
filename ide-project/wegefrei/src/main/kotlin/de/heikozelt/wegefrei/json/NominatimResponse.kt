package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json

/**
 * JSON-Antwort des Nominatim-Webservices
 * Die Felder house_number, road, city und postcode sind relevant.
 *
 * https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=50.07921600341797&lon=8.241934776306152
 * -->
 * {
 *   "place_id":147296246,
 *   "licence":"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright",
 *   "osm_type":"way",
 *   "osm_id":176219328,
 *   "lat":"50.079071850000005",
 *   "lon":"8.241947337710211",
 *   "place_rank":30,
 *   "category":"building",
 *   "type":"yes",
 *   "importance":0,
 *   "addresstype":"building",
 *   "name":null,
 *   "display_name":
 *   "22, Luisenstraße, Mitte, Wiesbaden, Hesse, 65185, Germany",
 *   "address": {
 *     "house_number":"22", <-----
 *     "road":"Luisenstraße", <-----
 *     "city_district":"Mitte",
 *     "city":"Wiesbaden", <-----
 *     "county":"Wiesbaden",
 *     "state":"Hesse",
 *     "ISO3166-2-lvl4":"DE-HE",
 *     "postcode":"65185", <-----
 *     "country":"Germany", (Die zuständige Behörde weiß schon für welches Land sie zuständig ist.)
 *     "country_code":"de"
 *   },
 *   "boundingbox":["50.0789883","50.0791299","8.2417875","8.2421109"]
 * }
 */
class NominatimResponse(
    @Json(name="display_name") val displayName: String,
    @Json(name="address") val nominatimAddress: NominatimAddress?
)