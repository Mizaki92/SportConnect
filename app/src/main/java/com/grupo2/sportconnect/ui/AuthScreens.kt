package com.grupo2.sportconnect.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo2.sportconnect.ui.theme.SportBackground
import com.grupo2.sportconnect.ui.theme.SportGreen
import com.grupo2.sportconnect.ui.theme.SportInk

@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    onRecoverPassword: () -> Unit,
    onExploreAsGuest: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    AuthPage {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            CivicPortalBadge()
        }

        BrandHeader(
            title = "SportConnect",
            subtitle = "Deporte e integración comunitaria",
            description = "Inicia sesión para confirmar tu reserva, inscribirte a talleres o unirte a tu equipo deportivo barrial."
        )

        message?.let { AuthNotice(text = it, isError = true) }

        AuthField(
            label = "Correo Electrónico",
            value = email,
            onValueChange = { email = it; message = null },
            placeholder = "nombre@correo.com",
            leadingSymbol = "✉",
            keyboardType = KeyboardType.Email
        )
        AuthField(
            label = "Contraseña",
            value = password,
            onValueChange = { password = it; message = null },
            placeholder = "Ingresa tu contraseña",
            leadingSymbol = "▣",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePassword = { passwordVisible = !passwordVisible },
            trailingLabel = "¿Olvidaste tu contraseña?",
            onTrailingLabelClick = onRecoverPassword
        )

        PrimaryAuthButton(text = "Iniciar Sesión  →") {
            message = when {
                !email.isValidEmail() -> "Ingresa un correo electrónico válido."
                password.isBlank() -> "Ingresa tu contraseña para continuar."
                else -> "El servicio de autenticación aún no está conectado. Inténtalo más tarde."
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("¿No tienes una cuenta de deportista?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "Regístrate aquí",
                color = SportGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onRegister).padding(6.dp)
            )
            OutlinedButton(onClick = onExploreAsGuest) {
                Text("Explorar como visitante")
            }
        }

        AuthFooter()
    }
}

