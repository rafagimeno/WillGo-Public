package com.example.willgo.view.screens.navScreens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.dp
import com.example.willgo.data.Event
import com.example.willgo.data.FavoriteEvent
import com.example.willgo.graphs.getFavoriteEventIds
import com.example.willgo.view.screens.getClient
import com.example.willgo.view.sections.CommonEventCard
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CompletableDeferred

@Composable
fun MapScreen(
    eventsState: MutableState<List<Event>>,
    onEventClick: (Event) -> Unit
){

    val coroutineScope = rememberCoroutineScope()
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }
    val events by eventsState
    var favoriteEvents by remember { mutableStateOf<List<Long>>(emptyList()) }

    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2, pageCount = { Int.MAX_VALUE })

    // Número real de eventos
    val realPageCount = events.size

    val cameraPositionState = rememberCameraPositionState()
    LaunchedEffect(Unit) {
        favoriteEvents = getFavoriteEventIds()
        if (events.isNotEmpty() && events[0].latitude != null && events[0].longitude != null) {
            val initialLocation = LatLng(events[0].latitude!!, events[0].longitude!!)
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(initialLocation, 14f))
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (events.isNotEmpty()) {
            val validPageIndex = (pagerState.currentPage % realPageCount).let {
                if (it >= 0) it else it + realPageCount
            }

            val selectedEvent = events[validPageIndex]
            if (selectedEvent.latitude != null && selectedEvent.longitude != null) {
                val selectedLocation = LatLng(selectedEvent.latitude, selectedEvent.longitude)
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f))
            }
        }
    }

    Box(
       Modifier.fillMaxSize()
    )
    {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState
        ){
            events.forEach { event ->
                if (event.latitude != null && event.longitude != null) {
                    val eventLocation = LatLng(event.latitude, event.longitude)
                    val isFavorite = event.id in favoriteEvents

                    Marker(
                        position = eventLocation,
                        title = event.name_event,
                        snippet = "${event.date ?: ""} - ${event.category?.name ?: ""}",
                        icon = if (isFavorite) BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                        onClick = {
                            // pongo accion con el click
                            // por ejemplo, navegar a la pantalla con detalles e evento
                            onEventClick(event)
                            true
                        }
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            contentPadding = PaddingValues(horizontal = 36.dp), // Ajuste del padding para mostrar páginas adyacentes
            pageSpacing = 16.dp, // Espacio entre las páginas
        ) { page ->
            val adjustedPage = page % realPageCount
            val validIndex = if (adjustedPage >= 0) adjustedPage else adjustedPage + realPageCount
            CommonEventCard(event = events[validIndex], modifier = Modifier.fillMaxWidth())
        }
    }

}

