package com.helic.wiggy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.helic.wiggy.ui.theme.genderBlueColor
import com.helic.wiggy.ui.theme.genderRedColor

@Composable
fun GenderTag(name: String) {
    val color = if (name == "Male") genderBlueColor else genderRedColor
    ChipView(gender = name, color = color)
}

@Composable
fun ChipView(gender: String, color: Color) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(.08f))
    ) {
        Text(
            text = gender, modifier = Modifier.padding(12.dp, 6.dp, 12.dp, 6.dp),
            style = MaterialTheme.typography.caption,
            color = color
        )
    }
}