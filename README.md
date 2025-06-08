# PlotSquaredExpansion

A modular feature expansion plugin for PlotSquared v7-Premium that provides an intuitive GUI interface for managing plot flags and settings.

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=java&logoColor=white)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green?style=flat-square)
![PlotSquared](https://img.shields.io/badge/PlotSquared-7.3.8+-blue?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

## 🚀 Features

### 📋 Interactive GUI Management
- **Clean, intuitive interface** for managing plot flags
- **Separate panels** for boolean and custom flags
- **Real-time flag status** with visual indicators
- **Search functionality** to quickly find specific flags
- **Pagination support** for large flag lists

### 🎛️ Flag Management
- **67+ Boolean Flags** - Toggle on/off features like PvP, fly, explosions, etc.
- **23+ Custom Flags** - Set values for greetings, time, weather, gamemodes, etc.
- **Live preview** of current flag values
- **Batch operations** support
- **Permission-based access** control

### 🔧 Configuration
- **Highly customizable** GUI appearance and behavior
- **Multi-language support** with custom message files
- **Flexible permission system**
- **Auto-refresh** and **timeout** settings
- **Admin bypass** options

### 🔍 Search & Filter
- **Smart search** across all flag types
- **Category-based filtering**
- **Real-time results** with instant GUI updates
- **Mixed search results** handling

## 📦 Installation

### Prerequisites
- **Minecraft Server**: 1.20.4+ (Paper/Spigot)
- **Java**: Version 17 or higher
- **PlotSquared**: Version 7.3.8+ (Premium)

### Download & Install
1. Download the latest `PlotSquaredExpansion-X.X.X.jar` from [Releases](https://github.com/f1shyondrugs/PlotsquaredExpansion/releases)
2. Place the JAR file in your server's `plugins/` directory
3. Restart your server
4. The plugin will generate default configuration files automatically

### Build from Source
```bash
# Clone the repository
git clone https://github.com/yourusername/PlotSquaredExpansion.git
cd PlotSquaredExpansion

# Build with Maven
mvn clean package

# Or use the provided build script (Windows)
build.bat
```

The compiled JAR will be available in the `target/` directory.

## ⚙️ Configuration

### Main Configuration (`config.yml`)
```yaml
# Plugin Settings
plugin:
  debug: false
  check-updates: true

# GUI Settings
gui:
  title: "&9Plot Settings"
  size: 27
  auto-refresh: true
  close-after-change: false

# Chat Input Settings
chat-input:
  timeout: 30
  cancel-words: ["cancel", "stop", "exit"]

# Permission Settings
permissions:
  require-ownership: true
  allow-trusted: false
  admin-bypass: true
```

### Flag Configuration (`flags.yml`)
Customize which flags appear in the GUI by editing the `boolean_flags` and `custom_flags` lists. Flags are displayed in the exact order they appear in the configuration.

### Language Configuration (`lang.yml`)
Full internationalization support - customize all messages, GUI text, and help information in your preferred language.

## 🎮 Usage

### Basic Commands
- `/plot settings` or `/p settings` - Open the plot settings GUI
- Requires being on a plot you own (or have permissions for)

### GUI Navigation
1. **Main Menu**: Choose between Boolean Flags and Custom Flags
2. **Boolean Flags**: Click to toggle on/off (Green = Enabled, Red = Disabled)
3. **Custom Flags**: Click to enter custom values via chat
4. **Search**: Type in chat to filter flags by name
5. **Navigation**: Use arrow buttons to browse multiple pages

### Permission-Based Access
- **Plot Ownership**: Must own the plot (configurable)
- **Trusted Users**: Can be allowed to modify flags (configurable)
- **Admin Bypass**: Admins can modify any plot (configurable)

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `plotsquaredexpansion.settings` | Access to plot settings GUI | `true` |
| `plotsquaredexpansion.admin` | Administrative permissions & bypass | `op` |

## 📋 Supported Flags

### Boolean Flags (67+)
Toggle-based flags including:
- **Combat**: `pvp`, `pve`, `explosion`, `projectiles`
- **Movement**: `fly`, `deny-exit`, `deny-portals`
- **Environment**: `weather`, `time`, `leaf-decay`, `crop-grow`
- **Interaction**: `device-interact`, `player-interact`, `villager-interact`
- **Protection**: `block-burn`, `mob-break`, `vehicle-break`
- And many more...

### Custom Flags (23+)
Value-based flags including:
- **Messages**: `greeting`, `farewell`, `description`
- **Limits**: `animal-cap`, `entity-cap`, `mob-cap`
- **Gameplay**: `gamemode`, `time`, `weather`, `music`
- **Commands**: `blocked-cmds`, `titles`
- And more...

## 🔧 Development

### Project Structure
```
src/main/java/com/f1shy312/plotsquaredexpansion/
├── PlotSquaredExpansion.java    # Main plugin class
├── commands/                    # Command handlers
├── gui/                        # GUI management
├── listeners/                  # Event listeners
├── managers/                   # Configuration & message managers
└── utils/                      # Utility classes
```

### Building
- **Maven**: `mvn clean package`
- **Java**: Version 17+
- **Dependencies**: Automatically handled by Maven

### Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 🆘 Support

### Troubleshooting
- **PlotSquared Not Found**: Install PlotSquared v7.3.8+ (Premium version required)
- **Permission Denied**: Check plot ownership or admin permissions
- **GUI Not Opening**: Verify you're standing on a plot
- **Flags Not Saving**: Ensure PlotSquared integration is active

### Getting Help
- **Issues**: Report bugs via [GitHub Issues](../../issues)
- **Questions**: Check existing issues or create a new one
- **Feature Requests**: Welcome via GitHub Issues

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **PlotSquared Team** - For the excellent plot management system
- **Paper/Spigot** - For the server platform
- **Contributors** - Thank you for your contributions!

---

**Author**: Das_F1sHy312  
**Website**: [f1shy312.com](https://f1shy312.com)  
**Version**: 1.0-SNAPSHOT 

**Discord**: f1shyondrugs312  
**E-Mail**: info@f1shy312.com  

 