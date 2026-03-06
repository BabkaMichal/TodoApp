package com.example.todoapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoBottomSheet(
    initialType: ItemType,
    onDismiss: () -> Unit,
    onSave: (TodoItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    var selectedDaysFromNow by remember { mutableStateOf(-1) }

    //automatically show keyboard
    val focusRequester = remember { FocusRequester() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    //panel
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 32.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //type
            val typeName = when (initialType) {
                ItemType.DAILY -> "Daily Task (24h)"
                ItemType.TASK -> "Long Term Task"
                ItemType.ROUTINE -> "Routine"
                ItemType.LIST_ITEM -> "List Item"
            }
            Text(
                text = "New $typeName",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp)
            )

            //text
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("What do you want to add?", style = MaterialTheme.typography.titleLarge) },
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (title.isNotBlank()) {
                            val finalDeadline = if (selectedDaysFromNow != -1) getEndOfDayTimestamp(selectedDaysFromNow) else null
                            onSave(TodoItem(title = title, type = initialType, difficulty = selectedDifficulty, deadline = finalDeadline))
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            //show keyboard
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            //difficulty: only tasks
            if (initialType == ItemType.TASK) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Difficulty:", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                    Difficulty.values().forEach { difficulty ->
                        FilterChip(
                            selected = selectedDifficulty == difficulty,
                            onClick = { selectedDifficulty = difficulty },
                            label = { Text(difficulty.name.toCapitalized()) }
                        )
                    }
                }
            }

            //deadline: only for long term tasks
            if (initialType == ItemType.TASK) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Deadline:", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                    FilterChip(selected = selectedDaysFromNow == 0, onClick = { selectedDaysFromNow = 0 }, label = { Text("Today") })
                    FilterChip(selected = selectedDaysFromNow == 1, onClick = { selectedDaysFromNow = 1 }, label = { Text("Tomorrow") })
                    FilterChip(selected = selectedDaysFromNow == 7, onClick = { selectedDaysFromNow = 7 }, label = { Text("7 days") })
                }
            }

            //save button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val finalDeadline = if (selectedDaysFromNow != -1) getEndOfDayTimestamp(selectedDaysFromNow) else null
                        onSave(TodoItem(title = title, type = initialType, difficulty = selectedDifficulty, deadline = finalDeadline))
                    }
                },
                modifier = Modifier.align(Alignment.End),
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }


}