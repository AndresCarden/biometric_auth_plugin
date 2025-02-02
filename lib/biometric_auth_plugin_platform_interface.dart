import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'biometric_auth_plugin_method_channel.dart';

abstract class BiometricAuthPluginPlatform extends PlatformInterface {
  /// Constructs a BiometricAuthPluginPlatform.
  BiometricAuthPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static BiometricAuthPluginPlatform _instance = MethodChannelBiometricAuthPlugin();

  /// The default instance of [BiometricAuthPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelBiometricAuthPlugin].
  static BiometricAuthPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [BiometricAuthPluginPlatform] when
  /// they register themselves.
  static set instance(BiometricAuthPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
