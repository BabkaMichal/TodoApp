package com.example.todoapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel
) {
    val allItems by viewModel.todoItems.collectAsState()

    //tabs & states
    var selectedBottomTab by remember { mutableStateOf(ItemType.TASK) }
    var selectedTaskSubTab by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var openedFolderId by remember { mutableStateOf<Int?>(null) }
    var openedFolderTitle by remember { mutableStateOf("") }

    if (openedFolderId != null) {
        BackHandler {
            openedFolderId = null
        }
    }

    //if on daily we look on top, otherwise bottom
    val targetType = when (selectedBottomTab) {
        ItemType.TASK -> if (selectedTaskSubTab == 0) ItemType.DAILY else ItemType.TASK
        else -> selectedBottomTab
    }

    val filteredItems = if (selectedBottomTab == ItemType.LIST_ITEM) {
        if (openedFolderId == null) {
            // in root folder show only items without parent
            allItems.filter { it.type == ItemType.LIST_ITEM && it.parentId == null }
        } else {
            // show items in opened folder
            allItems.filter { it.parentId == openedFolderId }
        }
    } else {
        // normal filtering
        allItems.filter { it.type == targetType && it.parentId == null }
    }

    Scaffold(
        topBar = {
            Column (
                modifier = Modifier.shadow(elevation = 4.dp)
            ){
                TopAppBar(
                    title = {
                        Text(
                            if (openedFolderId != null) openedFolderTitle
                            else when(selectedBottomTab) {
                                ItemType.ROUTINE -> "Routines"
                                ItemType.TASK -> "My Tasks"
                                ItemType.LIST_ITEM -> "Lists"
                                else -> ""
                            },
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    navigationIcon = {
                        if (openedFolderId != null) {
                            IconButton(onClick = { openedFolderId = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                //top tab
                if (selectedBottomTab == ItemType.TASK) {
                    TabRow(
                        selectedTabIndex = selectedTaskSubTab,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTaskSubTab]),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTaskSubTab == 0,
                            onClick = { selectedTaskSubTab = 0 },
                            text = { Text("Daily (24h)") }
                        )
                        Tab(
                            selected = selectedTaskSubTab == 1,
                            onClick = { selectedTaskSubTab = 1 },
                            text = { Text("Long Term") }
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar (
                containerColor = MaterialTheme.colorScheme.surface
            ){
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    label = { Text("Routine") },
                    selected = selectedBottomTab == ItemType.ROUTINE,
                    onClick = { selectedBottomTab = ItemType.ROUTINE }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Tasks") },
                    selected = selectedBottomTab == ItemType.TASK,
                    onClick = { selectedBottomTab = ItemType.TASK }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Lists") },
                    selected = selectedBottomTab == ItemType.LIST_ITEM,
                    onClick = { selectedBottomTab = ItemType.LIST_ITEM }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(filteredItems) { item ->
                TodoItemCard(
                    item = item,
                    onDelete = { viewModel.deleteItem(item) },
                    onCheckedChange = { isChecked ->
                        val updatedItem = item.copy(isCompleted = isChecked)
                        viewModel.updateItem(updatedItem)
                    },
                    //when we click on list - open it
                    onClick = {
                        if (selectedBottomTab == ItemType.LIST_ITEM && openedFolderId == null) {
                            openedFolderId = item.id
                            openedFolderTitle = item.title
                        }
                    }
                )
            }
        }
    }

    if (showDialog) {
        AddTodoBottomSheet(
            initialType = targetType,
            onDismiss = { showDialog = false },
            onSave = { newItem ->
                val itemToSave = when {
                    newItem.type == ItemType.DAILY -> newItem.copy(deadline = System.currentTimeMillis() + 86400000)
                    openedFolderId != null -> newItem.copy(parentId = openedFolderId, type = ItemType.LIST_ITEM)
                    else -> newItem
                }
                viewModel.addItem(itemToSave)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItemCard(
    item: TodoItem,
    onDelete: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit = {}
) {
    val difficultyColor = getDifficultyColor(item.difficulty)
    val isFolder = item.type == ItemType.LIST_ITEM && item.parentId == null
    //item card
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) Color(0xFFF5F5F5) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT side -> 4/5 of size
            Row(
                modifier = Modifier
                    .weight(4f)
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isFolder) {
                    //checkbox
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = onCheckedChange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                //title
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (item.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                    color = if (item.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                //time
                if ((item.type == ItemType.TASK || item.type == ItemType.DAILY) && !item.isCompleted && item.deadline != null) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ){
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF5D4037),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatDeadline(item.deadline),
                                color = Color(0xFF5D4037),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            )
                        }
                    }

                }
            }

            //RIGHT size -> colored stripe -> 1/5 of size
            if (item.type == ItemType.TASK && !item.isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(difficultyColor)
                )
            } else {
                //if not task or completed -> no stripe & add remove button
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(50.dp)
                        .background(Color(0xFFC7F1CB)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFF5D4037)
                        )
                    }
                }
            }
        }
    }
}