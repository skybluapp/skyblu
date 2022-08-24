package com.skyblu.userinterface.componants.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chargemap.compose.numberpicker.*
import com.skyblu.configuration.Dropzone
import com.skyblu.configuration.LARGE_PADDING
import com.skyblu.configuration.TitledEnum
import com.skyblu.configuration.TitledEnums
import timber.log.Timber

/**
 * A composable interface for the user to pick from a range of numbers
 * @param value The value currently being displayed
 * @param heading A Title for the selection screen
 * @param onValueChanged A function to call when the value is changed
 * @param leadingIcon An icon to display before the selected value
 * @param trailingIcon An icon to display after the selected value
 * @param transformation Transforms the selection in to a string
 */
@Composable
fun AppNumberPicker(
    value: Int,
    heading : String = "",
    onValueChanged: (Int) -> Unit,
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    range : IntRange,
    transformation : (Int) -> String,
) {
    var open by remember {
        mutableStateOf(false)
    }
    var thisVal by remember {
        mutableStateOf(value = value)
    }
    val enabled by remember { mutableStateOf(true)}
    val colors = MaterialTheme.colors


    if(open){
        AlertDialog(
            onDismissRequest = {open = false},
            confirmButton = {

                    AppTextButton(onClick = { open = false; onValueChanged(thisVal) }, text = "Confirm")
            },
            text = {

                Column(verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.height(250.dp)) {
                    Text(heading, style = MaterialTheme.typography.h6)
                    NumberPicker(
                        value = thisVal,
                        onValueChange = { thisVal = it;},
                        range = range,
                        label = transformation
                    )
                }
            }
        )
    }


    Box(){
        TextField(
            value = transformation(value),
            onValueChange = {},
            colors = textFieldColors(),
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        painter = painterResource(id = leadingIcon),
                        contentDescription = heading,
                        tint = colors.onBackground
                    )
                }
            },
            trailingIcon = {
                if (trailingIcon != null) {
                    Icon(
                        painter = painterResource(id = trailingIcon),
                        contentDescription = heading,
                        tint = colors.onBackground
                    )
                }
            },
            placeholder = { Text(text = heading) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    AppTextFieldDefaults.height,
                    AppTextFieldDefaults.height
                )
                .clip(RoundedCornerShape(10.dp))
        )
        if (enabled) {
            Box(modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = { open = true }))
        }
    }
}


/**
 * A composable interface for the user to pick from a range of enums
 * @param value The value currently being displayed
 * @param heading A Title for the selection screen
 * @param onValueChanged A function to call when the value is changed
 * @param leadingIcon An icon to display before the selected value
 * @param trailingIcon An icon to display after the selected value
 * @param list The list of available enums
 */
@Composable
fun <E : TitledEnum, L : TitledEnums>EnumTextPicker(
    value: E,
    heading : String = "",
    onValueChanged: (E) -> Unit,
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    list : L,
) {
    var open by remember {
        mutableStateOf(false)
    }
    var thisVal : E by remember {
        mutableStateOf(value = value)
    }
    val enabled by remember { mutableStateOf(true)}
    val colors = MaterialTheme.colors


    if(open){
        AlertDialog(
            onDismissRequest = {open = false},
            confirmButton = {

                AppTextButton(onClick = { open = false; onValueChanged(thisVal) }, text = "Confirm")
            },
            text = {

                Column(verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.height(250.dp)) {
                    Text(heading, style = MaterialTheme.typography.h6)
                    ListItemPicker(
                        value = thisVal,
                        onValueChange = { it -> thisVal = it as E; },
                        list = list.titles(),
                        label ={it.title},
                        modifier = Modifier.fillMaxWidth().padding(vertical = LARGE_PADDING)
                    )
                }
            }
        )
    }


    Box(){
        TextField(
            value = value.title,
            onValueChange = {},
            colors = textFieldColors(),
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        painter = painterResource(id = leadingIcon),
                        contentDescription = heading,
                        tint = colors.onBackground
                    )
                }
            },
            trailingIcon = {
                if (trailingIcon != null) {
                    Icon(
                        painter = painterResource(id = trailingIcon),
                        contentDescription = heading,
                        tint = colors.onBackground
                    )
                }
            },
            placeholder = { Text(text = heading) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    AppTextFieldDefaults.height,
                    AppTextFieldDefaults.height
                )
                .clip(RoundedCornerShape(10.dp))
        )
        if (enabled) {
            Box(modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = { open = true }))
        }
    }
}

