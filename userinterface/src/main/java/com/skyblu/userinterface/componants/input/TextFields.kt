package com.skyblu.userinterface.componants.input

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyblu.userinterface.R



/**
 * The default textfield composable used by the app
 * @param value The Sting being displayed
 * @param onValueChanged An action to perform when the value is changed
 * @param onIme A action to perform when the IME button is pressed
 * @param placeholder Text to display when the text box is empty
 * @param leadingIcon An icon to display before the text
 * @param trailingIcon An icon to display after the text
 * @param maxLines The maximum lines allowed in the textbox
 * @param singleLine True if text should only fill a single line
 * @param keyboardType The keyboard to use for the text box
 * @param imeAction The IME action to display
 * @param error True is the entry is invalid
 */
@Composable
fun AppTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    onIme: () -> Unit = {},
    placeholder: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    error: Boolean = false
) {
    val colors = MaterialTheme.colors
    val keyboardOptions = KeyboardOptions(
        keyboardType = keyboardType,
        imeAction = imeAction,
    )
    val keyboardActions = KeyboardActions(
        onNext = { onIme() },
        onGo = { onIme() },
        onSearch = { onIme() },
        onDone = { onIme() }
    )

    TextField(
        value = value,
        onValueChange = { onValueChanged(it) },
        colors = textFieldColors(),
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = placeholder,
                    tint = colors.onBackground
                )
            }
        },
        trailingIcon = {
            if (trailingIcon != null) {
                Icon(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = placeholder,
                    tint = colors.onBackground
                )
            }
        },
        placeholder = { Text(text = placeholder) },
        maxLines = maxLines,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                2.dp,
                if(error){MaterialTheme.colors.error} else {Color.Transparent},
                RoundedCornerShape(10.dp)
            )
            .heightIn(
                AppTextFieldDefaults.height,
                AppTextFieldDefaults.height
            )
            .clip(RoundedCornerShape(10.dp))
            .onGloballyPositioned { },

        keyboardActions = keyboardActions,
        visualTransformation = if (keyboardType == KeyboardType.Password) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        label = { Text(text = placeholder) },
    )
}

@Preview(showBackground = true)
@Composable
fun AppTextFieldPreview() {
    var value by remember { mutableStateOf("") }
    AppTextField(
        value = value,
        onValueChanged = { value = it },
        placeholder = "TextField",
        leadingIcon = R.drawable.blue_plane,
        trailingIcon = R.drawable.blue_plane
    )
}

object AppTextFieldDefaults {
    val borderWidth = 2.dp
    val height = TextFieldDefaults.MinHeight
}




@Composable
fun textFieldColors(): TextFieldColors {
    return TextFieldDefaults.textFieldColors(
        textColor = MaterialTheme.colors.onBackground,
        disabledTextColor = Color.Black,
        backgroundColor = MaterialTheme.colors.surface,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        placeholderColor = MaterialTheme.colors.onBackground,
        cursorColor = MaterialTheme.colors.onBackground,
        focusedLabelColor = MaterialTheme.colors.onBackground
    )
}