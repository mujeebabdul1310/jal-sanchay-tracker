package com.jalsanchay.tracker.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jalsanchay.tracker.data.Result
import com.jalsanchay.tracker.navigation.Screen
import com.jalsanchay.tracker.ui.components.LoadingButton
import com.jalsanchay.tracker.ui.components.WaterHeroCard
import com.jalsanchay.tracker.ui.components.WaterScreenBackground
import com.jalsanchay.tracker.ui.components.WaterSectionCard
import com.jalsanchay.tracker.ui.components.waterFieldColors
import com.jalsanchay.tracker.ui.theme.ErrorRed
import com.jalsanchay.tracker.ui.theme.SuccessGreen
import com.jalsanchay.tracker.ui.theme.WarningOrange
import com.jalsanchay.tracker.ui.viewmodel.AuthViewModel

@Composable
fun SignupScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val signupState by viewModel.signupState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(signupState) {
        when (val result = signupState) {
            is Result.Success -> {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
                viewModel.clearStates()
            }
            is Result.Error -> snackbarHostState.showSnackbar(result.message)
            else -> {}
        }
    }

    val strength = remember(password) {
        when {
            password.length < 8 -> 0.33f
            password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() } -> 1.0f
            password.any { it.isDigit() } -> 0.66f
            else -> 0.33f
        }
    }
    val strengthColor = when (strength) {
        0.33f -> ErrorRed
        0.66f -> WarningOrange
        else -> SuccessGreen
    }
    val strengthLabel = when (strength) {
        0.33f -> "Weak"
        0.66f -> "Medium"
        else -> "Strong"
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        WaterScreenBackground(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                WaterHeroCard(
                    title = "Create account",
                    subtitle = "Build a harvesting profile to track storage, runoff, and water impact over time."
                )
                Spacer(modifier = Modifier.height(18.dp))

                WaterSectionCard {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = if (it.isBlank()) "Name is required" else null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        isError = nameError != null,
                        supportingText = { nameError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = waterFieldColors()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = if (it.isBlank()) "Email is required" else null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        isError = emailError != null,
                        supportingText = { emailError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        colors = waterFieldColors()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = if (it.length < 8) "Minimum 8 characters" else null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = { passwordError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        colors = waterFieldColors()
                    )

                    if (password.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                            val animatedStrength by animateFloatAsState(targetValue = strength, label = "signup_strength")
                            LinearProgressIndicator(
                                progress = { animatedStrength },
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = strengthColor,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                            Text(text = strengthLabel, color = strengthColor, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = if (it != password) "Passwords do not match" else null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = confirmPasswordError != null,
                        supportingText = { confirmPasswordError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        colors = waterFieldColors()
                    )

                    LoadingButton(
                        text = "CREATE ACCOUNT",
                        isLoading = signupState is Result.Loading,
                        onClick = {
                            nameError = if (name.isBlank()) "Name is required" else null
                            emailError = if (email.isBlank()) "Email is required" else null
                            passwordError = if (password.length < 8) "Minimum 8 characters" else null
                            confirmPasswordError = if (confirmPassword != password) "Passwords do not match" else null

                            if (nameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
                                viewModel.signUp(name, email, password)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text(text = "Already have an account? ", color = MaterialTheme.colorScheme.tertiary)
                    Text(
                        text = "Login",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
