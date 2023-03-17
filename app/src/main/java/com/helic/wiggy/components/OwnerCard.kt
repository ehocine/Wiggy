package com.helic.wiggy.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.helic.wiggy.R
import com.helic.wiggy.data.models.animals.Animal
import com.helic.wiggy.data.models.owner.Owner
import com.helic.wiggy.ui.theme.TextColor
import com.helic.wiggy.ui.theme.blue
import com.helic.wiggy.utils.sendEmailToOwner


@Composable
fun OwnerCard(
    context: Context,
    owner: Owner,
    animal: Animal,
    snackbar: (String, SnackbarDuration) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        SubcomposeAsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .data(owner.image)
                .crossfade(true)
                .error(R.drawable.account)
                .build(),
            contentDescription = "User Image"
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

        Column(modifier = Modifier) {
            Text(
                text = owner.name,
                color = MaterialTheme.colors.TextColor,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = owner.bio,
                color = MaterialTheme.colors.TextColor,
                style = MaterialTheme.typography.caption,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            FloatingActionButton(
                modifier = Modifier.size(40.dp),
                onClick = {
                    sendEmailToOwner(
                        context = context,
                        emailAddress = owner.email,
                        subject = "Requesting info about ${animal.name}",
                        message = "Hey ${owner.name}, I would like to request more info about ${animal.name}.",
                        snackbar = snackbar
                    )
                },
                backgroundColor = blue
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.Message,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
    }
}