# Hero's Journey

Documentación oficial de la aplicación.
 
Hero's Journey es una aplicación que pretende mejorar la productividad de sus usarios de una manera amable y compasiva, adoptando además mecánicas de gamificación propias de los videojuegos RPG de fantasía medieval.

- [Guía de instalación y configuración](#guía-de-instalación-y-configuración)  
  - [Requisitos previos](#requisitos-previos)  
  - [Dependencias clave](#dependencias-clave)  
  - [Pasos para configurar](#pasos-para-configurar)  
- [FAQ y resolución de errores comunes](#faq-y-resolución-de-errores-comunes)


## Guía de instalación y configuración

  ### Requisitos previos
  
* Android Studio: versión 2022.3.1 (Arctic Fox) o superior.
* Android SDK:

  * compileSdk: 35
  * minSdk: 24 (Android 7.0 “Nougat”)
  * targetSdk: 34
  * Android SDK Build-Tools: 31 o superior
* JDK: Java 8 (configurado en `compileOptions` y `kotlinOptions.jvmTarget = "1.8"`)

**Dependencias clave (coordenadas Gradle)**

```gradle
// Core y UI
implementation "androidx.core:core-ktx:<versión>"
implementation "androidx.appcompat:appcompat:<versión>"
implementation "com.google.android.material:material:<versión>"
implementation "androidx.constraintlayout:constraintlayout:<versión>"

// Navegación
implementation "androidx.navigation:navigation-fragment-ktx:<versión>"
implementation "androidx.navigation:navigation-ui-ktx:<versión>"

// Ciclo de vida
implementation "androidx.lifecycle:lifecycle-livedata-ktx:<versión>"
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:<versión>"

// Persistencia con Room
implementation "androidx.room:room-runtime:<versión>"
implementation "androidx.room:room-ktx:<versión>"
kapt "androidx.room:room-compiler:<versión>"

// Preferencias
implementation "androidx.preference:preference-ktx:<versión>"

// SplashScreen
implementation "androidx.core:core-splashscreen:<versión>"

// Trabajo en segundo plano
implementation "androidx.work:work-runtime-ktx:<versión>"

// Cliente HTTP y conversión JSON
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.retrofit2:converter-gson:2.9.0"

// Testing
testImplementation "junit:junit:<versión>"
androidTestImplementation "androidx.test.ext:junit:<versión>"
androidTestImplementation "androidx.test.espresso:espresso-core:<versión>"
androidTestImplementation "androidx.room:room-testing:<versión>"
androidTestImplementation "androidx.arch.core:core-testing:<versión>"
```

  
  ### Pasos para configurar
  
  1. Clonar el repositorio:
  
    bash
    Copiar
    Editar
    git clone https://github.com/carloszuilavila/HeroJourney.git
  
  2. Abrir la carpeta en Android Studio:
  
    File > Open → seleccionar el directorio raíz del proyecto.
  
  3. Sincronizar Gradle:
  
    Android Studio lo hará automáticamente; si no, pulsa el icono de “Sync Project with Gradle Files”.
    
  4. Compilar el proyecto:
    
    Build > Make Project (o el atajo ⇧⌘F9 / Ctrl+F9).
  
  5. Ejecutar la app:

    Selecciona un emulador o dispositivo USB (mín. Android 5.0).
  
    Pulsa el ▶️ “Run” en la toolbar.

## FAQ y resolución de errores comunes

La app no compila: 

    Verifica versión de Gradle Plugin y SDK Tools.

LiveData no emite: 

    Asegúrate de observar con el LifecycleOwner correcto (getViewLifecycleOwner()).

Room Migration Error: 

    Borra datos del emulador o incrementa la versión de la base de datos.

Permisos de almacenamiento denegados: 

    No aplica, la app usa solo almacenamiento interno.
