# PDF Reader by Level Up Foundation

## Overview
**PDF Reader** is an Android application designed to empower visually impaired users by converting PDFs into text and providing a seamless Text-to-Speech (TTS) experience. Users can easily upload PDFs, listen to the text, and navigate through lines using intuitive controls like play, pause, next line, and previous line.

---

## Features

- **PDF to Text Conversion**: Extracts text from PDF files using Google ML Kit.
- **Text-to-Speech Integration**: Reads the extracted text aloud, providing an accessible reading experience.
- **Navigation Controls**:
  - Play and pause text reading.
  - Move to the next or previous line in the text.
- **Local Data Management**: Store and manage PDFs and their text data with Room Database.

---

## Tech Stack

- **Jetpack Compose**: For modern, declarative UI development.
- **Coroutines**: For handling asynchronous operations efficiently.
- **Hilt**: For dependency injection and improved code scalability.
- **Room Database**: For local data persistence.
- **Google ML Kit**: For robust text extraction from PDF files.

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/gauravmitbhu/leveluppdfreader.git
   ```
2. Open the project in Android Studio.
3. Sync the Gradle files.
4. Build and run the project on an Android device or emulator.

---

## How to Use

1. **Upload PDF**: Select a PDF file from your device.
2. **Convert to Text**: The app extracts text using Google ML Kit.
3. **Listen to Text**: Use the play button to start the TTS.
4. **Control Navigation**:
   - **Pause**: Stop the current reading.
   - **Next Line**: Move to the next line of text.
   - **Previous Line**: Go back to the previous line.

---

## Contributing

Contributions are welcome! Follow these steps to contribute:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature-name
   ```
3. Make your changes and commit them:
   ```bash
   git commit -m "Description of changes"
   ```
4. Push to your branch:
   ```bash
   git push origin feature-name
   ```
5. Open a pull request.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Contact

For any inquiries or support, reach out to us at [info@level-up.foundation](mailto:info@level-up.foundation).

---
