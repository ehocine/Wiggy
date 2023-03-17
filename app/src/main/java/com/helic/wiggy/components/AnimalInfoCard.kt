package com.helic.wiggy.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.helic.wiggy.ui.theme.TextColor


@Composable
fun AnimalInfoCard(name: String, gender: String, location: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            Text(
                text = name,
                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                color = MaterialTheme.colors.TextColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Red
                )

                Text(
                    text = location,
                    modifier = Modifier.padding(8.dp, 12.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.caption
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            GenderTag(gender)
        }
    }
}