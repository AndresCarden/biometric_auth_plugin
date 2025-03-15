import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class BiometricAuthPlugin {
  static const MethodChannel _channel = MethodChannel('biometric_auth_plugin');

  /// Verifica si la autenticación biométrica está disponible en el dispositivo.
  static Future<bool> isBiometricAvailable() async {
    try {
      final bool available = await _channel.invokeMethod('isBiometricAvailable');
      return available;
    } catch (e) {
      if (kDebugMode) {
        print("❌ Error al verificar biometría: $e");
      }
      return false;
    }
  }

  /// Obtiene los tipos de autenticación biométrica disponibles en el dispositivo.
  static Future<List<String>> getAvailableBiometricTypes() async {
    try {
      final List<dynamic> availableTypes =
      await _channel.invokeMethod('getAvailableBiometricTypes');
      return availableTypes.cast<String>();
    } catch (e) {
      if (kDebugMode) {
        print("❌ Error obteniendo tipos biométricos: $e");
      }
      return [];
    }
  }

  /// Inicia la autenticación biométrica con opciones personalizadas.
  static Future<bool> authenticate({
    String title = "Autenticación Biométrica",
    String subtitle = "Usa tu huella o rostro",
    String description = "Por favor autentícate.",
    String negativeButtonText = "Cancelar",
    bool allowDeviceCredentials = false, // Opción para permitir PIN/Patrón
    bool useCrypto = false, // Opción para autenticación con encriptación
  }) async {
    final completer = Completer<bool>();

    try {
      final bool authenticated = await _channel.invokeMethod('authenticate', {
        "title": title,
        "subtitle": subtitle,
        "description": description,
        "negativeButtonText": negativeButtonText,
        "allowDeviceCredentials": allowDeviceCredentials,
        "useCrypto": useCrypto,
      });
      completer.complete(authenticated);
    } on PlatformException catch (e) {
      if (kDebugMode) {
        print("❌ Error en autenticación biométrica: ${e.message}");
      }

      if (e.code == "TOO_MANY_ATTEMPTS") {
        print("⛔ Se superó el número máximo de intentos.");
      }

      completer.complete(false);
    }

    return completer.future;
  }

  /// Detección de cambios en la biometría (ejemplo: se eliminaron o agregaron huellas).
  static Future<bool> checkBiometricChanges() async {
    try {
      final bool changed = await _channel.invokeMethod('checkBiometricChanges');
      return changed;
    } catch (e) {
      if (kDebugMode) {
        print("⚠️ Error verificando cambios biométricos: $e");
      }
      return false;
    }
  }

  /// Habilita autenticación en segundo plano cada cierto tiempo.
  static Future<void> enableBackgroundAuthentication() async {
    try {
      await _channel.invokeMethod('enableBackgroundAuthentication');
    } catch (e) {
      if (kDebugMode) {
        print("⚠️ Error activando autenticación en segundo plano: $e");
      }
    }
  }

  /// Obtiene el nivel de seguridad de la biometría del dispositivo.
  static Future<String> getBiometricStrengthLevel() async {
    try {
      final String strength = await _channel.invokeMethod('getBiometricStrengthLevel');
      return strength;
    } catch (e) {
      if (kDebugMode) {
        print("⚠️ Error obteniendo el nivel de seguridad biométrica: $e");
      }
      return "unknown";
    }
  }
}
