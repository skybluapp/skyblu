package com.skyblu.userinterface.componants.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyblu.configuration.Concept
import com.skyblu.configuration.LARGE_PADDING
import com.skyblu.configuration.MEDIUM_PADDING
import com.skyblu.configuration.SMALL_PADDING
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.ActionConcept

@Composable
fun buttonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        disabledBackgroundColor = MaterialTheme.colors.background,
        disabledContentColor = MaterialTheme.colors.onBackground,
    )
}

@Composable
fun textButtonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(
        backgroundColor = Color.Transparent,
        contentColor = MaterialTheme.colors.error,
        disabledBackgroundColor = MaterialTheme.colors.background,
        disabledContentColor = MaterialTheme.colors.onBackground,
    )
}

@Composable
fun errorButtonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.error,
        disabledBackgroundColor = MaterialTheme.colors.background,
        disabledContentColor = MaterialTheme.colors.onBackground,
    )
}



/**
 * A regular button
 * @param onClick A function that is ran when clicked
 * @param text A string to display in the button
 * @param leadingIcon An icon to display before the text
 * @param trailingIcon An icon to display after the text
 * @param colors A set of colors for the button
 */
@Composable
fun AppButton(
    onClick: () -> Unit,
    text: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    colors: ButtonColors = buttonColors()
) {
    Button(
        onClick = { onClick() },
        colors = colors,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(end = SMALL_PADDING)
                )
            }
            Text(text = text)
            if (trailingIcon != null) {
                Icon(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(start = SMALL_PADDING)
                )
            }
        }
    }
}

/**
 * A button that spans the width of the screen
 * @param onClick A function that is ran when clicked
 * @param text A string to display in the button
 * @param leadingIcon An icon to display before the text
 * @param trailingIcon An icon to display after the text
 * @param colors A set of colors for the button
 */
@Composable
fun AppSpanButton(
    onClick: () -> Unit,
    text: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    colors: ButtonColors = buttonColors()
) {
    Button(
        onClick = { onClick() },
        colors = colors,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(end = SMALL_PADDING)
                )
            }
            Text(text = text)
            if (trailingIcon != null) {
                Icon(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(start = SMALL_PADDING)
                )
            }
        }
    }
}

/**
 * Text that can perform an action when clicked
 * @param onClick A function that is ran when clicked
 * @param text A string to display in the button
 * @param colors A set of colors for the button
 */
@Preview
@Composable
fun AppTextButton(
    onClick: () -> Unit = {},
    text: String = "Click Here to perform action",
    colors: ButtonColors = textButtonColors()
) {
    TextButton(
        onClick = { onClick() },
        colors = colors
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Text that can perform an action when clicked
 * @param onClick A function that is ran when clicked
 * @param text A string to display in the button
 * @param colors A set of colors for the button
 */
@Composable
@Preview
fun AppSettingsCategory(
    menuAction: ActionConcept = ActionConcept(
        action = {},
        concept = Concept.LocationTracking
    )
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(MaterialTheme.colors.background)
            .clickable { menuAction.action() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = menuAction.concept.icon),
            contentDescription = menuAction.concept.title,
            Modifier.padding(
                start = LARGE_PADDING,
                end = LARGE_PADDING
            )
        )
        Text(
            text = menuAction.concept.title,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AppSpanButtonPreview() {
    AppButton(
        onClick = {},
        text = "Button",
        leadingIcon = R.drawable.blue_plane,
        trailingIcon = R.drawable.blue_plane
    )
}

@Preview(showBackground = true)
@Composable
fun AppButtonPreview() {
    AppSpanButton(
        onClick = {},
        text = "Button",
        leadingIcon = R.drawable.blue_plane,
        trailingIcon = R.drawable.blue_plane
    )
}