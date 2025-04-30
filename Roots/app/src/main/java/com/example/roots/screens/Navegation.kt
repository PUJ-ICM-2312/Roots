package com.example.roots.screens

import LoginScreen
import SignUpScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationStack() {
    val navController = rememberNavController()

    val screens = listOf(
        Screen.Swipe,
        Screen.AddProperty,
        Screen.Chat,
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
        Screen.FakeMap
    )

    Box {
        NavHost(navController = navController, startDestination = Screen.Welcome.route) {
            screens.forEach { screen ->
                composable(route = screen.route) {
                    when (screen) {
                        is Screen.Swipe -> SwipeROOTS()
                        is Screen.AddProperty -> AddPropertyScreen()
                        is Screen.Chat -> ChatScreen()
                        is Screen.ConfirmSubscription -> ConfirmSubscriptionScreen()
                        is Screen.Messages -> MessagesScreen()
                        is Screen.MyProperties -> MyPropertiesScreen()
                        is Screen.Payment -> PaymentScreen()
                        is Screen.PaymentSuccess -> PaymentSuccessScreen()
                        is Screen.Plans -> PlansScreen()
                        is Screen.PropertyScrollMode -> PropertyScrollModeScreen()
                        is Screen.Settings -> SettingsScreen()
                        is Screen.Filter -> FilterScreen()
                        is Screen.EditProfile -> EditProfileScreen()
                        is Screen.CurrentPlan -> CurrentPlanScreen()
                        is Screen.Login -> LoginScreen()
                        is Screen.Welcome -> WelcomeScreen()
                        is Screen.SignUp -> SignUpScreen()
                        is Screen.FakeMap -> MapScreenPreview()
                        is Screen.MapPreview -> MapScreenPreview()
                    }
                }
            }
        }

        SidebarNavigation(navController, screens)
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
}
