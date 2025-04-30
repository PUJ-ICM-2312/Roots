package com.example.roots.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roots.R
import androidx.compose.foundation.BorderStroke
import androidx.navigation.NavController



@Composable
fun PaymentScreen(navController: NavController) {
    var selectedCard by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var receivePromos by remember { mutableStateOf(false) }

    val isPayEnabled = selectedCard.isNotEmpty() && acceptedTerms

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Confirmar\nSuscripción",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD5FDE5), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Plan Pro 6 meses:\n$ 109.900", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Elige método de pago", fontSize = 16.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(16.dp))

            PaymentCardOption(
                label = "Mastercard\nxxxx - 0001",
                icon = R.drawable.mastercard,
                selected = selectedCard == "mastercard",
                onSelect = { selectedCard = "mastercard" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentCardOption(
                label = "VISA\nxxxx - 0231",
                icon = R.drawable.visa,
                selected = selectedCard == "visa",
                onSelect = { selectedCard = "visa" }
            )

            Spacer(modifier = Modifier.height(24.dp))

            CheckboxWithLabel(
                text = "Aceptar términos y condiciones*",
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = it }
            )

            CheckboxWithLabel(
                text = "Recibir promociones por correo",
                checked = receivePromos,
                onCheckedChange = { receivePromos = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Acción de pago */ },
                enabled = isPayEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPayEnabled) Color(0xFFB6F2C1) else Color(0xFFE0E0E0),
                    contentColor = Color.Black
                )
            ) {
                Text("Pagar", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PaymentCardOption(label: String, icon: Int, selected: Boolean, onSelect: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFEDEDED),
        border = if (selected) BorderStroke(2.dp, Color.Black) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontSize = 16.sp)
        }
    }
}


@Composable
fun CheckboxWithLabel(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}
