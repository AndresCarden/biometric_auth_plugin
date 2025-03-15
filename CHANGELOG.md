## 0.0.3 - Soporte Completo para iOS

### 🚀 Nuevas Características:
- **Soporte para credenciales del dispositivo (PIN, patrón, contraseña)** en iOS.
- **Detección de cambios biométricos**: Verifica si los datos biométricos han cambiado en iOS.
- **Autenticación en segundo plano**: Permite la reautenticación automática en intervalos definidos en iOS.
- **Personalización del diálogo de autenticación**: Personaliza el título, subtítulo, descripción y botón negativo en iOS.
- **Manejo mejorado de intentos fallidos**: Se agregaron límites de intentos fallidos con control más eficiente en iOS.

### 🛠 Mejoras:
- **Optimización del manejo de errores**: Se agregaron mensajes más detallados para los errores de autenticación en iOS.
- **Compatibilidad mejorada**: Se corrigieron errores para garantizar mejor estabilidad en iOS.
- **Mejor control de flujo**: Se evitó que se envíen respuestas duplicadas en caso de múltiples intentos de autenticación.

---
📌 **Notas:**
Esta versión agrega compatibilidad total con iOS, permitiendo autenticación con Face ID, Touch ID y credenciales del dispositivo. Se recomienda actualizar a esta versión para una mejor seguridad y experiencia de usuario.

## 0.0.2 - Mejoras y Nuevas Características

### 🚀 Nuevas Características:
- **Detección de cambios en la biometría**: Verifica si las huellas digitales o datos biométricos han cambiado.
- **Autenticación en segundo plano**: Permite la reautenticación automática en intervalos definidos.
- **Soporte para personalización del diálogo**: Permite personalizar el título, subtítulo, descripción y botón negativo del cuadro de autenticación.
- **Manejo mejorado de intentos fallidos**: Ahora se pueden configurar límites de intentos y manejar errores de forma más efectiva.

### 🛠 Mejoras:
- **Optimización del manejo de errores**: Se agregaron mensajes más detallados para los errores de autenticación.
- **Compatibilidad mejorada**: Se corrigieron errores para garantizar mejor estabilidad en Android e iOS.
- **Mejor control de flujo**: Se evitó que se envíen respuestas duplicadas en caso de múltiples intentos de autenticación.

---
📌 **Notas:**
Esta versión mejora la experiencia de autenticación y agrega nuevas funcionalidades avanzadas. Se recomienda actualizar a esta versión para mayor seguridad y usabilidad.

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

