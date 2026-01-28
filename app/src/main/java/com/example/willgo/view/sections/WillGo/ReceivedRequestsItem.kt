package com.example.willgo.view.sections.WillGo

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.willgo.data.Event
import com.example.willgo.data.Request
import com.example.willgo.view.screens.getClient
import com.example.willgo.view.sections.VerticalSeparator
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

@Composable
fun ReceivedRequestsItem(
    idEvent: Long,
    nickname: String,
    state: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    accept: () -> Unit,
    decline: () -> Unit,
) {
    val stateRemember = remember { mutableStateOf(state) }
    val nameEvent = remember{ mutableStateOf("") }
    LaunchedEffect(Unit) {
        nameEvent.value = getNameEvent(idEvent)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .clickable {onClick()}
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
            Text(text = stateRemember.value,
                color = if(stateRemember.value == "Pendiente") {
                    Color.Black
                }
                else{
                    if(stateRemember.value == "Aceptada")Color.Green else Color.Red
                }
            )
            VerticalSeparator()
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Green)
                ,
                contentAlignment = Alignment.Center
            ){
                Icon(imageVector = Icons.Rounded.Check,
                    contentDescription = "Selected",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { accept()
                                   stateRemember.value = "Aceptada"},
                )
            }
            VerticalSeparator()
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Red)
                ,
                contentAlignment = Alignment.Center
            ){
                Icon(imageVector = Icons.Rounded.Close,
                    contentDescription = "Selected",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            decline()
                                   stateRemember.value = "Rechazada"},
                )
            }
        }


    }

}

suspend fun getNameEvent(idEvent: Long): String {
    val response = getClient()
        .postgrest["Evento"]
        .select {
            filter {
                and {
                    eq("id", idEvent)
                }
            }
        }
    val request = response.decodeSingleOrNull<Event>() // Devuelve null si no hay resultados
    return request!!.name_event
}

@Preview
@Composable
fun ReceivedRequestsItemPreview() {
    ReceivedRequestsItem(3,"Paco Camarasa", state = "Pendiente", onClick = {} ,accept = {}, decline = {})
}