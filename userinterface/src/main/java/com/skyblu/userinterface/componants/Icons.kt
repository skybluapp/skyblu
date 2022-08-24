package com.skyblu.userinterface.componants

import androidx.annotation.DrawableRes
import androidx.navigation.NavController
import com.skyblu.configuration.Concept
import com.skyblu.userinterface.R

/**
 * Connects a concept to an action
 * @param concept a related icon, title and optional route
 * @param action a action connected to the concept
 * @see Concept
 */
data class ActionConcept(
    val concept: Concept,
    val action: () -> Unit,
    )
