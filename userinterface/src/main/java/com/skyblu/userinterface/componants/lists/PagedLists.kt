package com.skyblu.userinterface.componants.lists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

/**
 * A list of content that can request additional content when the list has been scrolled to the bottom
 * @param Heading composable content to display at the top of the scrollable ist
 * @param list A list of content to poulate the list
 * @param endReached True if the end of the content has been reached
 * @param isLoading True if a request for more content is in progress
 * @param Content Takes an item in the list and converts it in to a composable function
 * @param swipeState the current state of swipe-to-refresh
 * @param refresh A function to perform when the user swipes to refresh
 */
@Composable
fun <E> PagingList(
    Heading: @Composable () -> Unit,
    list: List<E>,
    endReached: Boolean,
    isLoading: Boolean,
    loadNextPage: () -> Unit,
    Content: @Composable (E) -> Unit,
    swipeState: SwipeRefreshState,
    refresh: () -> Unit,
) {
    val size = list.size
    SwipeRefresh(
        state = swipeState,
        onRefresh = { refresh() }) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Heading()
            }
            if (list.isNotEmpty()) {
                items(list.size) { index ->
                    if (list.isNotEmpty()) {
                        val data: E = list[index]
                        if (index >= size - 1 && !endReached && !isLoading) {
                            loadNextPage()
                        }
                        Content(data)
                    }

                }
            }
            item {
                if (isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator()
                    }

                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}