# lm-launcher

A small Swing GUI for managing local GGUF models and launching apps with model-dependent command-line placeholders.

## What it does

- scans a configured models directory for `.gguf` files
- stores per-model settings in `models.json`
- stores app launch definitions in `apps.json`
- launches apps with placeholders resolved from the selected model

## Model settings

Each model can store:

- context length
- max context length
- threads
- GPU layers
- prompt template
- temperature
- top-p
- top-k
- min-p

The main window also provides sliders for quickly adjusting launch-time values for:

- context length
- threads
- GPU layers
- temperature
- top-p
- top-k
- min-p

## App placeholders

Use these tokens in app arguments:

- `MODEL`
- `CONTEXT_SIZE`
- `THREADS`
- `GPU_LAYERS`
- `TEMP`
- `TOP_P`
- `TOP_K`
- `MIN_P`

Example:

```text
-m MODEL -c CONTEXT_SIZE -t THREADS -ngl GPU_LAYERS --temp TEMP --top-p TOP_P --top-k TOP_K --min-p MIN_P
```

## Setup

### lm-launcher.properties

The launcher reads `lm-launcher.properties` on startup. It requires at least the following keys:

| Key | Description | Example |
|-----|-------------|---------|
| `models` | Path to the directory containing GGUF model files (scanned on startup) | `/mnt/data/data/lm/models/gguf/` |
| `console` | Command template to launch a terminal; `CMD` is replaced with the resolved app command | `/usr/bin/konsole --noclose -e CMD` |

Example:

```properties
models=/mnt/data/data/lm/models/gguf/
console=/usr/bin/konsole --noclose -e CMD
```

### Default configuration files

The repository ships with default `lm-launcher.properties`, `models.json`, and `apps.json` files. These are meant as reference examples — copy them and adjust the paths and settings to match your environment. Apps and models can be managed in the GUI.

## Files

- `lm-launcher.properties` – launcher configuration
- `models.json` – saved model settings
- `apps.json` – saved app definitions
- `run.sh` – starts the packaged launcher jar

## Run

```bash
./run.sh
```

To build from source with the bundled Jackson jars:

```bash
javac -cp jackson-annotations-2.17.2.jar:jackson-core-2.17.2.jar:jackson-databind-2.17.2.jar -d /tmp/lm-launcher-build $(find src -name '*.java' | sort)
java -cp /tmp/lm-launcher-build:jackson-annotations-2.17.2.jar:jackson-core-2.17.2.jar:jackson-databind-2.17.2.jar de.extio.lm_launcher.Main
```