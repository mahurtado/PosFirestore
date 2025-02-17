package com.example.pos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.Locale

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    NavigationBar {
        // looping over each tab to generate the views and navigation for each item
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title
                    )
                },
                label = {Text(tabBarItem.title)})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
) {
    Box() {
        Icon(
            imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
            contentDescription = title
        )
    }
}

@Composable
fun SimpleOutlinedTextField(
    value: String,
    label: String,
    onChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (!it.contains("\n"))
                onChange(it) },
        maxLines = 1,
        enabled = enabled,
        label = { Text(label) }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerRangeComponent(
    historyViewModel: HistoryViewModel) {
    val datePickerFromState by remember { mutableStateOf( DatePickerState(
        initialSelectedDateMillis = historyViewModel.fromDateValue,
        locale = Locale.getDefault(),
        yearRange = 2020..2025
    )) }
    val datePickerToState by remember { mutableStateOf( DatePickerState(
        initialSelectedDateMillis = historyViewModel.toDateValue,
        locale = Locale.getDefault(),
        yearRange = 2020..2025
    )) }

    var textFrom  by remember { mutableStateOf(DateUtils().dateToString(DateUtils().convertMillisToLocalDate(datePickerFromState.selectedDateMillis!!)))}
    var textTo  by remember { mutableStateOf(DateUtils().dateToString(DateUtils().convertMillisToLocalDate(datePickerToState.selectedDateMillis!!)))}

    var showDateFromPicker by remember { mutableStateOf(false) }
    var showDateToPicker by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        // FROM
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "From")
            Button(
                onClick = {
                    showDateFromPicker = true
                },
            ) {
                Text(text = textFrom)
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
        // TO
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "To")
            Button(
                onClick = {
                    showDateToPicker = true
                },
            ) {
                Text(text = textTo)
            }
        }
    }

    // FROM
    if (showDateFromPicker) {
        DatePickerDialog(
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = { //() -> onChange(it) }
                        textFrom = DateUtils().dateToString(DateUtils().convertMillisToLocalDate(datePickerFromState.selectedDateMillis!!))
                        showDateFromPicker = false
                        historyViewModel.setFromDate(datePickerFromState.selectedDateMillis!!)
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton  (
                    onClick = {
                        showDateFromPicker = false
                    }
                ) { Text("Cancel") }
            }
        )
        {
            DatePicker(state = datePickerFromState)
        }
    }

    // TO
    if (showDateToPicker) {
        DatePickerDialog(
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = {
                        textTo = DateUtils().dateToString(DateUtils().convertMillisToLocalDate(datePickerToState.selectedDateMillis!!))
                        showDateToPicker = false
                        historyViewModel.setToDate(datePickerToState.selectedDateMillis!!)
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton  (
                    onClick = {
                        showDateToPicker = false
                    }
                ) { Text("Cancel") }
            }
        )
        {
            DatePicker(state = datePickerToState)
        }
    }

}