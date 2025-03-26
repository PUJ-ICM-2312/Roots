package com.example.rootsmckp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthForm(
    fields: List<AuthField>,
    buttonText: String,
    onButtonClick: () -> Unit,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        fields.forEach { field ->
            OutlinedTextField(
                value = field.value,
                onValueChange = field.onChange,
                label = { Text(field.label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(50),
                visualTransformation = if (field.isPassword) PasswordVisualTransformation() else VisualTransformation.None
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(buttonText, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary, fontSize = 16.sp)
        }
    }
}

data class AuthField(
    val label: String,
    val value: String,
    val isPassword: Boolean = false,
    val onChange: (String) -> Unit
)
