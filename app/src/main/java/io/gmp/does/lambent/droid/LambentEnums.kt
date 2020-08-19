package io.gmp.does.lambent.droid

enum class TickEnum(speed: Double, name: String){
    HUNDREDTHS (.01, "HUNDREDTHS"),
    FHUNDREDTHS (.05, "FHUNDREDTHS"),
    TENTHS (.1, "TENTHS"),
    FTENTHS (.5, "FTENTHS"),
    ONES (1.0, "ONES"),
    TWOS  (2.0, "TWOS"),
    FIVES  (5.0, "FIVES"),
    TENS  (10.0, "TENS"),
    TWENTYS  (20.0, "TWENTYS"),
    THIRTHYS  (30.0, "THIRTYS"),
    MINS  (60.0, "MINS")
}

enum class BrightnessSliderEnum(posn: Int, value: Int, label: String) {
    FULL  (5, 1, "100%"),
    HALF  (4, 2, "50%"),
    QUARTER  (3, 4, "25%"),
    PCT_TEN  (2, 10, "10%"),
    PCT_FIVE  (1, 20, "5%"),
    OFF  (0, 0, "0%")
}

enum class RunningEnum {
    RUNNING,
    NOTRUNNING
}

enum class BPP {
    RGB,
    GRB,
    RGBWW,
    RGBCW,
    RGBNW,
    RGBAW,
}