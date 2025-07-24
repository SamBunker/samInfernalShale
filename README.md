# 🪨 Sam Infernal Shale Mining Script

![Sam Infernal Shale](https://img.shields.io/badge/sam-Infernal_Shale-db0917?style=for-the-badge&logoColor=white)
![Oldschool Runescape](https://img.shields.io/badge/Oldschool-Runescape-6e58ed?style=for-the-badge&logoColor=white)
![Version](https://img.shields.io/badge/version-1.2.3-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-PowBot_SDK-orange?style=for-the-badge)
![Completion](https://img.shields.io/badge/90%25-Complete-32a852?style=for-the-badge&logoColor=white)

> 🚀 **Advanced RuneScape automation script for Infernal Shale mining built on the Powbot SDK**

---

## ✨ Features

### 🎯 **Mining Methods**
- **🎪 3T Mining**: Advanced tick manipulation using wet cloth timing reset for maximum efficiency
  
- **💤 AFK Mining**: Passive approach with minimal interaction requirements

### 🎒 **Smart Management**
- **💎 Automatic Gem Bag Handling**: Smart overflow detection and management
- **⚡ Special Attack Integration**: Enhanced pickaxe support (Dragon/Crystal/Infernal)
- **🔨 Hammer Type Support**: Regular Hammer and Imcando Hammer (off-hand) options
- **📊 Real-time Performance Tracking**: Success rates, timing analysis, and failure tracking

### 💰 **Profit & Analytics**
- **📈 Real-time Profit Tracking**: Via RuneScape Wiki API integration
- **⏱️ Averaged Profit/Hour**: Rolling calculation based on recent performance
- **🎯 Rock Success Metrics**: Mined/Failed/Missed tracking with success percentages
- **🔧 Adaptive Timing System**: Learns from failures to optimize performance

### 🎨 **UI & Experience**
- **🖼️ Dynamic Paint Overlay**: Real-time statistics and task status
- **⚙️ Configuration UI**: Easy setup with dynamic visibility options
- **📱 Responsive Design**: Clean, informative display optimized for different screen sizes

---

## ⚙️ Configuration Options

The script uses `@ScriptConfiguration` annotations for user-friendly setup:

### 🎮 **Mining Method**
Choose your preferred mining approach:
- **3T Mining** - Requires rock selection, wet cloth, and precise timing
- **AFK Mining** - Minimal interaction, no rock selection needed

### 🪨 **Selected Rocks** *(3T Mining only)*
- Interactive rock selection for targeted mining
- Supports multiple rock rotation for efficiency
- Automatically hidden for AFK Mining mode

### 🔨 **Hammer Type**
- **Regular Hammer** - Standard inventory hammer
- **Imcando Hammer (off-hand)** - Special off-hand hammer support

---

## 🚀 Installation & Setup

### Prerequisites
- **Powbot Client** with valid subscription
- **Java Development Kit** (JDK 8 or higher)
- **Gradle** for building the project

### Build Instructions
```bash
# Clone the repository
git clone https://github.com/SamBunker/samInfernalShale.git
cd samInfernalShale

# Build the project
./gradlew build

# Compile Java sources
./gradlew compileJava

# Clean build artifacts (if needed)
./gradlew clean
```

### Required Items
- **Pickaxe** (any type, Dragon/Crystal/Infernal for special attacks)
- **Hammer** or **Imcando Hammer (off-hand)** depending on configuration
- **Chisel** for shale processing
- **Jim's Wet Cloth** (for 3T Mining)
- **Gem Bag** (optional, for automatic gem management)

---

## 🏗️ Architecture Overview

This script follows a **hierarchical task-based system** with event-driven state management:

### Core Components
- **`samInfernalShale.java`**: Main script with event handlers and configuration
- **`TaskManager.java`**: Orchestrates task execution across priority-based lists
- **Task System**: Modular design with Utility → Movement → Inventory → Mining priority

### Task Hierarchy
1. **UtilityTaskList**: High-priority tasks (timing reset, running, special attacks)
2. **MovementTaskList**: Movement and positioning logic
3. **InventoryTaskList**: Gem bag management and cloth handling
4. **MiningTaskList**: Core mining operations (3T, Regular, AFK)

---

## 📅 Changelog

### **July 2025**
- **2025-07-23**: 🔨 Added Imcando Hammer (off-hand) support (#7, #9)
- **2025-07-22**: 📊 Enhanced rock interaction tracking with comprehensive metrics (#5)
- **2025-07-22**: ⚡ Added chiseling while walking optimization for 3T mining
- **2025-07-22**: 💰 Implemented averaged profit per hour calculation system
- **2025-07-22**: 🎯 Added adaptive 3T mining timing system (#2, #3, #4)
- **2025-07-20**: ✨ Implemented crushed shale tracking and special attack fixes
- **2025-07-20**: 🔧 Reorganized command structure and improved task execution

### **June 2025**
- **2025-06-16**: 📖 Created initial README.md
- **2025-06-14**: 🎉 Released version 1.1 with major improvements
- **2025-06-14**: 🔄 Merged collaborative changes from Dan (#1)
- **2025-06-08**: ⚙️ Added dynamic script configuration system
- **2025-06-08**: 💤 Implemented AFK mining mode with UI optimizations
- **2025-06-08**: 📈 Added rocks mined tracker to paint overlay
- **2025-06-08**: ⚡ Enhanced special attack system with sprint controls
- **2025-06-07**: 🎯 Optimized mining speeds and corrected crushing mechanics
- **2025-06-05**: 🔄 Introduced new mining methodology
- **2025-06-03**: 💎 Implemented gem bag checking and tracking system

### **May 2025**
- **2025-05-30**: 🎨 Added PaintBuilder for quality of life improvements
- **2025-05-30**: 🏗️ Complete project foundation and task system creation
- **2025-05-30**: 🎯 Initial project creation and core functionality development

---

## 📊 Performance Metrics

The script tracks and displays comprehensive performance data:

- **⭐ Rock Success Rate**: Percentage of successful mining attempts
- **💰 Real-time Profit**: Current session profit with GE price integration
- **⏱️ Profit/Hour**: Averaged calculation based on recent performance
- **🎯 Timing Statistics**: Success/failure rates for 3T mining precision
- **📈 Mining XP Tracking**: Integrated with Powbot's skill tracking

---

## 🤝 Contributing

This is an open-source project! Contributions are welcome:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

---

## 📄 License

This project is open-source and available under the MIT License.

---

## 🙏 Acknowledgments

- **Powbot SDK** - Providing the automation framework
- **RuneScape Wiki API** - Real-time pricing data
- **Community Contributors** - Collaborative improvements and feedback

---

## 🔗 Links

- **🐛 Report Issues**: [GitHub Issues](https://github.com/SamBunker/samInfernalShale/issues)
- **💬 Discussions**: [GitHub Discussions](https://github.com/SamBunker/samInfernalShale/discussions)
- **📋 Project Board**: [Development Progress](https://github.com/SamBunker/samInfernalShale/projects)

---
<div align="center">

**⭐ Star this repository if you found it helpful!**

*Created with ❤️ by SamBunker*

</div>
