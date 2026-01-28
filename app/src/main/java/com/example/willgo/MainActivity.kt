package com.example.willgo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.willgo.data.Event
import com.example.willgo.data.User.User
import com.example.willgo.graphs.RootNavigationGraph
import com.example.willgo.ui.theme.WillGoTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
        enableEdgeToEdge()
        setContent {
            WillGoTheme {

               Surface(
                   modifier = Modifier.fillMaxSize(),
                   color = MaterialTheme.colorScheme.background
               ) {
                   RootNavigationGraph()
               }
            }
        }
    }

    fun getWillGo(event: Event){
        lifecycleScope.launch{
            val client = getClient()
            val supabaseResponse = client.postgrest["WillGo"].select(){
                filter{
                    eq("event_id", event.id)
                }
            }
            val data = supabaseResponse.decodeList<User>()
            Log.e("supabase", data.toString())
        }
    }

    fun addWillGo(event: Event){
        lifecycleScope.launch {
            val user = getUser()
            val client = getClient()
            val supabaseResponse = client.postgrest["WillGo"].insert(user)
            val data = supabaseResponse.decodeList<User>()
            Log.e("supabase", data.toString())
        }
    }

    private suspend fun getUser(): User {
        val client = getClient()
        val supabaseResponse = client.postgrest["Usuario"].select()
        val data = supabaseResponse.decodeList<User>()
        Log.e("supabase", data.toString())
        return data[0]
    }


    private fun getData(){
        lifecycleScope.launch{
            val client = getClient()
            val supabaseResponse = client.postgrest["Usuario"].select()
            val data = supabaseResponse.decodeList<User>()
            Log.e("supabase", data.toString())
        }
    }

    private fun getClient(): SupabaseClient{
        return createSupabaseClient(
            supabaseUrl = "https://trpgyhwsghxnaakpoftt.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRycGd5aHdzZ2h4bmFha3BvZnR0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjgwMjgwNDcsImV4cCI6MjA0MzYwNDA0N30.IJthecg-DH9rwOob2XE6ANunb6IskxCbMAacducBVPE"
        ){
            install(Postgrest)
        }
    }

}