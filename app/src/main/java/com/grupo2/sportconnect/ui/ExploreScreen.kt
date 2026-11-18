package com.grupo2.sportconnect.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo2.sportconnect.domain.model.Court
import com.grupo2.sportconnect.domain.model.Reservation
import com.grupo2.sportconnect.domain.model.ReservationRequest
import com.grupo2.sportconnect.domain.model.SlotAvailability
import com.grupo2.sportconnect.domain.model.TimeSlot
import com.grupo2.sportconnect.domain.usecase.GetCourtsUseCase
import com.grupo2.sportconnect.domain.usecase.GetReservationsUseCase
import com.grupo2.sportconnect.domain.usecase.IsReservationAvailableUseCase
import com.grupo2.sportconnect.domain.usecase.RequestReservationUseCase
import com.grupo2.sportconnect.domain.usecase.SearchCourtsUseCase
import com.grupo2.sportconnect.domain.repository.RepositoryException

@Composable
fun ExploreScreen(
    onLoginClick: () -> Unit = {},
    getCourts: GetCourtsUseCase,
    getReservations: GetReservationsUseCase,
    searchCourts: SearchCourtsUseCase,
    isReservationAvailable: IsReservationAvailableUseCase,
    requestReservation: RequestReservationUseCase
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedSport by rememberSaveable { mutableStateOf("Todos") }
    var selectedDay by rememberSaveable { mutableStateOf("Hoy") }
    var selectedCourt by remember { mutableStateOf<Court?>(null) }
    var demoReservations by remember { mutableStateOf(emptyList<Reservation>()) }
    var courts by remember { mutableStateOf(emptyList<Court>()) }
    var reservationDataAvailable by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val sports = listOf("Todos", "Fútbol 7", "Básquet", "Tenis")
    val days = listOf("Hoy", "Mañana", "Fin de semana")
    val filteredCourts = searchCourts(courts, query, selectedSport, selectedDay)
    val colors = MaterialTheme.colorScheme
    androidx.compose.runtime.LaunchedEffect(getCourts, getReservations) {
        isLoading = true
        try {
            courts = getCourts()
            demoReservations = getReservations()
            reservationDataAvailable = true
            loadError = null
        } catch (exception: RepositoryException) {
            reservationDataAvailable = false
            loadError = exception.userMessage
        } finally {
            isLoading = false
        }
    }

    BackHandler(enabled = selectedCourt != null) {
        selectedCourt = null
    }

    if (selectedCourt != null) {
        CourtDetailScreen(
            court = selectedCourt!!,
            reservations = demoReservations,
            reservationDataAvailable = reservationDataAvailable,
            isReservationAvailable = isReservationAvailable,
            onBack = { selectedCourt = null },
            onRequestReservation = { request ->
                val created = requestReservation(request)
                if (created) demoReservations = getReservations()
                created
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .safeDrawingPadding()
                .padding(top = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    top = 8.dp,
                    end = 16.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Header()
                }
                item {
                    SearchField(
                        query = query,
                        onQueryChange = { query = it }
                    )
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterRow(
                            options = sports,
                            selected = selectedSport,
                            onSelect = { selectedSport = it }
                        )
                        FilterRow(
                            options = days,
                            selected = selectedDay,
                            onSelect = { selectedDay = it }
                        )
                    }
                }
                item {
                    VisitorMessage(onLoginClick = onLoginClick)
                }
                loadError?.let { error ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "No se pudieron cargar las canchas o reservas: $error",
                                modifier = Modifier.padding(14.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Canchas disponibles",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "${filteredCourts.size} espacios · horarios de demostración",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
                if (filteredCourts.isEmpty() && loadError == null && isLoading) {
                    item {
                        Text(
                            text = "Cargando disponibilidad...",
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                } else if (filteredCourts.isEmpty() && loadError == null) {
                    item {
                        EmptyResults()
                    }
                } else {
                    items(filteredCourts, key = { it.name }) { court ->
                        CourtCard(
                            court = court,
                            selectedDay = selectedDay,
                            onDetails = { selectedCourt = court }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text("SC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "SportConnect",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Distrito: San Isidro / Miraflores",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = "●",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⌕", fontSize = 25.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(9.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                "Buscar canchas o deportes",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun FilterRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(option) }
            )
        }
    }
}

@Composable
private fun VisitorMessage(onLoginClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text("i", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("¿Navegando como visitante?", fontWeight = FontWeight.SemiBold)
                Text(
                    "Inicia sesión para confirmar reservas y recibir tu pase QR.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onLoginClick) {
                Text("Ingresar")
            }
        }
    }
}

@Composable
private fun CourtCard(court: Court, selectedDay: String, onDetails: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            CourtArtwork(court = court, availabilityDay = selectedDay)
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = court.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${court.venue} · ${court.address}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    DetailTag(court.sport)
                    DetailTag(court.features)
                }
                Text(
                    text = "◷  Disponibilidad de demostración",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedButton(
                    onClick = onDetails,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver disponibilidad y reservar")
                    Spacer(Modifier.width(6.dp))
                    Text("→")
                }
            }
        }
    }
}

@Composable
internal fun CourtArtwork(
    court: Court,
    height: Dp = 158.dp,
    availabilityDay: String = "Hoy"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2C8A58))
        ) {
            val stripeWidth = size.width / 8
            for (stripe in 0..7) {
                if (stripe % 2 == 0) {
                    drawRect(
                        color = Color(0xFF23804F).copy(alpha = 0.55f),
                        topLeft = Offset(stripe * stripeWidth, 0f),
                        size = Size(stripeWidth, size.height)
                    )
                }
            }
            val inset = 18.dp.toPx()
            drawRoundRect(
                color = Color.White.copy(alpha = 0.82f),
                topLeft = Offset(inset, inset),
                size = Size(size.width - inset * 2, size.height - inset * 2),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )
            drawLine(
                color = Color.White.copy(alpha = 0.82f),
                start = Offset(size.width / 2, inset),
                end = Offset(size.width / 2, size.height - inset),
                strokeWidth = 2.dp.toPx()
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.82f),
                radius = 23.dp.toPx(),
                center = Offset(size.width / 2, size.height / 2),
                style = Stroke(width = 2.dp.toPx())
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            color = Color.White,
            shape = RoundedCornerShape(30.dp)
        ) {
            val availableCount = court.scheduleByDay[availabilityDay]
                .orEmpty()
                .count { it.availability == SlotAvailability.AVAILABLE }
            Text(
                text = if (availableCount == 0) {
                    "●  Sin franjas libres $availabilityDay"
                } else {
                    "●  $availableCount franjas libres $availabilityDay"
                },
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            color = Color(0xE6003527),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = if (court.hourlyPricePEN == 0) {
                    "Gratis"
                } else {
                    "${formatPEN(court.hourlyPricePEN)} / hora"
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
internal fun DetailTag(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
        shape = RoundedCornerShape(30.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1
        )
    }
}

@Composable
private fun EmptyResults() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No hay canchas con esos filtros", fontWeight = FontWeight.SemiBold)
            Text(
                "Prueba con otro deporte, fecha o término de búsqueda.",
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
