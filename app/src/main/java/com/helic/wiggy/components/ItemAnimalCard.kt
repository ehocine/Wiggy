package com.helic.wiggy.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.helic.wiggy.data.models.animals.Animal
import com.helic.wiggy.data.models.owner.Owner
import com.helic.wiggy.ui.theme.CardColor
import com.helic.wiggy.ui.theme.TextColor

@Composable
fun ItemAnimalCard(animal: Animal, onItemClicked: (animal: Animal) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = { onItemClicked(animal) }),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.CardColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .size(90.dp)
                    .weight(4f)
                    .clip(RoundedCornerShape(16.dp)),
                alignment = Alignment.CenterStart,
                model = ImageRequest.Builder(LocalContext.current)
                    .data(animal.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "Animal Image"
            ) {
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                    }
                } else {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier.clip(RectangleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(8f)
            ) {
                Text(
                    text = animal.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colors.TextColor,
                    fontWeight = FontWeight.Bold,
                    style = typography.subtitle1
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildString {
                        append(animal.age)
                        append(" yrs")
                    },
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colors.TextColor,
                    style = typography.caption
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Red
                    )

                    Text(
                        text = animal.location,
                        modifier = Modifier.padding(8.dp, 0.dp, 12.dp, 0.dp),
                        color = MaterialTheme.colors.TextColor,
                        style = typography.caption,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f),
                horizontalArrangement = Arrangement.End
            ) {
                GenderTag(animal.gender)
            }
        }
    }
}