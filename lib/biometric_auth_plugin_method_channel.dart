import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'biometric_auth_plugin_platform_interface.dart';

/// An implementation of [BiometricAuthPluginPlatform] that uses method channels.
class MethodChannelBiometricAuthPlugin extends BiometricAuthPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('biometric_auth_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
