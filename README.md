# chess-game
play the chess with computer

## VS Code / Visual Studio में कैसे run करें

> यह project Java Swing पर based है। Main entry point: `Gaming.GameLauncher`

### 1) Required software
- JDK 17 या उससे ऊपर
- VS Code (recommended) + **Extension Pack for Java**
  - Visual Studio (full IDE) में Java support limited/extra setup मांग सकता है, इसलिए VS Code आसान रहेगा।

### 2) VS Code में run करने के steps
1. VS Code खोलें और `chess-game` folder open करें।
2. अगर prompt आए तो Java extensions install करें।
3. `Gaming/GameLauncher.java` file खोलें।
4. `main` method के ऊपर दिखने वाले **Run** button पर click करें।
5. “Game Launcher” window open होगी, जहाँ से Chess/Ludo select कर सकते हैं।

### 3) Terminal से compile + run (VS Code के अंदर भी)
Project root (`/workspace/chess-game`) पर:

```bash
javac Gaming/*.java
java Gaming.GameLauncher
```

### 4) Common issues
- **`javac: command not found`**
  - JDK install नहीं है या PATH में नहीं है।
- **GUI window open नहीं हो रही**
  - Headless/remote environment में Swing UI block हो सकती है। Local machine पर run करें।

## Extra Documentation
- `docs/multimodal-question-option-model.md`: Hindi guide for building a model that extracts questions/options from PDF, DOC, and images in a fixed format.
