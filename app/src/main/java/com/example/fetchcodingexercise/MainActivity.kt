package com.example.fetchcodingexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fetchcodingexercise.ui.theme.FetchCodingExerciseTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var itemGroups by rememberSaveable { mutableStateOf(emptyMap<Int, List<Item>>()) }

            // Retrieve items from the endpoint filtering out items with blank or null names and grouping by listId
            LaunchedEffect(Unit) {
                try {
                    itemGroups = withContext(Dispatchers.IO) {
                        FetchAPI.getItems()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            FetchCodingExerciseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ItemList(Modifier.padding(innerPadding), itemGroups)
                }
            }
        }
    }
}

@Composable
fun ItemList(modifier: Modifier, itemGroups: Map<Int, List<Item>>) {
    val listState = rememberLazyListState()
    val screenBackground = Color(0xFF300D38)
    val accentColor = Color(0xFFFBA919)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.08f)
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Fetch Coding Exercise",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = screenBackground
            )
        }

        if (itemGroups.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(color = accentColor)
            }
        } else {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                itemGroups.toSortedMap().forEach { (listId, items) ->
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(screenBackground.copy(alpha = 0.8f))
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "List ID: $listId",
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 15.dp)
                            )
                        }
                    }

                    items(items.sortedBy { it.id }) { item ->
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(Color.White)
                                    .padding(horizontal = 15.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "ID#: ${item.id}",
                                    modifier = Modifier.weight(1f),
                                    color = screenBackground
                                )
                                Text(
                                    text = "Name: ${item.name.orEmpty()}",
                                    modifier = Modifier.weight(4f),
                                    color = screenBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}