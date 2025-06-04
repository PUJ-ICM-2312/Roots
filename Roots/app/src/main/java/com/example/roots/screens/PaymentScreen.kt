// app/src/main/java/com/example/roots/screens/PaymentScreen.kt
package com.example.roots.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roots.model.Plan
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    planId: String,
    meses: Int
) {
    val context = LocalContext.current

    // 1) Lista “simulada” de planes (puedes reemplazarla por tu consulta real)
    val planes = listOf(
        Plan(id = "plan1", nombre = "Plan Publicador",  descripcion = "", precioMensual = 9900.0),
        Plan(id = "plan2", nombre = "Plan Pro",         descripcion = "", precioMensual = 19900.0),
        Plan(id = "plan3", nombre = "Plan Premium",     descripcion = "", precioMensual = 29900.0)
    )

    // 2) Buscamos el plan seleccionado por su ID
    val planSeleccionado = planes.find { it.id == planId }
    if (planSeleccionado == null) {
        // Si no lo encontramos, avisamos y volvemos atrás
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Plan no encontrado.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return
    }

    // 3) Función auxiliar para formatear números con separador de miles (ej: 109.900)
    fun formatPrecio(valor: Double): String {
        val nf = NumberFormat.getInstance(Locale("es", "CO"))
        nf.maximumFractionDigits = 0
        return nf.format(valor)
    }

    // 4) Estados locales
    var selectedPayment by remember { mutableStateOf<String?>(null) }
    var acceptTerms by remember { mutableStateOf(false) }
    var receivePromos by remember { mutableStateOf(false) }

    // 5) Calculamos el total a pagar
    val totalAPagar = planSeleccionado.precioMensual * meses

    // 6) Colores base (mismo verde claro que usas en tu app)
    val verdeClaro = Color(0xFFD5FDE5)
    val fondoGrisClaro = Color(0xFFF2F2F2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        // —— Título principal ——
        Text(
            text = "Confirmar Suscripción",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(24.dp))

        // —— 6.2 Tarjeta verde con “Plan X meses: $YYY” ——
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = verdeClaro),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "${planSeleccionado.nombre} · $meses meses: \$${formatPrecio(totalAPagar)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // —— 6.3 Subtítulo “Elige método de pago” ——
        Text(
            text = "Elige método de pago",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))

        // —— 6.4 Opción Mastercard ——
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { selectedPayment = "mastercard" },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedPayment == "mastercard") verdeClaro.copy(alpha = 0.6f) else fondoGrisClaro
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Aquí podrías reemplazar con el ícono real de Mastercard
                Icon(
                    imageVector = Icons.Default.CreditCard, // Ícono genérico
                    contentDescription = "Mastercard",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Mastercard", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "xxxx - 0001", fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }

        // —— 6.5 Opción VISA ——
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { selectedPayment = "visa" },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedPayment == "visa") verdeClaro.copy(alpha = 0.6f) else fondoGrisClaro
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Aquí podrías reemplazar con el ícono real de VISA
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = "VISA",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "VISA", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "xxxx - 0231", fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // —— 6.6 Casilla “Aceptar términos y condiciones” ——
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { acceptTerms = !acceptTerms }  // cambiar al tocar toda la fila
        ) {
            Checkbox(
                checked = acceptTerms,
                onCheckedChange = { acceptTerms = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Aceptar términos y condiciones*", fontSize = 14.sp)
        }

        // —— 6.7 Casilla “Recibir promociones por correo” ——
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { receivePromos = !receivePromos }
        ) {
            Checkbox(
                checked = receivePromos,
                onCheckedChange = { receivePromos = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Recibir promociones por correo", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // —— 6.8 Botón “Pagar” ——
        val botonHabilitado = acceptTerms && (selectedPayment != null)

        Button(
            onClick = {
                // Si todo está bien, navegamos a payment_success
                navController.navigate("payment_success")
            },
            enabled = botonHabilitado,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (botonHabilitado) verdeClaro else verdeClaro.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Pagar",
                fontSize = 16.sp,
                color = if (botonHabilitado) Color.Black else Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // —— 6.9 Botón “Cancelar” para volver atrás ——
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBDBDBD), // Gris claro de fondo
                contentColor = Color(0xFF000000)    // Gris oscuro para el texto
            )
        ) {
            Text(
                text = "Cancelar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}
