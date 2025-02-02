import Flutter
import LocalAuthentication
import UIKit

public class BiometricAuthPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "biometric_auth_plugin", binaryMessenger: registrar.messenger())
        let instance = BiometricAuthPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "authenticate":
            authenticate(result: result)
        case "isBiometricAvailable":
            result(isBiometricAvailable())
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    private func isBiometricAvailable() -> Bool {
        let context = LAContext()
        var error: NSError?
        return context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error)
    }

    private func authenticate(result: @escaping FlutterResult) {
        let context = LAContext()
        let reason = "Usa Face ID o Touch ID para autenticación"

        context.evaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, localizedReason: reason) { success, error in
            DispatchQueue.main.async {
                if success {
                    result(true)
                } else {
                    result(FlutterError(code: "AUTH_ERROR", message: "Autenticación fallida", details: nil))
                }
            }
        }
    }
}
