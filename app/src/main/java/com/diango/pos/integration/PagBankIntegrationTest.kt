// test/integration/PagBankIntegrationTest.kt
package com.diango.pos.test.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.diango.pos.data.model.Customer
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.data.model.TransactionRequest
import com.diango.pos.integration.PagBankSDKIntegration
import com.diango.pos.utils.NetworkResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PagBankIntegrationTest {
    
    private lateinit var pagBankIntegration: PagBankSDKIntegration
    
    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        pagBankIntegration = PagBankSDKIntegration(context)
    }
    
    @Test
    fun testSDKInitialization() {
        // Given & When
        val initialized = pagBankIntegration.initialize()
        
        // Then
        assertTrue("SDK deveria ser inicializado com sucesso", initialized)
    }
    
    @Test
    fun testCreditCardTransaction() = runBlocking {
        // Given
        val request = TransactionRequest(
            amount = 100.0,
            paymentMethod = PaymentMethod.CREDIT_CARD.value,
            installments = 1,
            description = "Teste de transação",
            customer = Customer("João Silva", "12345678900", null, null)
        )
        
        // When
        val result = pagBankIntegration.processTransaction(request)
        
        // Then
        assertTrue("Transação deveria ser processada", result is NetworkResult.Success)
        if (result is NetworkResult.Success) {
            assertEquals(100.0, result.data?.amount ?: 0.0, 0.01)
            assertEquals(PaymentMethod.CREDIT_CARD, result.data?.paymentMethod)
        }
    }
    
    @Test
    fun testPixTransaction() = runBlocking {
        // Given
        val request = TransactionRequest(
            amount = 50.0,
            paymentMethod = PaymentMethod.PIX.value,
            installments = 1,
            description = "Pagamento PIX teste",
            customer = null
        )
        
        // When
        val result = pagBankIntegration.processTransaction(request)
        
        // Then
        assertTrue("Transação PIX deveria ser processada", result is NetworkResult.Success)
        if (result is NetworkResult.Success) {
            assertEquals(50.0, result.data?.amount ?: 0.0, 0.01)
            assertEquals(PaymentMethod.PIX, result.data?.paymentMethod)
        }
    }
    
    @Test
    fun testInvalidAmount() = runBlocking {
        // Given
        val request = TransactionRequest(
            amount = -10.0, // Valor inválido
            paymentMethod = PaymentMethod.CREDIT_CARD.value,
            installments = 1,
            description = null,
            customer = null
        )
        
        // When
        val result = pagBankIntegration.processTransaction(request)
        
        // Then
        assertTrue("Transação com valor inválido deveria falhar", result is NetworkResult.Error)
    }
}