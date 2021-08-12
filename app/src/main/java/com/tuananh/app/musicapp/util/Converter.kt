package com.tuananh.app.musicapp.util

class Converter {

    companion object {
        fun convertDuration(msec: Int): String {
            var seconds = "${((msec/1000) % 60)}"
            var minutes = "${((msec/(1000*60)) % 60)}"
            if(seconds.length < 2) seconds="0$seconds"
            if(minutes.length < 2) minutes="0$minutes"
            return "$minutes:$seconds"
        }
    }
}
