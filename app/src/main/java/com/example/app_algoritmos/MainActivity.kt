package com.example.app_algoritmos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_algoritmos.ui.theme.App_algoritmosTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_algoritmosTheme {
                AppPrincipal()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPrincipal() {
    var telaAtual by remember { mutableStateOf("home") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Calculadora de Recursos Hídricos")
                },
                navigationIcon = {
                    if (telaAtual != "home") {
                        IconButton(onClick = { telaAtual = "home" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (telaAtual) {
            "home" -> TelaInicial(
                onEvapotranspiracaoClick = { telaAtual = "et" },
                onNecessidadeHidricaClick = { telaAtual = "hidrica" },
                modifier = Modifier.padding(padding)
            )
            "et" -> CalculadoraEvapotranspiracao(modifier = Modifier.padding(padding))
            "hidrica" -> CalculadoraNecessidadeHidrica(modifier = Modifier.padding(padding))
        }
    }
}

// ==================== TELA INICIAL ====================
@Composable
fun TelaInicial(
    onEvapotranspiracaoClick: () -> Unit,
    onNecessidadeHidricaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Calculadoras de\nRecursos Hídricos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            onClick = onEvapotranspiracaoClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("🌡️  Evapotranspiração", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Text("ET₀ (Hargreaves) + ETc da cultura", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            onClick = onNecessidadeHidricaClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("💧  Necessidade Hídrica", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Text("Lâmina de irrigação e tempo de aplicação", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ==================== CALCULADORA DE EVAPOTRANSPIRAÇÃO ====================
@Composable
fun CalculadoraEvapotranspiracao(modifier: Modifier = Modifier) {
    var tMax by remember { mutableStateOf("") }
    var tMin by remember { mutableStateOf("") }
    var ra by remember { mutableStateOf("") }
    var kc by remember { mutableStateOf("1.05") }
    var culturaSelecionada by remember { mutableStateOf("Milho (Kc médio)") }

    var et0 by remember { mutableStateOf(0.0) }
    var etc by remember { mutableStateOf(0.0) }
    var mostrarResultado by remember { mutableStateOf(false) }

    val culturas = listOf(
        "Milho (Kc médio)" to 1.05,
        "Soja (Kc médio)" to 1.00,
        "Feijão (Kc médio)" to 1.05,
        "Cana-de-açúcar" to 1.25,
        "Trigo" to 1.15,
        "Algodão" to 1.15,
        "Pastagem" to 0.85,
        "Inserir Kc manualmente" to 0.0
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Fórmula: Hargreaves-Samani (ET₀)", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = tMax,
            onValueChange = { tMax = it },
            label = { Text("Temperatura Máxima (°C)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = tMin,
            onValueChange = { tMin = it },
            label = { Text("Temperatura Mínima (°C)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = ra,
            onValueChange = { ra = it },
            label = { Text("Radiação Extraterrestre Ra (MJ/m²/dia)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Ex: 30~40 no verão (Brasil). Consulte tabelas ou use calculadora Ra.") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Dropdown de cultura
        var expanded by remember { mutableStateOf(false) }

        @OptIn(ExperimentalMaterial3Api::class)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = culturaSelecionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Cultura") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                culturas.forEach { (nome, valor) ->
                    DropdownMenuItem(
                        text = { Text(nome) },
                        onClick = {
                            culturaSelecionada = nome
                            if (valor > 0) kc = valor.toString()
                            expanded = false
                        }
                    )
                }
            }
        }

        if (culturaSelecionada == "Inserir Kc manualmente") {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = kc,
                onValueChange = { kc = it },
                label = { Text("Kc (Coeficiente da cultura)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val tMaxD = tMax.toDoubleOrNull() ?: 0.0
                val tMinD = tMin.toDoubleOrNull() ?: 0.0
                val raD = ra.toDoubleOrNull() ?: 0.0
                val kcD = kc.toDoubleOrNull() ?: 1.0

                if (tMaxD > tMinD && raD > 0) {
                    et0 = 0.0023 * raD * ( (tMaxD + tMinD) / 2 + 17.8 ) * sqrt(tMaxD - tMinD)
                    etc = et0 * kcD
                    mostrarResultado = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular ET₀ e ETc")
        }

        if (mostrarResultado) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Resultados", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("ET₀ (Evapotranspiração de Referência): %.2f mm/dia".format(et0))
                    Text("ETc (Evapotranspiração da Cultura):   %.2f mm/dia".format(etc))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Fórmula usada: ET₀ = 0.0023 × Ra × (Tmed + 17.8) × √(Tmax − Tmin)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ==================== CALCULADORA DE NECESSIDADE HÍDRICA ====================
@Composable
fun CalculadoraNecessidadeHidrica(modifier: Modifier = Modifier) {
    var etc by remember { mutableStateOf("") }
    var dias by remember { mutableStateOf("") }
    var eficiencia by remember { mutableStateOf("80") }
    var vazao by remember { mutableStateOf("") }

    var laminaLiquida by remember { mutableStateOf(0.0) }
    var laminaBruta by remember { mutableStateOf(0.0) }
    var tempoIrrigacao by remember { mutableStateOf(0.0) }
    var mostrarResultado by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Cálculo da lâmina de irrigação", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = etc,
            onValueChange = { etc = it },
            label = { Text("ETc - Evapotranspiração da cultura (mm/dia)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dias,
            onValueChange = { dias = it },
            label = { Text("Período (dias)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = eficiencia,
            onValueChange = { eficiencia = it },
            label = { Text("Eficiência do sistema de irrigação (%)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = vazao,
            onValueChange = { vazao = it },
            label = { Text("Vazão do sistema (L/h) - opcional") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Deixe em branco se não quiser calcular o tempo") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val etcD = etc.toDoubleOrNull() ?: 0.0
                val diasD = dias.toDoubleOrNull() ?: 0.0
                val efD = eficiencia.toDoubleOrNull() ?: 80.0
                val vazaoD = vazao.toDoubleOrNull()

                if (etcD > 0 && diasD > 0 && efD > 0) {
                    laminaLiquida = etcD * diasD
                    laminaBruta = laminaLiquida / (efD / 100)

                    if (vazaoD != null && vazaoD > 0) {
                        // 1 mm = 10.000 L/ha
                        val volumeLitrosPorHa = laminaBruta * 10000
                        tempoIrrigacao = volumeLitrosPorHa / vazaoD
                    } else {
                        tempoIrrigacao = 0.0
                    }
                    mostrarResultado = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular Necessidade Hídrica")
        }

        if (mostrarResultado) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Resultados", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Lâmina Líquida: %.2f mm".format(laminaLiquida))
                    Text("Lâmina Bruta (com perdas): %.2f mm".format(laminaBruta))

                    if (tempoIrrigacao > 0) {
                        Text("Tempo de irrigação: %.2f horas".format(tempoIrrigacao))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Fórmulas:\n" +
                                "• Lâmina Líquida = ETc × dias\n" +
                                "• Lâmina Bruta = Lâmina Líquida ÷ Eficiência",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}