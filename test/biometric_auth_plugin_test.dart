import 'package:flutter_test/flutter_test.dart';
import 'package:biometric_auth_plugin/biometric_auth_plugin.dart';
import 'package:biometric_auth_plugin/biometric_auth_plugin_platform_interface.dart';
import 'package:biometric_auth_plugin/biometric_auth_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockBiometricAuthPluginPlatform
    with MockPlatformInterfaceMixin
    implements BiometricAuthPluginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final BiometricAuthPluginPlatform initialPlatform = BiometricAuthPluginPlatform.instance;

  test('$MethodChannelBiometricAuthPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelBiometricAuthPlugin>());
  });

  test('getPlatformVersion', () async {
    BiometricAuthPlugin biometricAuthPlugin = BiometricAuthPlugin();
    MockBiometricAuthPluginPlatform fakePlatform = MockBiometricAuthPluginPlatform();
    BiometricAuthPluginPlatform.instance = fakePlatform;

    expect(await biometricAuthPlugin.getPlatformVersion(), '42');
  });
}
