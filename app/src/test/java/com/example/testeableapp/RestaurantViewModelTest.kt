package com.example.testeableapp

import com.example.testeableapp.model.MenuData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun startCollectors(
        viewModel: RestaurantViewModel,
        scope: kotlinx.coroutines.CoroutineScope
    ): List<Job> {
        return listOf(
            scope.launch { viewModel.orderedItems.collect {} },
            scope.launch { viewModel.total.collect {} },
            scope.launch { viewModel.isEmpty.collect {} }
        )
    }

    @Test
    fun agregarItemAlPedido_aumentaCantidadAUno() = runTest {
        val viewModel = RestaurantViewModel()

        viewModel.addItem(1)

        assertEquals(1, viewModel.quantities.value[1])
    }

    @Test
    fun incrementarYDecrementarCantidad_actualizaCantidadCorrectamente() = runTest {
        val viewModel = RestaurantViewModel()

        viewModel.addItem(1)
        viewModel.incrementItem(1)

        assertEquals(2, viewModel.quantities.value[1])

        viewModel.decrementItem(1)

        assertEquals(1, viewModel.quantities.value[1])
    }

    @Test
    fun decrementarDesdeUno_eliminaItemDelPedido() = runTest {
        val viewModel = RestaurantViewModel()

        viewModel.addItem(1)
        viewModel.decrementItem(1)

        assertFalse(viewModel.quantities.value.containsKey(1))
    }

    @Test
    fun calcularTotal_validaSumaDePrecioPorCantidad() = runTest {
        val viewModel = RestaurantViewModel()
        val collectors = startCollectors(viewModel, this)

        viewModel.addItem(1)       // Patatas Bravas = 5.50
        viewModel.addItem(2)       // Croquetas = 6.00
        viewModel.incrementItem(1) // Patatas Bravas queda con cantidad 2

        advanceUntilIdle()

        val patatas = MenuData.items.first { it.id == 1 }
        val croquetas = MenuData.items.first { it.id == 2 }

        val totalEsperado = (patatas.price * 2) + croquetas.price

        assertEquals(totalEsperado, viewModel.total.value, 0.001)

        collectors.forEach { it.cancel() }
    }

    // Prueba unitaria adicional 1
    @Test
    fun pedidoVacio_noGeneraConfirmacion() = runTest {
        val viewModel = RestaurantViewModel()

        viewModel.placeOrder()

        assertNull(viewModel.confirmation.value)
    }

    // Prueba unitaria adicional 2
    @Test
    fun aceptarConfirmacion_limpiaPedidoYConfirmacion() = runTest {
        val viewModel = RestaurantViewModel()
        val collectors = startCollectors(viewModel, this)

        viewModel.addItem(1)
        viewModel.addItem(2)

        advanceUntilIdle()

        viewModel.placeOrder()

        assertNotNull(viewModel.confirmation.value)

        viewModel.dismissConfirmation()

        advanceUntilIdle()

        assertTrue(viewModel.quantities.value.isEmpty())
        assertNull(viewModel.confirmation.value)

        collectors.forEach { it.cancel() }
    }
}