package com.skyblu.userinterface.componants.lists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun <E> PagingList(
    Heading: @Composable () -> Unit,
    list: List<E>,
    endReached: Boolean,
    isLoading: Boolean,
    loadNextPage: () -> Unit,
    Content: @Composable (E) -> Unit,
    swipeState : SwipeRefreshState,
    refresh : () -> Unit,
) {
    val size = list.size
    SwipeRefresh(
        state = swipeState,
        onRefresh = {refresh()}) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Heading()
            }
            if(list.isNotEmpty()){
                items(list.size) { index ->
                    if(list.isNotEmpty()){
                        val data: E = list[index]
                        if (index >= size - 1 && !endReached && !isLoading) {
                            loadNextPage()
                        }
                        Content(data)
                    }

                }
            }



            item{
                if(isLoading){
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
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