package com.helic.wiggy.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.helic.wiggy.data.models.animals.Animal
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.ui.theme.CardColor
import com.helic.wiggy.ui.theme.TextColor
import com.helic.wiggy.ui.theme.genderRedColor
import com.helic.wiggy.utils.AddOrRemoveAnimalAction

@Composable
fun ItemAnimalCardOfUser(
    animal: Animal,
    context: Context,
    mainViewModel: MainViewModel,
    onItemClicked: (animal: Animal) -> Unit,
    snackbar: (String, SnackbarDuration) -> Unit
) {

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
                    .size(80.dp)
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

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    text = animal.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colors.TextColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.subtitle1
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildString {
                        append(animal.age)
                        append("yrs | ")
                        append(animal.gender)
                    },
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.caption
                )

                Row(verticalAlignment = Alignment.Bottom) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Red
                    )

                    Text(
                        text = animal.location,
                        modifier = Modifier.padding(8.dp, 12.dp, 12.dp, 0.dp),
                        color = MaterialTheme.colors.TextColor,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                DeleteChipView(
                    onDeleteClicked = {

                        // Delete the Animal from firebase
                        mainViewModel.addOrRemoveAnimalFromFirebase(
                            context = context,
                            snackbar = snackbar,
                            animal = animal,
                            action = AddOrRemoveAnimalAction.REMOVE
                        )

                        //Delete the Animal from the user's record
                        mainViewModel.addOrRemoveAnimalForUser(
                            context = context,
                            snackbar = snackbar,
                            animal = animal,
                            action = AddOrRemoveAnimalAction.REMOVE
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun DeleteChipView(onDeleteClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(genderRedColor.copy(.08f))
            .clickable { onDeleteClicked() }
    ) {
        Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = "Delete Icon",
            tint = genderRedColor,
            modifier = Modifier.padding(6.dp)
        )
    }
}