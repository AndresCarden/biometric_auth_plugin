package com.example.biometric_auth_plugin

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.concurrent.Executor
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class BiometricAuthPlugin : FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler {
  private lateinit var channel: MethodChannel
  private var activity: Activity? = null
  private lateinit var executor: Executor
  private var resultInvoked = false
  private var failedAttempts = 0
  private val maxFailedAttempts = 5
  private var isBlocked = false

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "biometric_auth_plugin")
    channel.setMethodCallHandler(this)
    executor = ContextCompat.getMainExecutor(flutterPluginBinding.applicationContext)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    resultInvoked = false
    failedAttempts = 0

    when (call.method) {
      "authenticate" -> {
        val title = call.argument<String>("title") ?: "Autenticación Biométrica"
        val subtitle = call.argument<String>("subtitle") ?: "Usa tu huella o rostro"
        val description = call.argument<String>("description") ?: "Por favor autentícate."
        val negativeButtonText = call.argument<String>("negativeButtonText") ?: "Cancelar"
        val allowDeviceCredentials = call.argument<Boolean>("allowDeviceCredentials") ?: false
        authenticate(result, title, subtitle, description, negativeButtonText, allowDeviceCredentials)
      }
      "isBiometricAvailable" -> result.success(isBiometricAvailable())
      "getAvailableBiometricTypes" -> result.success(getAvailableBiometricTypes())
      "checkBiometricChanges" -> result.success(detectBiometricChanges())
      "enableBackgroundAuthentication" -> enableBackgroundAuthentication()
      "getBiometricStrengthLevel" -> result.success(getBiometricStrengthLevel())
      else -> result.notImplemented()
    }
  }

  private fun isBiometricAvailable(): Boolean {
    val biometricManager = BiometricManager.from(activity ?: return false)
    return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
  }

  private fun getAvailableBiometricTypes(): List<String> {
    val biometricManager = BiometricManager.from(activity ?: return emptyList())
    val availableTypes = mutableListOf<String>()

    if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
      availableTypes.add("fingerprint")
    }
    // Comprobamos si BIOMETRIC_WEAK está disponible en la versión actual del SDK
    try {
      val biometricWeak = BiometricManager.Authenticators.BIOMETRIC_WEAK
      if (biometricManager.canAuthenticate(biometricWeak) == BiometricManager.BIOMETRIC_SUCCESS) {
        availableTypes.add("face")
      }
    } catch (e: NoSuchFieldError) {
      Log.w("BiometricAuth", "BIOMETRIC_WEAK no disponible en esta versión del SDK.")
    }

    if (biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
      availableTypes.add("pin")
    }

    return availableTypes
  }

  private fun getBiometricStrengthLevel(): String {
    val biometricManager = BiometricManager.from(activity ?: return "unknown")
    return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
      BiometricManager.BIOMETRIC_SUCCESS -> "strong"
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "none_enrolled"
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "no_hardware"
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "hw_unavailable"
      else -> "unknown"
    }
  }

  private fun detectBiometricChanges(): Boolean {
    val biometricManager = BiometricManager.from(activity ?: return false)
    return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
  }

  private fun enableBackgroundAuthentication() {
    val handler = Handler(Looper.getMainLooper())

    val runnable = object : Runnable {
      override fun run() {
        authenticate(
          object : MethodChannel.Result {
            override fun success(result: Any?) {}
            override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {}
            override fun notImplemented() {}
          },
          "Autenticación Requerida",
          "Verifica tu identidad",
          "Se necesita reautenticación.",
          "Cancelar",
          false
        )
        handler.postDelayed(this, 60000) // Verifica cada 60 segundos
      }
    }
    handler.post(runnable)
  }

  private fun authenticate(
    result: MethodChannel.Result,
    title: String,
    subtitle: String,
    description: String,
    negativeButtonText: String,
    allowDeviceCredentials: Boolean
  ) {
    if (isBlocked) {
      result.error("BLOCKED", "Autenticación bloqueada por múltiples intentos fallidos. Intente más tarde.", null)
      return
    }

    val activity = this.activity as? FragmentActivity ?: return
    val authenticators = if (allowDeviceCredentials) {
      BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    } else {
      BiometricManager.Authenticators.BIOMETRIC_STRONG
    }

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle(title)
      .setSubtitle(subtitle)
      .setDescription(description)
      .setNegativeButtonText(negativeButtonText)
      .apply {
        if (allowDeviceCredentials) setDeviceCredentialAllowed(true)
      }
      .build()

    val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
      override fun onAuthenticationSucceeded(authResult: BiometricPrompt.AuthenticationResult) {
        failedAttempts = 0
        result.success(true)
      }

      override fun onAuthenticationFailed() {
        failedAttempts++
        if (failedAttempts >= maxFailedAttempts) {
          isBlocked = true
          Handler(Looper.getMainLooper()).postDelayed({ isBlocked = false }, 30000)
          result.error("TOO_MANY_ATTEMPTS", "Se alcanzó el límite de intentos. Inténtelo en 30 segundos.", null)
        }
      }

      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        result.error("AUTH_ERROR", errString.toString(), errorCode)
      }
    })

    biometricPrompt.authenticate(promptInfo)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
