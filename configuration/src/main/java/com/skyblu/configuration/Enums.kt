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



