# Fake Location

Android 虚拟定位应用 - 修复崩溃问题并添加中文支持

## 功能特点

- 🎯 手动输入经纬度设置虚拟位置
- 🌍 快速选择全球主要城市
- 🔧 前台服务持续模拟位置
- 🇨🇳 完整中文界面支持

## 系统要求

- Android 6.0+ (API 23+)
- Kotlin + Jetpack Compose
- Gradle 8.x

## 使用说明

### Android 10 及以下

1. 安装 APK
2. 打开应用，授予位置权限
3. 在开发者选项中启用"允许模拟位置"
4. 或者使用 ADB 命令：
   ```bash
   adb shell pm grant com.fakelocation.app android.permission.MOCK_LOCATION
   ```

### Android 11+

由于 Google 从 Android 11 开始移除了第三方应用使用模拟位置的功能，Android 11+ 设备需要：

1. **Root + 系统应用**：将应用安装为系统应用
2. **Xposed/Magisk**：使用 Magisk Hide 或 Xposed 模块

## 构建项目

```bash
# 克隆项目
git clone https://github.com/tinklert/FakeLocation.git
cd FakeLocation

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

## 项目结构

```
├── app/src/main/
│   ├── java/com/fakelocation/
│   │   ├── FakeLocationApp.kt      # Application 类
│   │   ├── MainActivity.kt         # 主 Activity
│   │   ├── location/               # 位置模拟核心代码
│   │   ├── ui/screens/             # UI 屏幕
│   │   └── ui/components/          # UI 组件
│   └── res/
│       ├── values/                 # 英文资源
│       └── values-zh-rCN/          # 中文资源
```

## 技术栈

- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture
- **Location**: Google Play Services Location
- **Service**: Foreground Service

## 许可证

MIT License
