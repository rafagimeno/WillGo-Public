package com.example.willgo.data

import android.util.Log
import androidx.compose.runtime.MutableState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {
    suspend fun loadEventsFromSupabase(eventsState: MutableState<List<Event>>){

        try{
            val client = getClient()
            val supabaseResponse = client.postgrest["Evento"].select()
            val events = supabaseResponse.decodeList<Event>()
            Log.d("Supabase", "Eventos obtenidos: ${events.size}")
            eventsState.value = events
        } catch (e: Exception) {
            Log.e("Supabase", "Error al obtener eventos: ${e.message}")
        }
    }

    private fun getClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://trpgyhwsghxnaakpoftt.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRycGd5aHdzZ2h4bmFha3BvZnR0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjgwMjgwNDcsImV4cCI6MjA0MzYwNDA0N30.IJthecg-DH9rwOob2XE6ANunb6IskxCbMAacducBVPE"
        ){
            install(Postgrest)
        }
    }

    suspend fun getEventsByCategory(category: String): List<Event> {
        return withContext(Dispatchers.IO) {
            getClient().from("Evento")
                .select(columns = Columns.ALL){
                    filter {
                        eq("category", category)
                    }
                }
                .decodeList<Event>()
        }
    }

    suspend fun getEventByName(name: String): List<Event> {
        return withContext(Dispatchers.IO) {
            getClient().from("Evento")
                .select(columns = Columns.ALL){
                    filter {
                        eq("name", name)
                    }
                }
                .decodeList<Event>()
        }
    }

}