@Composable
fun RegisterScreen(onBackToLogin: () -> Unit) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var document by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("San Borja") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmation by rememberSaveable { mutableStateOf("") }
    var termsAccepted by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmationVisible by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    BackHandler(onBack = onBackToLogin)

    AuthPage {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                fontSize = 26.sp,
                color = SportInk,
                modifier = Modifier.clickable(onClick = onBackToLogin).padding(end = 16.dp)
            )
            Text(
                text = "Crear Cuenta",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
            Spacer(Modifier.size(38.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Tag(text = "◉ Registro de Deportista", highlighted = true)
            Tag(text = "Lima Metropolitana")
        }
        Text(
            text = "Accede libremente a reservas de canchas, talleres vecinales y partidos comunitarios.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AuthNotice(
            text = "La cuenta creada es de deportista para reservas, talleres y partidas comunitarias con integración al sistema de accesos QR.",
            isError = false,
            title = "Pase Deportivo Digital"
        )
        AuthNotice(
            text = "Verificación de correo no duplicado y formato válido activo.",
            isError = false,
            title = "Validación en tiempo real"
        )

        AuthField("Nombres", firstName, { firstName = it; message = null }, "Ej. Carlos Eduardo", "♙")
        AuthField("Apellidos", lastName, { lastName = it; message = null }, "Ej. Gómez Peralta", "▤")
        AuthField(
            "Número de DNI / Documento",
            document,
            { document = it.filter(Char::isDigit).take(12); message = null },
            "Ej. 74829103",
            "▤",
            keyboardType = KeyboardType.Number
        )
        AuthField(
            "Número de Teléfono",
            phone,
            { phone = it; message = null },
            "PE  +51   987 654 321",
            "⌕",
            keyboardType = KeyboardType.Phone
        )
        AuthField(
            "Correo Electrónico",
            email,
            { email = it; message = null },
            "nombre@correo.com",
            "✉",
            keyboardType = KeyboardType.Email
        )
        AuthField(
            label = "Distrito de preferencia",
            value = district,
            onValueChange = { district = it; message = null },
            placeholder = "Ingresa tu distrito",
            leadingSymbol = "⌖"
        )
        AuthField(
            label = "Contraseña",
            value = password,
            onValueChange = { password = it; message = null },
            placeholder = "Crea una contraseña segura",
            leadingSymbol = "▣",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePassword = { passwordVisible = !passwordVisible }
        )
        Text(
            text = "Requisitos: mínimo 8 caracteres y al menos 1 número.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AuthField(
            label = "Confirmar Contraseña",
            value = confirmation,
            onValueChange = { confirmation = it; message = null },
            placeholder = "Repite tu contraseña",
            leadingSymbol = "◴",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            passwordVisible = confirmationVisible,
            onTogglePassword = { confirmationVisible = !confirmationVisible }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it; message = null })
            Text(
                text = "Acepto los Términos y Condiciones y la política de protección de datos personales.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        message?.let { AuthNotice(text = it, isError = true) }
        PrimaryAuthButton(text = "Crear mi Cuenta de Deportista") {
            message = when {
                firstName.isBlank() || lastName.isBlank() || document.length < 8 ||
                    phone.isBlank() || district.isBlank() -> "Completa todos los campos obligatorios."
                !email.isValidEmail() -> "Ingresa un correo electrónico válido."
                password.length < 8 || password.none(Char::isDigit) ->
                    "La contraseña debe tener 8 caracteres y al menos un número."
                confirmation != password -> "Las contraseñas no coinciden."
                !termsAccepted -> "Debes aceptar los términos y la política de privacidad."
                else -> "El servicio de registro aún no está conectado. Tus datos no se han enviado."
            }
        }
        Text(
            text = "¿Ya tienes una cuenta registrada?  Inicia sesión →",
            color = SportGreen,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBackToLogin)
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun RecoveryScreen(onBackToLogin: () -> Unit) {
    var selectedMethod by rememberSaveable { mutableStateOf("Correo Electrónico") }
    var identifier by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var requestReady by rememberSaveable { mutableStateOf(false) }

    BackHandler(onBack = onBackToLogin)

    AuthPage {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                fontSize = 26.sp,
                color = SportInk,
                modifier = Modifier.clickable(onClick = onBackToLogin).padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("Recuperar Contraseña", fontWeight = FontWeight.Bold)
                Text(
                    "Identificador: PD02-B",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            CivicPortalBadge()
        }

        BrandHeader(
            title = "¿Olvidaste tu contraseña?",
            subtitle = "",
            description = "Ingresa el correo electrónico asociado a tu cuenta de deportista o tu número de DNI para recibir un enlace seguro y código de restablecimiento."
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Correo Electrónico", "DNI / Documento").forEach { method ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            selectedMethod = method
                            identifier = ""
                            message = null
                            requestReady = false
                        },
                    color = if (selectedMethod == method) MaterialTheme.colorScheme.surface else
                        MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = method,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selectedMethod == method) SportGreen else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AuthField(
            label = if (selectedMethod == "Correo Electrónico") {
                "Correo Electrónico Registrado"
            } else {
                "DNI / Documento Registrado"
            },
            value = identifier,
            onValueChange = {
                identifier = if (selectedMethod == "DNI / Documento") {
                    it.filter(Char::isDigit).take(12)
                } else {
                    it
                }
                message = null
                requestReady = false
            },
            placeholder = if (selectedMethod == "Correo Electrónico") {
                "nombre@correo.com"
            } else {
                "Ingresa tu número de documento"
            },
            leadingSymbol = if (selectedMethod == "Correo Electrónico") "✉" else "▤",
            keyboardType = if (selectedMethod == "Correo Electrónico") {
                KeyboardType.Email
            } else {
                KeyboardType.Number
            }
        )
        Text(
            text = "ⓘ  Enviaremos un código de verificación de 6 dígitos con validez de 15 minutos.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AuthNotice(
            text = "Si no recibes el correo, revisa tu carpeta de correo no deseado (spam) o acércate a la mesa de ayuda de tu sede municipal.",
            isError = false,
            title = "Aviso de Seguridad Municipal"
        )

        message?.let { AuthNotice(text = it, isError = !requestReady) }
        PrimaryAuthButton(text = "Enviar enlace de recuperación  →") {
            val isValid = if (selectedMethod == "Correo Electrónico") {
                identifier.isValidEmail()
            } else {
                identifier.length >= 8
            }
            if (!isValid) {
                requestReady = false
                message = if (selectedMethod == "Correo Electrónico") {
                    "Ingresa un correo electrónico válido."
                } else {
                    "Ingresa un documento válido de al menos 8 dígitos."
                }
            } else {
                requestReady = true
                message = "Solicitud validada. El envío del código estará disponible cuando se conecte el servicio de recuperación."
            }
        }
        Text(
            text = "¿Recordaste tu contraseña?  Iniciar sesión",
            color = SportGreen,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBackToLogin)
                .padding(vertical = 8.dp)
        )
        AuthFooter()
    }
}

@Composable
private fun AuthPage(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SportBackground)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}

@Composable
private fun BrandHeader(title: String, subtitle: String, description: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SportGreen),
                contentAlignment = Alignment.Center
            ) {
                Text("SC", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
        Text(
            text = title,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = SportInk,
            textAlign = TextAlign.Center
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                color = SportGreen,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = description,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingSymbol: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {},
    trailingLabel: String? = null,
    onTrailingLabelClick: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            if (trailingLabel != null) {
                Text(
                    text = trailingLabel,
                    color = SportGreen,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onTrailingLabelClick)
                )
            } else {
                Text(
                    text = "Obligatorio",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Text(
                    text = leadingSymbol,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp
                )
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onTogglePassword) {
                        Text(if (passwordVisible) "◉" else "◎", color = SportGreen)
                    }
                }
            } else {
                null
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            ),
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            }
        )
    }
}

@Composable
private fun PrimaryAuthButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SportGreen)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AuthNotice(text: String, isError: Boolean, title: String? = null) {
    val backgroundColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val foregroundColor = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (title != null) {
                Text(title, color = foregroundColor, fontWeight = FontWeight.SemiBold)
            }
            Text(text, color = foregroundColor, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun CivicPortalBadge() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = "✓  Portal Cívico",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelSmall,
            color = SportGreen,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun Tag(text: String, highlighted: Boolean = false) {
    Surface(
        shape = CircleShape,
        color = if (highlighted) MaterialTheme.colorScheme.tertiaryContainer else
            MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (highlighted) SportGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AuthFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        Text(
            text = "◈  Red Deportiva Municipal & Vecinal",
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Acceso seguro regulado por las redes deportivas comunitarias y municipales.",
            modifier = Modifier.padding(top = 5.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(trim()).matches()
