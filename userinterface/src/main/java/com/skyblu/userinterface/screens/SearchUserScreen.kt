package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skyblu.configuration.Concept
import com.skyblu.configuration.LARGE_PADDING
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.cards.AppJumpCardHeader
import com.skyblu.userinterface.componants.cards.UserCard
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppTextField
import com.skyblu.userinterface.componants.lists.PagingList
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.AppViewModel
import com.skyblu.userinterface.viewmodels.SearchUserViewModel

/**
 * A screen that allows the user to search for other users
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@Composable
@Preview(showBackground = true)
fun SearchScreen(
    navController: NavController = rememberNavController(),
    viewModel : SearchUserViewModel = hiltViewModel(),
    appViewModel : AppViewModel = hiltViewModel()
){

    val state = viewModel.state

    Scaffold(
        topBar = {
            AppTopAppBar(title = "Search Users", navigationIcon = {
                ActionConceptList(menuActions = listOf(ActionConcept(
                    action = {navController.popBackStack()},
                    concept = Concept.Previous
                )))
            })
        }
    ){
        Column(
            Modifier
                .fillMaxSize()
                .padding(LARGE_PADDING)) {
            AppTextField(
                value = state.search.value,
                onValueChanged = {state.search.value = it},
                leadingIcon = Concept.Search.icon,
                placeholder = "Search Users",
                imeAction = ImeAction.Search,
                onIme = {viewModel.search()}
            )

            Spacer(Modifier.height(LARGE_PADDING))
            
            PagingList(
                Heading = { },
                list = state.searchedUsers,
                endReached = state.endReached,
                isLoading = state.isLoading.value,
                loadNextPage = {viewModel.loadNextPage()},
                Content = {
                    UserCard(
                        user = it,
                        isFriend = appViewModel.savedUsers.userMap[appViewModel.state.thisUser.value!!]?.friends?.contains(it.ID)!!,
                        onClick = {navController.navigate(Concept.Profile.route + it.ID)},
                        onAddFriendButtonClicked = {viewModel.addFriend(it.ID)},
                        onRemoveFriendButtonClicked = {viewModel.unfriend(it.ID)},
                    )
                } ,
                swipeState = state.swipeRefreshState.value
            ) {

            }
        }
    }
    


}

