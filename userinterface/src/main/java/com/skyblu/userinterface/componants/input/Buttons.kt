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
fun errorButtonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.error,
        disabledBackgroundColor = MaterialTheme.colors.background,
        disabledContentColor = MaterialTheme.colors.onBackground,
    )
}


@Preview(showBackground = true)
@Composable
fun AppButtonPreview() {
    AppButton(
        onClick = {},
        text = "Button",
        leadingIcon = R.drawable.blue_plane,
        trailingIcon = R.drawable.blue_plane
    )
}

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

@Composable
fun AppTextButton(
    onClick: () -> Unit,
    text: String = "",
    colors: ButtonColors = buttonColors()
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