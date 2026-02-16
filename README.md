# MovementRecorder — Fabric Mod for Minecraft 1.21.11

MovementRecorder is a client-side Fabric mod for Minecraft 1.21.11 that allows you to record and play back player movements. Record your movement, save it to a file, and replay it at any time.

> **Note:** This is a port of the original [Forge 1.8.9 mod](https://github.com/onixiya1337/MovementRecorder) by [Yuro](https://github.com/onixiya1337), rewritten for Fabric 1.21.11.

## Requirements

- Minecraft **1.21.11**
- [Fabric Loader](https://fabricmc.net/) **0.18.4+**
- [Fabric API](https://modrinth.com/mod/fabric-api) **0.141.3+**

## Installation

1. Install the Fabric Loader for Minecraft 1.21.11.
2. Download the latest release JAR from the [Releases](https://github.com/onixiya1337/MovementRecorder/releases) page.
3. Place the JAR file into your Minecraft mods directory (`%appdata%/.minecraft/mods`).
4. Make sure Fabric API is also in the mods directory.

## Usage

### Keybind

- **N** — Stop recording/playback (configurable in Controls settings under the **Misc** category).

### Commands

All commands use the `/movrec` prefix:

| Command | Description |
|---|---|
| `/movrec start <name>` | Start recording movement under the given name |
| `/movrec stop` | Stop recording or playback |
| `/movrec play <name>` | Play back a saved recording |
| `/movrec list` | List all saved recordings |
| `/movrec delete <name>` | Delete a saved recording |
| `/movrec config rotationType <0\|1\|2>` | Set rotation mode: 0 = none, 1 = snap, 2 = smooth |
| `/movrec config removeStartDelay <true\|false>` | Skip idle ticks at the start of playback |
| `/movrec config removeEndDelay <true\|false>` | Skip idle ticks at the end of playback |

### Recordings

Recordings are saved as `.csv` files in `.minecraft/movementrecorder/`. Each tick stores position, rotation, key states, and sprinting/sneaking flags.

## Building from Source

```bash
./gradlew build
```

The output JAR will be in `build/libs/`.

**Requirements:** Java 21, Gradle 9.2+.

## Contributing

Contributions are welcome! Feel free to open an [issue](https://github.com/onixiya1337/MovementRecorder/issues) or submit a [pull request](https://github.com/onixiya1337/MovementRecorder/pulls).

## License

This mod is licensed under the [CC BY-NC-SA 4.0 License](LICENSE).

## Acknowledgments

- [Yuro](https://github.com/onixiya1337) — original Forge 1.8.9 mod
- [sarpedon](https://github.com/sarpedondev) — barebones movement recorder and early development help
- The [Fabric](https://fabricmc.net/) community for the modding toolchain
