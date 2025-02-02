import 'dart:async';
import 'package:flutter/services.dart';

class BiometricAuthPlugin {
  static const MethodChannel _channel = MethodChannel('biometric_auth_plugin');

  /// Check if biometrics are available on the device
  static Future<bool> isBiometricAvailable() async {
    final bool available = await _channel.invokeMethod('isBiometricAvailable');
    return available;
  }

  /// Start biometric authentication
  static Future<bool> authenticate() async {
    try {
      final bool authenticated = await _channel.invokeMethod('authenticate');
      return authenticated;
    } catch (e) {
      return false;
    }
  }
}
