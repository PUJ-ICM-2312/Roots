package com.example.roots.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.roots.model.Plan
import java.text.NumberFormat
import java.util.Locale


@Composable
fun ConfirmSubscriptionScreen(
    navController: NavController,
    planId: String
) {
    val context = LocalContext.current

    // 1) Ejemplo de planes (igual que antes). Solo los usamos para ofrecer detalles.
    //    En producción podrías reemplazar esto por una consulta real a Firestore.
    val planes = listOf(
        Plan(id = "plan1", nombre = "Plan Publicador",  descripcion = "", precioMensual = 9900.0),
        Plan(id = "plan2", nombre = "Plan Pro",         descripcion = "", precioMensual = 19900.0),
        Plan(id = "plan3", nombre = "Plan Premium",     descripcion = "", precioMensual = 29900.0)
    )

    // 2) Buscamos el plan que coincide con planId
    val planSeleccionado = planes.find { it.id == planId }

    // Si el plan no existe, simplemente volvemos atrás
    if (planSeleccionado == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Plan no encontrado.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return
    }

    // Color verde claro uniforme en toda la app
    val verdeClaro = Color(0xFFD5FDE5)

    // Para mostrar precios con separadores de miles (p.ej. "19.900")
    fun formatPrecio(valor: Double): String {
        val nf = NumberFormat.getInstance(Locale("es", "CO"))
        nf.maximumFractionDigits = 0
        return nf.format(valor)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        // ————— Encabezado —————
        Text(
            text = "Confirmar Suscripción",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(16.dp).align(Alignment.CenterHorizontally))

        // ————— Tarjeta de resumen del plan seleccionado —————
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = verdeClaro),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Nombre del plan (por ejemplo: "Plan Pro")
                Text(
                    text = planSeleccionado.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Viñetas con los detalles (los valores de ejemplo están 'hardcodeados'
                // en base a tu captura; puedes adaptarlos si cambian los requerimientos).
                when (planSeleccionado.id) {
                    "plan1" -> {
                        PlanBullet(text = "1 publicación · 30 días")
                        PlanBullet(text = "Sin anuncios")
                        PlanBullet(text = "Estadísticas básicas")
                    }
                    "plan2" -> {
                        PlanBullet(text = "5 publicaciones · 60 días")
                        PlanBullet(text = "Sin anuncios")
                        PlanBullet(text = "Inmuebles destacados")
                        PlanBullet(text = "Estadísticas avanzadas")
                    }
                    "plan3" -> {
                        PlanBullet(text = "Publicaciones ilimitadas · 90 días")
                        PlanBullet(text = "Sin anuncios")
                        PlanBullet(text = "Inmuebles en primer lugar")
                        PlanBullet(text = "Estadísticas avanzadas")
                        PlanBullet(text = "Soporte prioritario")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ————— Subtítulo —————
        Text(
            text = "Selecciona tu plan de pago",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ————— Botones de “1 mes”, “6 meses” y “12 meses” —————
        // Cada botón pinta en verde claro y llama a la misma ruta: "payment/{planId}/{meses}"
        PaymentOptionButton(
            label = "1 mes · \$${formatPrecio(planSeleccionado.precioMensual)}",
            colorFondo = verdeClaro
        ) {
            navController.navigate("payment/${planSeleccionado.id}/1")
        }
        Spacer(modifier = Modifier.height(8.dp))

        PaymentOptionButton(
            label = "6 meses · \$${formatPrecio(planSeleccionado.precioMensual * 6)}",
            colorFondo = verdeClaro
        ) {
            navController.navigate("payment/${planSeleccionado.id}/6")
        }
        Spacer(modifier = Modifier.height(8.dp))

        PaymentOptionButton(
            label = "12 meses · \$${formatPrecio(planSeleccionado.precioMensual * 12)}",
            colorFondo = verdeClaro
        ) {
            navController.navigate("payment/${planSeleccionado.id}/12")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ————— Botón “Cancelar” para volver atrás —————
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

/**
 * Pequeña “viñeta” para mostrar líneas de detalle dentro de la tarjeta verde.
 */
@Composable
private fun PlanBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.padding(end = 6.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

/**
 * Botón de opción de “X meses – $YYYYY” estilizado con fondo verde claro.
 * Recibe un lambda onClick para navegar a la pantalla de pago.
 */
@Composable
private fun PaymentOptionButton(
    label: String,
    colorFondo: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = colorFondo),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}