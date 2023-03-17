package com.helic.wiggy.navigation

sealed class Screens(val route: String) {
    object Login : Screens(route = "login")
    object Register : Screens(route = "register")
    object ForgetPassword : Screens(route = "forget_password")
    object Home : Screens(route = "home")
    object Details : Screens(route = "details")
    object AddAnimal : Screens(route = "add_animal")
    object Profile : Screens(route = "profile")
    object EditProfileDetails : Screens(route = "edit_profile_details")
    object PrivacyPolicy : Screens(route = "privacy_policy")
}