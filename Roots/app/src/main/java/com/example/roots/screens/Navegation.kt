package com.example.roots.screens

import LoginScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.navigation.navArgument
import androidx.navigation.NavType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationStack() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val screens = listOf(
        Screen.Swipe,
        Screen.AddProperty,
        Screen.ConfirmSubscription,
        Screen.Messages,
        Screen.MyProperties,
        Screen.Payment,
        Screen.PaymentSuccess,
        Screen.Plans,
        Screen.PropertyScrollMode,
        Screen.Settings,
        Screen.Filter,
        Screen.EditProfile,
        Screen.CurrentPlan,
        Screen.Login,
        Screen.Welcome,
        Screen.SignUp,
        Screen.FakeMap,
        Screen.RealMap,
        Screen.Favorites
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                items(screens) { screen ->
                    Text(
                        text = screen.route.replace("_", " ").replaceFirstChar { it.uppercase() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                scope.launch { drawerState.close() }
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Welcome.route
            ) {
                composable(Screen.Welcome.route) { WelcomeScreen(navController) }
                composable(Screen.Login.route) { LoginScreen(navController) }
                composable(Screen.SignUp.route) { SignUpScreen(navController) }
                composable(Screen.Swipe.route) { SwipeROOTS(navController) }
                composable(Screen.AddProperty.route) { AddPropertyScreen(navController) }
                composable(
                    route = "${Screen.ConfirmSubscription.route}/{planId}",
                    arguments = listOf(
                        navArgument("planId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val planId = backStackEntry.arguments?.getString("planId") ?: ""
                    ConfirmSubscriptionScreen(navController, planId)
                }
                composable(Screen.Messages.route) { MessagesScreen(navController) }
                composable(Screen.MyProperties.route) { MyPropertiesScreen(navController) }
                composable(
                    route = "${Screen.Payment.route}/{planId}/{meses}",
                    arguments = listOf(
                        navArgument("planId") { type = NavType.StringType },
                        navArgument("meses") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val planId = backStackEntry.arguments?.getString("planId") ?: ""
                    val meses = backStackEntry.arguments?.getInt("meses") ?: 1
                    PaymentScreen(navController, planId, meses)
                }
                composable(Screen.PaymentSuccess.route) { PaymentSuccessScreen(navController) }
                composable(Screen.Plans.route) { PlansScreen(navController) }
                composable(
                    route = "${Screen.PropertyScrollMode.route}/{propertyId}",
                    arguments = listOf(
                        navArgument("propertyId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                    PropertyScrollModeScreen(navController = navController, propertyId = propertyId)
                }
                composable(Screen.Settings.route) { SettingsScreen(navController) }
                composable(Screen.Filter.route) { FilterScreen(navController) }
                composable(Screen.EditProfile.route) { EditProfileScreen(navController) }
                composable(Screen.CurrentPlan.route) { CurrentPlanScreen(navController) }
                composable(Screen.FakeMap.route) { MapScreenPreview(navController) }
                composable(Screen.MapPreview.route) { MapScreenPreview(navController) }
                composable(Screen.RealMap.route) { RealMapScreen(navController) }
                composable(Screen.Favorites.route) { FavoritesScreen(navController)}
                composable(
                    route = "${Screen.PropertyScrollMode.route}/{propertyId}",
                    arguments = listOf(
                        navArgument("propertyId") { type = NavType.StringType }
                    )
                ) { backStack ->
                    val propertyId = backStack.arguments?.getString("propertyId") ?: ""
                    PropertyScrollModeScreen(
                        navController = navController,
                        propertyId = propertyId  // ahora es String
                    )

                }
                composable(
                    route = "chat_room/{chatId}/{receptorId}",
                    arguments = listOf(
                        navArgument("chatId") { type = NavType.StringType },
                        navArgument("receptorId") { type = NavType.StringType }
                    )
                ) { backStack ->
                    val chatId = backStack.arguments?.getString("chatId") ?: ""
                    val receptorId = backStack.arguments?.getString("receptorId") ?: ""
                    ChatScreen(
                        navController = navController,
                        chatId = chatId,
                        receptorId = receptorId
                    )
                }
                composable(
                    route = "edit_property/{propertyId}",
                    arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
                ) { backStack ->
                    val propertyId = backStack.arguments?.getString("propertyId") ?: return@composable
                    EditPropertyScreen(navController, propertyId)
                }


            }
        }
    }
}

sealed class Screen(val route: String) {
    object Swipe : Screen("swipe")
    object AddProperty : Screen("add_property")
    object Chat : Screen("chat")
    object ConfirmSubscription : Screen("confirm_subscription")
    object Messages : Screen("messages")
    object MyProperties : Screen("my_properties")
    object Payment : Screen("payment")
    object PaymentSuccess : Screen("payment_success")
    object Plans : Screen("plans")
    object PropertyScrollMode : Screen("property_scroll_mode")
    object Settings : Screen("settings")
    object Filter : Screen("filter")
    object EditProfile : Screen("edit_profile")
    object CurrentPlan : Screen("current_plan")
    object Login : Screen("login")
    object Welcome : Screen("welcome")
    object SignUp : Screen("signup")
    object FakeMap : Screen("fake_map")
    object MapPreview : Screen("map_preview")
    object RealMap : Screen("real_map")
    object Favorites : Screen("favorites")
}
