# Biometric Auth Plugin

## Descripción
Este es un plugin de Flutter para la autenticación biométrica en dispositivos Android e iOS. Permite a las aplicaciones utilizar la autenticación con **huella digital, reconocimiento facial o credenciales del dispositivo (PIN, patrón, contraseña)** para mejorar la seguridad.

## Características
✅ Verifica si la autenticación biométrica está disponible en el dispositivo.
✅ Soporta **Face ID** y **Touch ID** en iOS.
✅ Soporta **Huella digital, Face Unlock e Iris Scan** en Android.
✅ Detecta si no hay datos biométricos registrados.
✅ Permite manejar errores detallados en caso de fallos de autenticación.
✅ Compatible con **Android 6.0 (API 23) en adelante** y **iOS 11 en adelante**.

## Instalación
Agrega la dependencia en tu `pubspec.yaml`:
```yaml
biometric_auth_plugin:
  path: ../biometric_auth_plugin # Modifica según tu estructura
```
Luego, ejecuta:
```sh
flutter pub get
```

## Configuración en Android
### Agregar Permisos en `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
<uses-permission android:name="android.permission.USE_FINGERPRINT" />
```
### Agregar Dependencias en `android/app/build.gradle`
```gradle
dependencies {
    implementation 'androidx.biometric:biometric:1.1.0'
}
```

## Configuración en iOS
Edita el archivo `ios/Runner/Info.plist` y agrega la clave para Face ID:
```xml
<key>NSFaceIDUsageDescription</key>
<string>Se requiere Face ID para autenticación biométrica en la app.</string>
```

## Uso
### Verificar si la biometría está disponible
```dart
bool isAvailable = await BiometricAuthPlugin.isBiometricAvailable();
print("Biometría disponible: $isAvailable");
```

### Iniciar autenticación biométrica
```dart
bool success = await BiometricAuthPlugin.authenticate();
if (success) {
  print("Autenticación exitosa");
} else {
  print("Error en la autenticación");
}
```

## Manejo de Errores
El plugin devuelve errores en caso de fallos de autenticación:
- `BIOMETRIC_ERROR`: No hay hardware biométrico disponible.
- `AUTH_FAILED`: Datos biométricos no reconocidos.
- `AUTH_ERROR`: Otro error desconocido.

## Compatibilidad
| Plataforma | Versión mínima |
|------------|---------------|
| **Android** | 6.0 (API 23) |
| **iOS** | 11.0 |

## Contribuir
Si deseas mejorar este plugin, ¡envía un pull request! 🚀

## Licencia
Este proyecto está bajo la licencia **MIT**.

