# Android Barcode Generator

A simple Android barcode generation library and sample application that renders standard barcodes natively without using any external dependencies (like ZXing).

## 📌 Project Overview
This project demonstrates how to encode string data into standard barcode patterns and directly draw them onto an Android `Bitmap` utilizing `Canvas` and `Paint`. It implements the barcode rendering algorithms (bit encoding, checksum calculations, and pattern mappings) completely from scratch in Java.

Recently, the project has been updated to support the modern **Gradle (Android Studio)** build system, abandoning the legacy Eclipse/Ant project structure.

## 📦 Supported Barcode Formats
* **Code 128 (Code Set B):** A high-density barcode capable of encoding the entire ASCII character set. [Wiki](http://en.wikipedia.org/wiki/Code_128)
* **EAN-13 (International Article Number):** A 13-digit standard barcode format primarily used for identifying commercial products worldwide. [Wiki](http://en.wikipedia.org/wiki/International_Article_Number_(EAN))

## ✨ Key Features
- **Zero External Dependencies:** Built purely with native Android/Java APIs for processing strings and drawing Bitmaps.
- **Modern Build System:** Upgraded to use Android Gradle Plugin (AGP) and `.kts` (Kotlin DSL) build scripts.
- **Documented Algorithms:** The core generation algorithms are thoroughly documented with standard English Javadoc to help developers easily grasp how patterns are mapped to bit buffers.
- **Ready to run:** Includes an entry-point `BarCodeActivity` that provides a straightforward example of how to implement the model classes onto a target `ImageView`.

## 📂 Project Structure
```text
android-barcode/
  ├── app/
  │    ├── build.gradle.kts           # App-level build configurations
  │    └── src/main/
  │         ├── AndroidManifest.xml
  │         ├── java/com/coolbong/barcodegenerator/
  │         │    ├── BarCodeActivity.java       # Sample Entry Point
  │         │    └── model/
  │         │         ├── Code128.java          # Code 128 Generator logic
  │         │         ├── Code128Constant.java  # Weight values for Code 128
  │         │         ├── EAN13.java            # EAN-13 Generator logic
  │         │         └── EAN13Constant.java    # Pattern values for EAN-13
  │         └── res/                            # Android UI layouts & resources
  ├── build.gradle.kts                # Project-level build configuration
  └── settings.gradle.kts             # Gradle Settings
```

## 🚀 How to Build and Run
1. Open **Android Studio**.
2. Select **File > Open...** and navigate to this project's root folder (`android-barcode`).
3. Allow Gradle to sync.
4. Click **Run 'app'** to launch the sample app on an emulator or a physical Android device.
