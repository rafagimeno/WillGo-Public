package com.example.willgo.view.sections.WillGo


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SentRequestsItem(
    idEvent: Long,
    nickname: String,
    state: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = nickname,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(text = state,
                color = if(state == "Pendiente") {
                    Color.Black
                }
                else{
                    if(state == "Aceptada")Color.Green else Color.Red
                }
            )
            Box(
                modifier = Modifier
                    .wrapContentSize()
                ,
                contentAlignment = Alignment.Center
            ){
                Icon(imageVector = Icons.Rounded.Close,
                    contentDescription = "Selected",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onClick() },
                )
            }
        }


    }

}

@Preview
@Composable
fun SentRequestsItemPreview() {
    SentRequestsItem(3,"Paco Camarasa", state = "Pendiente", onClick = {})
}