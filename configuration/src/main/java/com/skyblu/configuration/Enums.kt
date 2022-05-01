package com.skyblu.configuration

/**
 * Named Enums used in the application
 */

interface TitledEnums{
    fun titles() : List<TitledEnum>
}

interface TitledEnum{
    val title : String
}

enum class Dropzone(override val title : String) : TitledEnum {
    BLACK_KNIGHTS(title = "Black Knights Parachute Centre"),
    CORNISH("Cornish Parachute Centre"),
    GO_SKYDIVE(title = "GoSkydive"),
    HINTON(title = "Hinton Skydiving Centre"),
    NETHERAVON(title = "The Army Parachute Association, Netheravon"),
    LANGAR(title = "Skydive Langar"),
    HIBALDSTOW(title = "Skydive Hibaldstow"),
    DUNKESWELL(title = "Skydive Dunkeswell"),
    LONDON(title = "London Parachute School"),
    CHATTERIS(title = "North London Skydiving Centre, Chatteris"),
    CARK(title = "North West Parachute Centre"),
    PARAGON(title = "Paragon Skydiving Club"),
    HEADCORN(title = "Skydive Headcorn"),
    SERVICES(title = "Services Parachute Centre"),
    SKYHIGH(title = "Skyhigh Skydiving"),
    SIBSON(title = "UK Parachuting, Sibson"),
    GB(title = "Skydive GB Parachute Club"),
    JERSEY("Skydive Jersey"),
    ST_ANDREWS("Skydive St Andrews"),
    ST_GEORGE(title = "Skydive St Andrews"),
    STRATHALLAN(title = "Skydive Strathallan"),
    SWANSEA("Skydive Swansea"),
    TILSTOCK("The Parachute Centre, Tilstock"),
    WESTON(title = "Skydive Weston"),
    BECCLES("UK Parachuting, Beccles"),
    WILD_GEESE("Wild Geese Parachute Centre")
    ;


    companion object : TitledEnums{
        public override fun titles() : List<TitledEnum>{
            return Dropzone.values().sortedBy { it.title }
        }
    }
}

enum class Licence(override val title : String) : TitledEnum {
    A("A"),
    B("B"),
    C("C"),
    D("D")
    ;

    companion object : TitledEnums{
        public override fun titles() : List<TitledEnum>{
            return Licence.values().sortedBy { it.title }
        }
    }
}
