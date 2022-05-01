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


@Composable
fun StyledNumberPicker(
    value : Int,
    onValueChanged : (Int) -> Unit,
    range : IntRange,
    transformation : (Int) -> String = {it.toString()}
){
    NumberPicker(
        value = value,
        range = range,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = LARGE_PADDING),
        label = transformation
    )
}

@Composable
fun StyledTextPicker(
    value : String,
    onValueChanged : (String) -> Unit,
    list : List<String>
){
    ListItemPicker(
        value = value,
        list = list,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = LARGE_PADDING),
    )
}

@Composable
fun <E : TitledEnum, L : TitledEnums>StyledEnumPicker(
    value : E,
    onValueChanged : (E) -> Unit,
    list : L
){
    ListItemPicker<TitledEnum>(
        value = value,
        list = list.titles(),
        onValueChange = {
            onValueChanged(it as E)
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = LARGE_PADDING),
        label = {
            it.title
        }
    )
}



@OptIn(ExperimentalComposeUiApi::class)
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
                    StyledNumberPicker(
                        value = thisVal,
                        onValueChanged = { thisVal = it;},
                        range = range,
                        transformation = transformation
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppTextPicker(
    value: String,
    heading : String = "",
    onValueChanged: (String) -> Unit,
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    list : List<String>,
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
                    StyledTextPicker(
                        value = thisVal,
                        onValueChanged = { thisVal = it; },
                        list = list,
                    )
                }
            }
        )
    }


    Box(){
        TextField(
            value = value,
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
            // Set alpha(0f) to hide click animation
            Box(modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = { open = true }))
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
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
                    StyledEnumPicker(
                        value = thisVal,
                        onValueChanged = { it -> thisVal = it; },
                        list = list,
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

