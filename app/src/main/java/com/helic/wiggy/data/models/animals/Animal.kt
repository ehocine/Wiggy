package com.helic.wiggy.data.models.animals

import com.helic.wiggy.data.models.owner.Owner

data class Animal(
    val name: String = "",
    val age: Double = 0.0,
    val gender: String = "",
    val color: String = "",
    val weight: Double = 0.0,
    val location: String = "",
    var image: String = "",
    val about: String = "",
    val owner: Owner = Owner()
)