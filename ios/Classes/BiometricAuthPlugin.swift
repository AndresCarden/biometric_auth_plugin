import Flutter
import LocalAuthentication
import UIKit

public class BiometricAuthPlugin: NSObject, FlutterPlugin {

    private let context = LAContext()
    private var failedAttempts = 0
    private let maxFailedAttempts = 5

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "biometric_auth_plugin", binaryMessenger: registrar.messenger())
        let instance = BiometricAuthPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        failedAttempts = 0

        switch call.method {
        case "authenticate":
            let args = call.arguments as? [String: Any] ?? [:]
            let title = args["title"] as? String ?? "Autenticación Biométrica"
            let subtitle = args["subtitle"] as? String ?? "Usa Face ID o Touch ID"
            let description = args["description"] as? String ?? "Por favor autentícate."
            let allowDeviceCredentials = args["allowDeviceCredentials"] as? Bool ?? false
            authenticate(result: result, title: title, subtitle: subtitle, description: description, allowDeviceCredentials: allowDeviceCredentials)
        case "isBiometricAvailable":
            result(isBiometricAvailable())
        case "checkBiometricChanges":
            result(checkBiometricChanges())
        case "enableBackgroundAuthentication":
            enableBackgroundAuthentication(result: result)
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    private func isBiometricAvailable() -> Bool {
        var error: NSError?
        return context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error)
    }

    private func checkBiometricChanges() -> Bool {
        let storedBiometricID = context.evaluatedPolicyDomainState
        return storedBiometricID != nil
    }

    private func enableBackgroundAuthentication(result: @escaping FlutterResult) {
        DispatchQueue.global(qos: .background).async {
            while true {
                self.authenticate(result: result, title: "Autenticación requerida", subtitle: "Verifica tu identidad", description: "Se necesita reautenticación.", allowDeviceCredentials: false)
                sleep(60)
            }
        }
    }

    private func authenticate(result: @escaping FlutterResult, title: String, subtitle: String, description: String, allowDeviceCredentials: Bool) {
        context.localizedReason = description

        let policy: LAPolicy = allowDeviceCredentials ? .deviceOwnerAuthentication : .deviceOwnerAuthenticationWithBiometrics

        context.evaluatePolicy(policy, localizedReason: title) { success, error in
            DispatchQueue.main.async {
                if success {
                    result(true)
                } else {
                    self.failedAttempts += 1
                    if self.failedAttempts >= self.maxFailedAttempts {
                        result(FlutterError(code: "TOO_MANY_ATTEMPTS", message: "Se alcanzó el límite de intentos fallidos.", details: nil))
                    } else {
                        result(FlutterError(code: "AUTH_ERROR", message: "Autenticación fallida", details: error?.localizedDescription))
                    }
                }
            }
        }
    }
}
