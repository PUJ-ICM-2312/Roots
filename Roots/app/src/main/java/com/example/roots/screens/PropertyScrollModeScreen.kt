package com.example.roots.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roots.R
import com.example.roots.ui.theme.RootsTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PropertyScrollModeScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) {
        Column( ) {
            PropertyCard()
        }
    }

}

@Composable
fun PropertyCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Precio: $1.650.000", fontWeight = FontWeight.Bold)
            Text(text = "Ubicación: Calle 2# 82-02", fontWeight = FontWeight.Bold)
            Text(text = "Duración contrato: 6 meses", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PropertyFeature(Icons.Default.SquareFoot, "110 m²")
                PropertyFeature(Icons.Default.Bed, "2")
                PropertyFeature(Icons.Default.Shower, "2")
                PropertyFeature(Icons.Default.DirectionsCar, "2")
            }
        }
    }
}

@Composable
fun PropertyFeature(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null)
        Text(text = label)
    }
}
/*
@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel() {
    val pagerState = rememberPagerState()
    val images = listOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3
    )

    HorizontalPager(
        state = pagerState,
        count = images.size
    ) { page ->
        Image(
            painter = painterResource(id = images[page]),
            contentDescription = "Property Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}
*/

@Preview(showBackground = true)
@Composable
fun PreviewPropertyScrollModeScreen() {
    PropertyScrollModeScreen()
}

@Preview(showBackground = true)
@Composable
fun Prev(){
    RootsTheme {
        PropertyScrollModeScreen()
    }
}