package com.skyblu.userinterface.componants.alerts

import androidx.compose.foundation.background
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.skyblu.userinterface.componants.input.AppTextButton
import com.skyblu.userinterface.componants.input.errorButtonColors

/**
 * A dialog box that can be confirmed or dismissed
 * @param show True if the dialog is being displayed
 * @param dismiss An action to take if the dialog is dismissed
 * @param title The text to display in the dialog box
 * @param confirm An action to take if the dialog is confirmed
 * @param buttonText Text to be displayed in the confirm button
 */
@Composable
fun AppDialog(
    show: Boolean,
    dismiss: () -> Unit,
    title: String,
    text: String,
    confirm: () -> Unit,
    buttonText: String
) {
    if (show)
        AlertDialog(
            onDismissRequest = {
                dismiss()
            },
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = text)
            },
            confirmButton = {
                AppTextButton(
                    onClick = { confirm() },
                    text = buttonText,
                    colors = errorButtonColors()
                )
            },
            modifier = Modifier.background(MaterialTheme.colors.background),
            backgroundColor = MaterialTheme.colors.background
        )
}