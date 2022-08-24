package com.skyblu.configuration

enum class Licence(override val title : String) : TitledEnum {
    A("A"),
    B("B"),
    C("C"),
    D("D")
    ;

    companion object : TitledEnums{
        override fun titles() : List<TitledEnum>{
            return Licence.values().sortedBy { it.title }
        }
    }
}
