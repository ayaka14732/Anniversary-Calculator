package com.example.anniversarycalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anniversarycalculator.ui.theme.AnniversaryCalculatorTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = AnniversaryRepository(applicationContext)

        setContent {
            AnniversaryCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    AnniversaryScreen(repository = repository)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnniversaryScreen(
    repository: AnniversaryRepository,
    viewModel: AnniversaryViewModel = viewModel(factory = AnniversaryViewModelFactory(repository))
) {
    val anniversaries by viewModel.anniversaries.collectAsState()
    val language by viewModel.language.collectAsState()
    val strings = when (language) {
        "da" -> Strings.da
        "zh" -> Strings.zh
        else -> Strings.en
    }

    var showDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(strings.title) }, actions = {
            IconButton(onClick = { viewModel.toggleLanguage() }) {
                Icon(Icons.Default.Language, contentDescription = "Language")
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = strings.addNew)
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (anniversaries.isEmpty()) {
                EmptyState(strings)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(anniversaries, key = { it.id }) { anniversary ->
                        AnniversaryCard(
                            anniversary = anniversary,
                            viewModel = viewModel,
                            strings = strings,
                            language = language,
                            onDelete = { viewModel.deleteAnniversary(anniversary.id) })
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddAnniversaryDialog(
            title = newTitle,
            date = newDate,
            strings = strings,
            onTitleChange = { newTitle = it },
            onDateClick = { showDatePicker = true },
            onDismiss = {
                showDialog = false
                newTitle = ""
                newDate = null
            },
            onSave = {
                if (newTitle.isNotBlank() && newDate != null) {
                    viewModel.addAnniversary(newTitle, newDate!!)
                    showDialog = false
                    newTitle = ""
                    newDate = null
                }
            })
    }

    if (showDatePicker) {
        DatePickerDialog(initialDate = newDate ?: LocalDate.now(), onDateSelected = {
            newDate = it
            showDatePicker = false
        }, onDismiss = { showDatePicker = false })
    }
}

@Composable
fun EmptyState(strings: Strings) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = strings.empty,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = strings.emptyDesc,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun AnniversaryCard(
    anniversary: Anniversary,
    viewModel: AnniversaryViewModel,
    strings: Strings,
    language: String,
    onDelete: () -> Unit
) {
    val diff = viewModel.calculateDifference(anniversary.date)
    val diffText = formatDifference(diff, strings)
    val dateFormatter = when (language) {
        "da" -> DateTimeFormatter.ofPattern(
            "dd. MMM yyyy", Locale("da", "DK")
        )

        "zh" -> DateTimeFormatter.ofPattern(
            "yyyy 年 MM 月 dd 日", Locale.CHINA
        )

        else -> DateTimeFormatter.ofPattern(
            "MMM dd, yyyy", Locale.ENGLISH
        )
    }


    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = anniversary.title, style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = diffText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = anniversary.date.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun formatDifference(diff: DateDifference, strings: Strings): String {
    if (diff.isToday) return strings.today

    val parts = mutableListOf<String>()
    if (diff.years > 0) parts.add("${diff.years}${strings.years}")
    if (diff.months > 0) parts.add("${diff.months}${strings.months}")
    if (diff.days > 0 || parts.isEmpty()) parts.add("${diff.days}${strings.days}")

    val timeStr = parts.joinToString(" ")
    return if (diff.isPast) "$timeStr${strings.ago}" else "$timeStr${strings.later}"
}

@Composable
fun AddAnniversaryDialog(
    title: String,
    date: LocalDate?,
    strings: Strings,
    onTitleChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(strings.addNew) }, text = {
        Column {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(strings.anniversaryTitle) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onDateClick, modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(date?.toString() ?: strings.selectDate)
            }
        }
    }, confirmButton = {
        TextButton(
            onClick = onSave, enabled = title.isNotBlank() && date != null
        ) {
            Text(strings.save)
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(strings.cancel)
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: LocalDate, onDateSelected: (LocalDate) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toEpochDay() * 24 * 60 * 60 * 1000
    )

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            datePickerState.selectedDateMillis?.let { millis ->
                val days = millis / (24 * 60 * 60 * 1000)
                onDateSelected(LocalDate.ofEpochDay(days))
            }
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}
