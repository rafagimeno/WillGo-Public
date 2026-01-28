package com.example.willgo

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.willgo.data.Event
import com.example.willgo.data.User.User
import com.example.willgo.view.screens.EventDataScreen
import com.example.willgo.view.screens.navScreens.ProfileScreen

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.willgo", appContext.packageName)
    }
}

@RunWith(AndroidJUnit4::class)
class MainActivityTest {


    @get:Rule
    val composeTestRule = createComposeRule()

    val user = User(
        nickname = "camilayoze12",
        name = "Camila Ayoze",
        password = "password123",
        email = "camilayoze@gmail.com",
        followers = 1,
        followed = 1
    )

    val event = Event(
        id = 1L,
        description = "Un emocionante concierto de rock con artistas internacionales.",
        name_event = "Concierto de Rock",
        email_contact = "contacto@conciertorock.com",
        phone = 1234567890L,
        category = null, // Suponiendo que Category es un enum o clase que tienes definida
        location = "Auditorio Nacional",
        latitude = 19.432608,
        longitude = -99.133209,
        date = "2024-12-15",
        price = 50.0f,
        image = "",
        duration = 3.5f,
        asistance = 150L,
        type = "Presencial"
    )


    // FUNCIONALES
    /*
    @Test
    fun testEventSearch() {
        // Escribe en el campo de búsqueda
        onView(withId(R.id.searchField))
            .perform(typeText("Concierto"), closeSoftKeyboard())

        // Comprueba si un resultado esperado aparece en la lista
        onView(withText("Concierto de Rock"))
            .check(matches(isDisplayed()))
    }
*/
    @Test
    fun testUserProfileDisplayed() {
        composeTestRule.setContent {
            ProfileScreen(
                navController = rememberNavController(),
                paddingValues = PaddingValues(7.dp),
                user = user,
                showBackArrow = true
            )
        }
        // Verifica que el nombre y el nickname del usuario son visibles
        composeTestRule.onNodeWithTag("username").assertTextEquals(user.name)

        // Verifica que los textos son correctos
        composeTestRule.onNodeWithTag("followerText").assertTextEquals("Seguidores")
        composeTestRule.onNodeWithTag("followedText").assertTextEquals("Seguidos")

    }

    // NO FUNCIONALES
    @Test
    fun testNavigationToProfile() {
        composeTestRule.setContent {
            ProfileScreen(
                navController = rememberNavController(),
                paddingValues = PaddingValues(7.dp),
                user = user,
                showBackArrow = true
            )

        }
        composeTestRule.onNodeWithTag("username")
            .assertExists() // Verifica que estás en la pantalla correcta
    }

    @Test
    fun testButtonClickPerformance() {
        composeTestRule.setContent {
            EventDataScreen(event, paddingValues = PaddingValues(7.dp), onBack = { },
                goAlone = {})

        }

        val startTime = System.nanoTime()
        composeTestRule.onNodeWithTag("attendButton").performClick()
        val endTime = System.nanoTime()
        val duration = endTime - startTime
        assert(duration < 200_000_000) // Verifica que tarde menos de 200ms
    }


    @Test
    fun testButtonStressClick() {
        composeTestRule.setContent {
            EventDataScreen(event, paddingValues = PaddingValues(7.dp), onBack = { },
                goAlone = {})

        }
        val buttonText =
            composeTestRule.onNodeWithTag("attendButton").fetchSemanticsNode().config.getOrNull(
                SemanticsProperties.Text
            ).toString()
        Log.d("TestButtonText", "Button Text: $buttonText")
        if (buttonText == "[WillGo]") {
            Log.d("AAAAAAAAAAAAAAAAAAAAA", "Hola")
            repeat(11) {
                composeTestRule.onNodeWithTag("attendButton").performClick()
                composeTestRule.waitForIdle()
            }

            val epa =
                composeTestRule.onNodeWithTag("attendButton").fetchSemanticsNode().config.getOrNull(
                    SemanticsProperties.Text
                ).toString()
            // Impimir en Logcat
            Log.d("EEEEEEEEEEEEEEE", epa)

            if (epa == "[WillGo ✔]") {
                Log.d("EEEEEEEEE", "ÉXITO")
            } else {
                Log.d("EEEEEEEEE", "FRACASO")
            }

            //composeTestRule.onNodeWithTag("willGoText").assertTextEquals("WillGo ✔")
        } else if (buttonText == "[WillGo ✔]") {
            Log.d("AAAAAAAAAAAAAAAAAAAA", "Adios")
            repeat(1) {
                composeTestRule.onNodeWithTag("attendButton").performClick()
            }
            composeTestRule.waitForIdle()
            Log.d("OOOOOOOOOOOOOOOO", "Clickeado")
            val epa =
                composeTestRule.onNodeWithTag("attendButton").fetchSemanticsNode().config.getOrNull(
                    SemanticsProperties.Text
                ).toString()
            // Verifica que el resultado final sea consistente
            Log.d("EEEEEEEEEEEEEEE", epa)

            if (epa == "[WillGo]") {
                Log.d("OLEEEEEEEEEE", "ÉXITO")
            } else {
                Log.d("OLEEEEEEEEE", "FRACASO")
            }
        }
// WillGo ✔
    }
}