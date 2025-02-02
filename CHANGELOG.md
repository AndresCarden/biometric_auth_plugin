## 0.0.1 - Primera Versión

### 🚀 Características:
- Agrega soporte para autenticación biométrica en **Android** (Huella digital, Face Unlock, Iris) y **iOS** (Face ID, Touch ID).
- Verifica si la biometría está disponible en el dispositivo.
- Permite autenticación con métodos biométricos o credenciales del dispositivo.
- Manejo detallado de errores en autenticación fallida o hardware no disponible.
- Soporte para **Android 6.0 (API 23) en adelante** y **iOS 11+**.
- Configuración sencilla con permisos requeridos en `AndroidManifest.xml` y `Info.plist`.
- Ejemplo de uso con **Dart** y **Flutter**.
- Compatible con `BiometricManager` en Android y `LocalAuthentication` en iOS.

### 🛠 Mejoras Planeadas:
- Soporte para `CryptoObject` en Android para operaciones seguras.
- Implementación de autenticación con claves privadas para cifrado seguro.
- Soporte mejorado para múltiples intentos de autenticación con restricciones de tiempo.

---
📌 **Notas:**  
Esta es la versión inicial del plugin. Se recomienda probarlo en dispositivos físicos para garantizar compatibilidad total.

