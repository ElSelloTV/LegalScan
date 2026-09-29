# LegalScan

App Android para abogados: convierte fotos (sacadas con la cámara o recibidas por WhatsApp) en un PDF prolijo, con detección y enderezado automático de bordes, listo para compartir por WhatsApp o correo. No guarda nada en el celular — el PDF es temporal.

## Cómo funciona

1. **Escanear / importar** → abre la cámara con detección automática de bordes (o el selector de galería, para fotos ya recibidas por WhatsApp). Se pueden agregar varias páginas.
2. El motor de escaneo (ML Kit Document Scanner de Google) recorta, endereza la perspectiva y mejora el contraste de cada página, y arma un solo PDF.
3. **Compartir** → un botón dedicado a WhatsApp, uno a correo, y uno genérico ("compartir con otra app") para el resto.
4. El PDF vive en un área temporal gestionada por Google Play services — LegalScan nunca lo copia a la galería, a Descargas, ni a almacenamiento propio de la app.
5. Al abrir la app se muestra **un único anuncio** (App Open Ad de Google AdMob) y nada más — sin banners ni anuncios entre pantallas.

## Stack técnico

- Kotlin + Jetpack Compose (Material 3)
- [ML Kit Document Scanner](https://developers.google.com/ml-kit/vision/doc-scanner) (`com.google.android.gms:play-services-mlkit-document-scanner`) — detección de bordes, recorte, enderezado y generación de PDF, todo on-device.
- [Google Mobile Ads](https://developers.google.com/admob/android/quick-start) (`com.google.android.gms:play-services-ads`) — un único App Open Ad al arrancar (`app/src/main/java/.../ads/AppOpenAdManager.kt`).
- [User Messaging Platform](https://developers.google.com/admob/ump/android/quick-start) (`com.google.android.ump:user-messaging-platform`) — formulario de consentimiento de anuncios, obligatorio en UE/Reino Unido/California antes de mostrar el anuncio.
- minSdk 26 (Android 8.0+), compileSdk / targetSdk 34.

No se usó ninguna librería propia de recorte/detección de bordes: ML Kit ya lo resuelve, así que el código de la app se limita a la UI y a armar los intents para compartir. Esto mantiene el proyecto simple y fácil de mantener.

## Requisitos para abrir el proyecto

- Android Studio (Koala o más reciente)
- JDK 17 (Android Studio ya trae uno embebido)
- Un celular o emulador con **Google Play services** instalado (obligatorio: ML Kit Document Scanner depende de Play services, no funciona en celulares sin Google, p. ej. Huawei sin GMS)

Pasos: `File → Open` → elegir esta carpeta. Android Studio detecta el `gradlew` incluido y sincroniza solo. Compilé y verifiqué el proyecto en este entorno (`./gradlew :app:assembleDebug`) antes de subirlo — quedó verde.

## Publicidad (AdMob)

La app usa la cuenta real de AdMob de LegalScan:

- `app/src/main/AndroidManifest.xml` → meta-data `com.google.android.gms.ads.APPLICATION_ID` = AdMob App ID.
- `app/src/main/res/values/strings.xml` → `app_open_ad_unit_id` = Ad Unit ID de "LegalScan - App Open".

Hasta que Google apruebe la app vinculada a la ficha de Play Store, AdMob sigue sirviendo anuncios de prueba en su lugar (comportamiento normal, no hay que tocar nada). Una vez publicada y aprobada, empiezan a mostrarse anuncios reales sin ningún cambio de código.

## Qué falta antes de subir a Play Store

- **Ícono final**: el ícono actual es un placeholder simple (documento + esquinas de encuadre) hecho en vector, para que compile sin depender de archivos gráficos externos. Conviene reemplazarlo por un diseño definitivo antes de publicar.
- **Política de privacidad**: ya redactada y publicada (incluye la sección de publicidad/AdMob) — falta completar el nombre del titular tal como figura en Play Console.
- **Firma de la app**: falta generar el keystore de release y configurar `signingConfig` en `app/build.gradle.kts` (no lo hice porque es una clave privada tuya, no algo que deba generar por vos sin que la resguardes).
- **Ficha de Play Store**: capturas de pantalla, descripción, clasificación de contenido, y el cuestionario de **Seguridad de los datos** — ahí hay que declarar que se comparte el identificador de publicidad con Google (AdMob) para publicidad, ya no "no data collected".
- **Vincular AdMob con la ficha de Play Store**: una vez creada la ficha en Play Console, hay que enlazarla desde AdMob (Apps → LegalScan → vincular con Play Store) para salir del período de anuncios limitados.

## Estructura del proyecto

```
app/src/main/java/ar/com/elsellotv/legalscan/
├── MainActivity.kt        → única pantalla: consentimiento + anuncio, botón de escaneo, resultado, botones de compartir
├── ScanUiState.kt          → estados de la UI (Idle / Ready / Error)
├── ShareUtils.kt           → intents de compartir (WhatsApp, correo, genérico)
├── ads/AppOpenAdManager.kt → carga y muestra el único anuncio de la app, una vez por proceso
└── ui/theme/Theme.kt        → tema Material 3 (colores LegalScan)
```
