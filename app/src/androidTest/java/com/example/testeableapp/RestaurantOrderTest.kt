package com.example.testeableapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.testeableapp.model.MenuData
import org.junit.Rule
import org.junit.Test

class RestaurantOrderTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun priceText(value: Double): String {
        return "%.2f €".format(value)
    }

    @Test
    fun mensajePedidoVacio_visibleAlInicio() {
        composeTestRule
            .onNodeWithTag("emptyOrderMessage")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun todosLosItemsDelMenu_visibles() {
        MenuData.items.forEach { item ->
            composeTestRule
                .onNodeWithTag("menuItem_${item.id}")
                .performScrollTo()
                .assertIsDisplayed()
        }
    }

    @Test
    fun totalGeneral_seActualizaAlAgregarItems() {
        composeTestRule
            .onNodeWithTag("addButton_1")
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithTag("totalValue")
            .assertTextEquals(priceText(5.50))

        composeTestRule
            .onNodeWithTag("addButton_2")
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithTag("totalValue")
            .assertTextEquals(priceText(11.50))
    }

    // Prueba de UI adicional 1
    @Test
    fun alAgregarProducto_apareceEnElPedidoConCantidadUno() {
        composeTestRule
            .onNodeWithTag("addButton_1")
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithTag("orderItem_1")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("orderItemQuantity_1")
            .assertTextEquals("1")
    }

    // Prueba de UI adicional 2
    @Test
    fun realizarPedido_muestraDialogoDeConfirmacion() {
        composeTestRule
            .onNodeWithTag("addButton_1")
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithTag("placeOrderButton")
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithTag("confirmationDialog")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("confirmationTitle")
            .assertTextEquals("Pedido Confirmado")
    }
}