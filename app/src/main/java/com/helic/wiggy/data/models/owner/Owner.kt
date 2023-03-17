package com.helic.wiggy.data.models.owner

import com.helic.wiggy.data.models.animals.Animal

data class Owner(
    var userID: String = "",
    var name: String = "",
    var email: String = "",
    var bio: String = "",
    var image: String = "",
    var listOfAnimals: List<Animal> = listOf(),
)
