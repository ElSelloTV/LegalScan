# LegalScan

App Android para abogados: convierte fotos (sacadas con la cámara o recibidas por WhatsApp) en un PDF prolijo, con detección y enderezado automático de bordes, listo para compartir por WhatsApp o correo. No guarda nada en el celular — el PDF es temporal.

## Cómo funciona

1. **Escanear / importar** → abre la cámara con detección automática de bordes (o el selector de galería, para fotos ya recibidas por WhatsApp). Se pueden agregar varias páginas.
2. El motor de escaneo (ML Kit Document Scanner de Google) recorta, endereza la perspectiva y mejora el contraste de cada página, y arma un solo PDF.
3. **Compartir** → un botón dedicado a WhatsApp, uno a correo, y uno genérico ("compartir con otra app") para el resto.
4. El PDF vive en un área temporal gestionada por Google Play services — LegalScan nunca lo copia a la galería, a Descargas, ni a almacenamiento propio de la app.

## Stack técnico

- Kotlin + Jetpack Compose (Material 3)
- [ML Kit Document Scanner](https://developers.google.com/ml-kit/vision/doc-scanner) (`com.google.android.gms:play-services-mlkit-document-scanner`) — detección de bordes, recorte, enderezado y generación de PDF, todo on-device.
- minSdk 26 (Android 8.0+), compileSdk / targetSdk 34.

No se usó ninguna librería propia de recorte/detección de bordes: ML Kit ya lo resuelve, así que el código de la app se limita a la UI y a armar los intents para compartir. Esto mantiene el proyecto simple y fácil de mantener.

## Requisitos para abrir el proyecto

- Android Studio (Koala o más reciente)
- JDK 17 (Android Studio ya trae uno embebido)
- Un celular o emulador con **Google Play services** instalado (obligatorio: ML Kit Document Scanner depende de Play services, no funciona en celulares sin Google, p. ej. Huawei sin GMS)

Pasos: `File → Open` → elegir esta carpeta. Android Studio detecta el `gradlew` incluido y sincroniza solo. Compilé y verifiqué el proyecto en este entorno (`./gradlew :app:assembleDebug`) antes de subirlo — quedó verde.

## Qué falta antes de subir a Play Store

- **Ícono final**: el ícono actual es un placeholder simple (documento + esquinas de encuadre) hecho en vector, para que compile sin depender de archivos gráficos externos. Conviene reemplazarlo por un diseño definitivo antes de publicar.
- **Política de privacidad**: Play Store exige una URL de política de privacidad para cualquier app que use la cámara. Como LegalScan no junta ni sube datos a ningún servidor (todo el procesamiento es local, on-device, y el archivo es temporal), la política puede ser muy simple — puedo redactarla cuando quieras.
- **Firma de la app**: falta generar el keystore de release y configurar `signingConfig` en `app/build.gradle.kts` (no lo hice porque es una clave privada tuya, no algo que deba generar por vos sin que la resguardes).
- **Ficha de Play Store**: capturas de pantalla, descripción, clasificación de contenido, cuestionario de seguridad de datos (ahí declarás "no recopilamos datos").
- **Monetización** (si la carrera dijiste): lo más simple para esta app es un pago único o una versión gratuita limitada (p. ej. hasta 3 páginas por PDF) + versión paga sin límite, usando Play Billing. No lo implementé todavía para no sobrecargar el MVP — lo armamos cuando confirmes el modelo que preferís.

## Estructura del proyecto

```
app/src/main/java/ar/com/elsellotv/legalscan/
├── MainActivity.kt      → única pantalla: botón de escaneo, resultado, botones de compartir
├── ScanUiState.kt        → estados de la UI (Idle / Ready / Error)
├── ShareUtils.kt         → intents de compartir (WhatsApp, correo, genérico)
└── ui/theme/Theme.kt      → tema Material 3 (colores LegalScan)
```
