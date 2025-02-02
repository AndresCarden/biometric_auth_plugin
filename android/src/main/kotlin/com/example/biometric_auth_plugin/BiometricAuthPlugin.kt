package com.example.biometric_auth_plugin

import android.app.Activity
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

class BiometricAuthPlugin : FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler {
  private lateinit var channel: MethodChannel
  private var activity: Activity? = null
  private lateinit var executor: Executor

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "biometric_auth_plugin")
    channel.setMethodCallHandler(this)
    executor = ContextCompat.getMainExecutor(flutterPluginBinding.applicationContext)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    Log.d("BiometricAuth", "📌 Actividad adjuntada correctamente: ${activity?.localClassName}")
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
    Log.d("BiometricAuth", "📌 Actividad desvinculada por cambios de configuración.")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
    Log.d("BiometricAuth", "📌 Actividad re-adjuntada después de cambio de configuración.")
  }

  override fun onDetachedFromActivity() {
    activity = null
    Log.d("BiometricAuth", "📌 Actividad desvinculada completamente.")
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    when (call.method) {
      "authenticate" -> authenticate(result)
      "isBiometricAvailable" -> result.success(isBiometricAvailable())
      else -> result.notImplemented()
    }
  }

  private fun isBiometricAvailable(): Boolean {
    val biometricManager = BiometricManager.from(activity ?: return false)
    return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
  }

  private fun authenticate(result: MethodChannel.Result) {
    val activity = this.activity as? FragmentActivity
    if (activity == null) {
      Log.e("BiometricAuth", "❌ Error: No se pudo obtener la actividad.")
      result.error("ACTIVITY_ERROR", "No se pudo obtener la actividad.", null)
      return
    }

    val biometricManager = BiometricManager.from(activity)
    val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
      Log.e("BiometricAuth", "❌ Error: El hardware biométrico no está disponible o configurado.")
      result.error("BIOMETRIC_ERROR", "El hardware biométrico no está disponible o configurado.", null)
      return
    }

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle("Autenticación Biométrica")
      .setSubtitle("Usa tu huella o rostro para continuar")
      .setDescription("Por favor autentícate para continuar.")
      .setNegativeButtonText("Cancelar")
      .build()

    val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
      override fun onAuthenticationSucceeded(authResult: BiometricPrompt.AuthenticationResult) {
        Log.d("BiometricAuth", "✅ Autenticación exitosa.")
        val cryptoObject = authResult.cryptoObject
        Log.d("BiometricAuth", "CryptoObject: $cryptoObject")
        result.success(true)
      }

      override fun onAuthenticationFailed() {
        Log.w("BiometricAuth", "❌ Falló la autenticación: Datos biométricos no reconocidos.")
        result.error("AUTH_FAILED", "Datos biométricos no reconocidos. Intente nuevamente.", null)
      }

      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        Log.e("BiometricAuth", "⚠️ Error de autenticación ($errorCode): $errString")
        result.error("AUTH_ERROR", errString.toString(), errorCode)
      }
    })

    biometricPrompt.authenticate(promptInfo)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
