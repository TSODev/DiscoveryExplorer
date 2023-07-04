package utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Dateformat {

    companion object {
        fun DateFromString(dateTime: String): String {
            // Create a ZonedDateTime object by parsing the String date value
            val zonedDateTime = ZonedDateTime.parse(dateTime)

// Create a DateTimeFormatter object for formatting
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")

// Format the ZonedDateTime object to a String
            return formatter.format(zonedDateTime)


        }
    }
}