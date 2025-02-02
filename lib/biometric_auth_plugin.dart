
import 'biometric_auth_plugin_platform_interface.dart';

class BiometricAuthPlugin {
  Future<String?> getPlatformVersion() {
    return BiometricAuthPluginPlatform.instance.getPlatformVersion();
  }
}
