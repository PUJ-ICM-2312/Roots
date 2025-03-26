package com.example.roots.screens

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
        Screen.PropertyScrollMode
    )

    Box {
        // Definimos la navegación principal
        NavHost(navController = navController, startDestination = Screen.Swipe.route) {
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
                    }
                }
            }
        }

        // Llamada al componente SidebarNavigation
        SidebarNavigation(navController, screens)
    }
}

sealed class Screen(val route: String) {
    object Swipe: Screen("swipe")
    object AddProperty: Screen("add_property")
    object Chat: Screen("chat")
    object ConfirmSubscription: Screen("confirm_subscription")
    object Messages: Screen("messages")
    object MyProperties: Screen("my_properties")
    object Payment: Screen("payment")
    object PaymentSuccess: Screen("payment_success")
    object Plans: Screen("plans")
    object PropertyScrollMode: Screen("property_scroll_mode")
}
