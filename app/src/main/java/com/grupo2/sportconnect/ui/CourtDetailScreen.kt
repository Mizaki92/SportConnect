package com.grupo2.sportconnect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import com.grupo2.sportconnect.domain.model.Court
import com.grupo2.sportconnect.domain.model.Reservation
import com.grupo2.sportconnect.domain.model.ReservationRequest
import com.grupo2.sportconnect.domain.model.SlotAvailability
import com.grupo2.sportconnect.domain.model.TimeSlot
import com.grupo2.sportconnect.domain.usecase.IsReservationAvailableUseCase
import com.grupo2.sportconnect.domain.repository.RepositoryException
import kotlinx.coroutines.launch

private data class BookingDay(
    val date: LocalDate,
    val scheduleKey: String,
    val label: String
)

@Composable
internal fun CourtDetailScreen(
    court: Court,
    reservations: List<Reservation>,
    reservationDataAvailable: Boolean,
    isReservationAvailable: IsReservationAvailableUseCase,
    onBack: () -> Unit,
    onRequestReservation: suspend (ReservationRequest) -> Boolean
) {
    val today = androidx.compose.runtime.remember { LocalDate.now() }
    val days = listOf(
        BookingDay(today, "Hoy", "Hoy"),
        BookingDay(today.plusDays(1), "Mañana", "Mañana"),
        BookingDay(
            today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)),
            "Fin de semana",
            "Sábado"
        )
    )
    var selectedDayIndex by rememberSaveable(court.name) { mutableStateOf(0) }
    var selectedStartMinutes by rememberSaveable(court.name) { mutableStateOf<Int?>(null) }
    var showReviewDialog by rememberSaveable(court.name) { mutableStateOf(false) }
    var reservationMessage by rememberSaveable(court.name) { mutableStateOf<String?>(null) }
    var reservationSucceeded by rememberSaveable(court.name) { mutableStateOf(false) }
    var isSubmitting by rememberSaveable(court.name) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val selectedDay = days[selectedDayIndex]
    val slots = court.scheduleByDay[selectedDay.scheduleKey].orEmpty()
    val selectedSlot = slots.firstOrNull { it.startMinutes == selectedStartMinutes }
    val request = selectedSlot?.let {
        ReservationRequest(court, selectedDay.scheduleKey, selectedDay.date, it)
    }
    val selectedSlotIsAvailable = reservationDataAvailable &&
        request != null && isReservationAvailable(request, reservations)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 250.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack) {
                        Text("←  Volver")
                    }
                    Text(
                        text = "Detalle de cancha",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CourtArtwork(court = court, height = 210.dp)
                    Text(
                        text = court.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${court.sport} · ${court.surface}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailTag(court.features)
                        DetailTag("Capacidad ${court.capacity}")
                    }
                }
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "SEDE MUNICIPAL",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(court.venue, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(court.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Aforo: ${court.capacity} jugadores")
                            Text(
                                text = if (court.hourlyPricePEN == 0) {
                                    "Gratis"
                                } else {
                                    "${formatPEN(court.hourlyPricePEN)} / hora"
                                },
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Seleccionar fecha",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedDay.date.format(
                            DateTimeFormatter.ofPattern("MMMM yyyy", spanishLocale())
                        ).replaceFirstChar { it.titlecase(spanishLocale()) },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        days.forEachIndexed { index, day ->
                            val isSelected = selectedDayIndex == index
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .clickable {
                                        selectedDayIndex = index
                                        selectedStartMinutes = null
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                tonalElevation = if (isSelected) 0.dp else 1.dp
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = day.label.uppercase(spanishLocale()),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = day.date.dayOfMonth.toString(),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Franjas horarias",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Duración: 60 min", style = MaterialTheme.typography.labelMedium)
                    }
                    if (slots.isEmpty()) {
                        Text(
                            text = "No hay horarios publicados para esta fecha.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    slots.forEach { slot ->
                        val slotRequest = ReservationRequest(court, selectedDay.scheduleKey, selectedDay.date, slot)
                        val canSelect = reservationDataAvailable &&
                            isReservationAvailable(slotRequest, reservations)
                        val isSelected = selectedStartMinutes == slot.startMinutes && canSelect
                        val reservationForSlot = reservations.firstOrNull {
                            it.courtName == court.name &&
                                it.date == selectedDay.date &&
                                it.startMinutes == slot.startMinutes
                        }
                        val status = when {
                            !reservationDataAvailable -> "Disponibilidad no disponible"
                            reservationForSlot != null -> reservationForSlot.status.label
                            slot.availability != SlotAvailability.AVAILABLE -> slot.availability.label
                            !canSelect -> "Conflicto con otra reserva"
                            else -> "Disponible"
                        }
                        TimeSlotCard(
                            slot = slot,
                            status = status,
                            selected = isSelected,
                            enabled = canSelect,
                            onClick = { selectedStartMinutes = slot.startMinutes }
                        )
                    }
                }
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Resumen de solicitud",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        SummaryRow("Responsable", "Usuario de demostración")
                        SummaryRow("Duración", selectedSlot?.let { slotDurationLabel(it) } ?: "Selecciona una franja")
                        SummaryRow("Tarifa referencial", formatPEN(court.hourlyPricePEN))
                        Text(
                            text = court.conditions,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Puedes cancelar sin incidencia hasta 2 horas antes del inicio. La disponibilidad real se debe validar con el servidor.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            item {
                DemoDataNotice()
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total referencial", style = MaterialTheme.typography.labelMedium)
                        Text(
                            formatPEN(court.hourlyPricePEN),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (selectedSlot == null) {
                            "Elige fecha y hora"
                        } else {
                            "${formatDate(selectedDay.date)}\n${slotDurationLabel(selectedSlot)}"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { showReviewDialog = true },
                    enabled = selectedSlotIsAvailable && !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Confirmar y solicitar reserva  →", fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Prototipo: la solicitud se guarda solo en este dispositivo.",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }

    if (showReviewDialog && request != null) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Resumen de reserva") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(court.name, fontWeight = FontWeight.SemiBold)
                    Text(court.venue)
                    Text("${formatDate(request.date)} · ${slotDurationLabel(request.slot)}")
                    Text("Responsable: Usuario de demostración")
                    Text("Importe referencial: ${formatPEN(court.hourlyPricePEN)}")
                    Text(
                        if (court.hourlyPricePEN == 0) {
                            "La franja gratuita se confirmará en este prototipo."
                        } else {
                            "La franja quedará pendiente del comprobante ficticio. No se realizará ningún cobro."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isSubmitting,
                    onClick = {
                        scope.launch {
                            isSubmitting = true
                            showReviewDialog = false
                            try {
                                reservationSucceeded = onRequestReservation(request)
                                reservationMessage = if (reservationSucceeded) {
                                    if (court.hourlyPricePEN == 0) {
                                        "Reserva confirmada en la demostración. La confirmación real requiere el backend."
                                    } else {
                                        "Solicitud registrada como pendiente de comprobante en la demostración. No se realizó ningún pago."
                                    }
                                } else {
                                    "El horario ya no está disponible. Actualiza la disponibilidad e intenta con otra franja."
                                }
                                if (reservationSucceeded) selectedStartMinutes = null
                            } catch (exception: RepositoryException) {
                                reservationSucceeded = false
                                reservationMessage = exception.userMessage
                            } finally {
                                isSubmitting = false
                            }
                        }
                    }
                ) {
                    Text("Solicitar reserva")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Volver")
                }
            }
        )
    }

    reservationMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { reservationMessage = null },
            title = {
                Text(if (reservationSucceeded) "Solicitud registrada" else "Horario no disponible")
            },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { reservationMessage = null }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
private fun TimeSlotCard(
    slot: TimeSlot,
    status: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val background = when {
        selected -> MaterialTheme.colorScheme.primary
        enabled -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    }
    val foreground = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (selected) "✓" else "◷",
                color = if (selected) Color.White else MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = slotDurationLabel(slot),
                    color = foreground,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = status,
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                text = if (enabled) {
                    if (selected) "Seleccionada" else "Elegir"
                } else {
                    "—"
                },
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DemoDataNotice() {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = "Datos ficticios para el avance académico. La disponibilidad de esta pantalla no está sincronizada con otras personas ni con una sede.",
            modifier = Modifier.padding(14.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun slotDurationLabel(slot: TimeSlot): String =
    "${formatTime(slot.startMinutes)} - ${formatTime(slot.endMinutes)}"

private fun formatTime(minutes: Int): String =
    String.format(Locale.ROOT, "%02d:%02d", minutes / 60, minutes % 60)

internal fun formatPEN(amount: Int): String =
    if (amount == 0) "Gratis" else "S/ ${String.format(Locale.ROOT, "%d.00", amount)}"

private fun formatDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", spanishLocale()))
        .replaceFirstChar { it.titlecase(spanishLocale()) }

private fun spanishLocale(): Locale = Locale.forLanguageTag("es-PE")
