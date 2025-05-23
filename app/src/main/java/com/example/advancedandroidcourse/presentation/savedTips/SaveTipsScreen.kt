package com.example.advancedandroidcourse.presentation.savedTips

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.advancedandroidcourse.R
import com.example.advancedandroidcourse.presentation.composables.BottomBar
import com.example.advancedandroidcourse.presentation.composables.PostItem
import com.example.advancedandroidcourse.presentation.notifications.NotificationViewModel
import com.example.advancedandroidcourse.data.model.PostDetails
import kotlinx.coroutines.launch

@Composable
fun SaveTipsScreen(
    navController: NavHostController,
    viewModel: SaveTipsViewModel = hiltViewModel()
) {
    val searchQuery by remember { mutableStateOf("") }

    val filteredTips = viewModel.savedPosts.filter {
        it.post.title.contains(searchQuery, ignoreCase = true)
    }

    //State to track which tip to confirm Delete
    var tipToDelete by remember { mutableStateOf<PostDetails?>(null) }

    //Create the snackbar state and scope
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val hasUnreadNotifications by notificationViewModel.hasUnreadNotifications.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomBar(navController = navController, hasUnreadNotifications)
        }
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
                .padding(innerPadding)
        ) {
            //Adding Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Easy Finn Logo",
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .size(52.dp)
            )

            //Heading
            Text(
                text = "Saved Tips",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)

            )

            //Use LazyVerticalGrid for a grid layout with 2 columns
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTips) { postDetails ->
                    Box {
                        PostItem(
                            postDetails = postDetails,
                            showAuthorInfo = true,
                            onToggleFavorited = {},
                            navController = navController,
                        )

                        //Remove button on Top Right
                        IconButton(
                            onClick = {
                                tipToDelete = postDetails
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                                .size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Saved Tip",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                }
            }
        }

        //Confirmation Dialog
        tipToDelete?.let { post ->
            AlertDialog(
                onDismissRequest = { tipToDelete = null },
                title = { Text("Unsave?") },
                text = { Text("Unsaving a tip will remove it from your saved tips collection.") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.removeSavedTip(post)
                        tipToDelete = null

                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Tip Unsaved",
                                actionLabel = "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.undoRemoveTip()
                            }
                        }
                    }) {
                        Text("REMOVE")
                    }


                },
                dismissButton = {
                    Button(onClick = { tipToDelete = null }) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}