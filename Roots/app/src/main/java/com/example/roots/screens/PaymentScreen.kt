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
import com.example.roots.model.Usuario
import com.example.roots.repository.UsuarioRepository
import com.example.roots.service.LoginService
import com.example.roots.service.UsuarioService
import kotlinx.coroutines.launch
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
        Plan(id = "plan1", nombre = "Plan Publicador", descripcion = "", precioMensual = 9900.0),
        Plan(id = "plan2", nombre = "Plan Pro",        descripcion = "", precioMensual = 19900.0),
        Plan(id = "plan3", nombre = "Plan Premium",    descripcion = "", precioMensual = 29900.0)
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

    // 3) Función auxiliar para formatear números con separador de miles (ej: 10.900)
    fun formatPrecio(valor: Double): String {
        val nf = NumberFormat.getInstance(Locale("es", "CO"))
        nf.maximumFractionDigits = 0
        return nf.format(valor)
    }

    // 4) Estados locales
    var selectedPayment by remember { mutableStateOf<String?>(null) }
    var acceptTerms by remember { mutableStateOf(false) }
    var receivePromos by remember { mutableStateOf(false) }
    var isPaying by remember { mutableStateOf(false) }

    // 5) Calculamos el total a pagar
    val totalAPagar = planSeleccionado.precioMensual * meses

    // 6) Colores base (mismo verde claro que usas en tu app)
    val verdeClaro = Color(0xFFD5FDE5)
    val fondoGrisClaro = Color(0xFFF2F2F2)

    // 7) Servicio de usuario para actualizar Firestore
    val usuarioService = remember { UsuarioService(UsuarioRepository()) }
    val scope = rememberCoroutineScope()

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

        // —— Tarjeta verde con “Plan X meses: $YYY” ——
        Card(
            modifier = Modifier.fillMaxWidth(),
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

        // —— Subtítulo “Elige método de pago” ——
        Text(
            text = "Elige método de pago",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))

        // —— Opción Mastercard ——
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { selectedPayment = "mastercard" },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedPayment == "mastercard")
                    verdeClaro.copy(alpha = 0.6f) else fondoGrisClaro
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
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = "Mastercard",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Mastercard", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "xxxx - 0001", fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }

        // —— Opción VISA ——
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { selectedPayment = "visa" },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedPayment == "visa")
                    verdeClaro.copy(alpha = 0.6f) else fondoGrisClaro
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
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = "VISA",
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

        // —— Casilla “Aceptar términos y condiciones” ——
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { acceptTerms = !acceptTerms }
        ) {
            Checkbox(
                checked = acceptTerms,
                onCheckedChange = { acceptTerms = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Aceptar términos y condiciones*", fontSize = 14.sp)
        }

        // —— Casilla “Recibir promociones por correo” ——
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

        // —— Botón “Pagar” ——
        val botonHabilitado = acceptTerms && (selectedPayment != null) && !isPaying

        Button(
            onClick = {
                //  Si no estamos ya en proceso de pago, iniciamos la rutina
                if (!isPaying && botonHabilitado) {
                    isPaying = true
                    scope.launch {
                        // 1) Validar usuario autenticado
                        val firebaseUser = LoginService.getCurrentUser()
                        if (firebaseUser == null) {
                            Toast.makeText(context, "No está autenticado", Toast.LENGTH_SHORT).show()
                            isPaying = false
                            return@launch
                        }

                        // 2) Obtener datos actuales del usuario desde Firestore
                        usuarioService.obtener(firebaseUser.uid) { usuario ->
                            if (usuario != null) {
                                // 3) Calcular nueva fecha de expiración (hoy + meses * 30 días en ms)
                                val msPorMes = 30L * 24L * 60L * 60L * 1000L
                                val ahora = System.currentTimeMillis()
                                val fechaExp = ahora + meses * msPorMes

                                // 4) Construir el Usuario actualizado (manteniendo campos existentes)
                                val updatedUser = Usuario(
                                    id = usuario.id,
                                    nombres = usuario.nombres,
                                    apellidos = usuario.apellidos,
                                    correo = usuario.correo,
                                    fotoPath = usuario.fotoPath,
                                    celular = usuario.celular,
                                    cedula = usuario.cedula,
                                    tienePlan = true,
                                    planId = planSeleccionado.id,
                                    fechaExpiracion = fechaExp
                                )

                                // 5) Guardar el cambio en Firestore
                                usuarioService.actualizar(updatedUser) { success ->
                                    isPaying = false
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            "Pago exitoso. ¡Ya puedes publicar!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // 6) Navegar a pantalla de éxito
                                        navController.navigate("payment_success")
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error al procesar pago. Intenta nuevamente.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                                isPaying = false
                            }
                        }
                    }
                }
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
            if (isPaying) {
                // Muestra un spinner pequeño mientras se procesa
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Procesando…", fontSize = 16.sp, color = Color.Gray)
            } else {
                Text(
                    text = "Pagar",
                    fontSize = 16.sp,
                    color = if (botonHabilitado) Color.Black else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // —— Botón “Cancelar” para volver atrás ——
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBDBDBD), // Gris claro
                contentColor = Color(0xFF000000)    // Texto negro
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