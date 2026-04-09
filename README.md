# Clean-Architecture Plugin

Generate the Clean-Architecture directory structure from Robert C. Martin (Uncle Bob) in your flutter projects.
This project is based on @Tyxz's original idea [Clean Architecture Plugin](https://github.com/Tyxz/clean_architecture_plugin). 

## How To Install
- Android Studio / IntelliJ IDEA -> Plugins -> Browse repositories
- Search: Flutter Clean Architecture Helper
- Install
- Restart IDE

## How To Use
- Right click on or anywhere in your destination folder
- New -> Clean Generator -> Flutter
- Select an **Architecture Style** from the dropdown:
  - **Default Clean Architecture**: `domain/`, `data/`, `presentation/`
  - **Core / UI / Widgets**: `core/`, `data/`, `ui/`, `widgets/`
  - **Custom (from Settings)**: Uses names configured in Settings
- Enter [feature_name] or leave blank to create structure in current directory
- Optional: Use the checkbox to split the data_sources folder into local and remote

## Screenshots

### Flow 1: Custom Architecture (Settings-based)

**1. Open Context Menu**
Right-click on any folder to access the generator:
![context menu](/assets/context_menu.png)

**2. Fill Dialog**
Select "Custom (from Settings)" and enter feature name:
![dialog filled](/assets/dialog_filled.png)

**3. Result - Custom Structure**
Generated using your Settings configuration:
![result custom](/assets/result_custom.png)

**4. Settings Used**
Customize layer names and add directories in Settings:
![settings custom](/assets/settings_default.png)

---

### Flow 2: Preset Architectures

**5. Default Clean Architecture Result**
Standard `domain/`, `data/`, `presentation/` structure:
![result default](/assets/result_default.png)

**6. Core / UI / Widgets Result**
Alternative `core/`, `data/`, `ui/` + top-level `widgets/`:
![result core ui](/assets/result_core_ui.png)

**7. Style Selector**
Choose from the 3 architecture presets:
![dialog with dropdown](/assets/dialog_dropdown.png)

**8. Settings Used**
Customize layer names and add directories in Settings:
![settings custom](/assets/settings_custom.png)

## Architecture Styles

### Default Clean Architecture
```
feature_name/
├── domain/
│   ├── entities/
│   ├── repositories/
│   └── use_cases/
├── data/
│   ├── data_sources/ (or local/ + remote/)
│   ├── models/
│   └── repositories/
└── presentation/
    ├── manager/
    ├── pages/
    └── widgets/
```

### Core / UI / Widgets
```
feature_name/
├── core/
│   ├── entities/
│   ├── repositories/
│   └── use_cases/
├── data/
│   ├── data_sources/ (or local/ + remote/)
│   ├── models/
│   └── repositories/
├── ui/
│   ├── manager/
│   ├── pages/
│   └── widgets/
└── widgets/          # Top-level widgets folder
```

### Custom (from Settings)
Uses layer names and subdirectories configured in `Settings > Tools > Clean Architecture`:
- Rename layers (e.g., `domain` → `core`, `presentation` → `ui`)
- Rename subdirectories (e.g., `use_cases` → `usecases`)
- Add custom directories (e.g., `widgets`, `utils`, `common`)

## Customization

Go to `Settings > Tools > Clean Architecture` to customize:

| Setting | Default | Example Custom |
|---------|---------|---------------|
| Domain layer | `domain` | `core` |
| Data layer | `data` | `data` |
| Presentation layer | `presentation` | `ui` |
| Domain subdirectories | `entities`, `repositories`, `use_cases` | `models`, `repos`, `usecases` |
| Data subdirectories | `data_sources`, `models`, `repositories` | `datasources`, `dtos`, `repos` |
| Presentation subdirectories | `manager`, `pages`, `widgets` | `blocs`, `screens`, `components` |
| Custom directories | *(empty)* | `widgets, utils, common` |

## Source
- https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html
- https://resocoder.com/2019/08/27/flutter-tdd-clean-architecture-course-1-explanation-project-structure/