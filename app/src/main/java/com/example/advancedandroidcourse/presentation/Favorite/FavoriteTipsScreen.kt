package com.example.advancedandroidcourse.presentation.Favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.advancedandroidcourse.R
import com.example.advancedandroidcourse.presentation.composables.BottomBar
import com.example.advancedandroidcourse.presentation.composables.SearchBar
import com.example.advancedandroidcourse.presentation.composables.TipCard
import com.example.advancedandroidcourse.presentation.notifications.NotificationViewModel

@Composable
fun FavoriteTipsScreen(
    navController: NavHostController,
    viewModel: FavoriteTipsViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredTips = viewModel.tipList.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val hasUnreadNotifications by notificationViewModel.hasUnreadNotifications.collectAsState()


    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            iconRes = R.drawable.search,
            value = searchQuery,
            onValueChange = { searchQuery = it },
            onSearchClick = {}
        )

        LazyColumn (
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            items(filteredTips) { post ->
                TipCard(post = post) {
                    navController.navigate("postDetails/${post.id}")
                }
            }

        }

        BottomBar(navController = navController, hasUnreadNotifications)
    }
}