# The Wallflower

**The Wallflower** turns a spare Android phone into a colorful wireless status screen for your Windows PC. Peek at the machine from across the room and see whether it’s calmly writing a document or auditioning to become a space heater.

It refreshes every 2.5 seconds and shows CPU, memory, graphics, storage, network activity, active displays, visible app/window titles, split-screen layout, and the busiest processes. Your old phone finally has a job title.

## Who is it for?

- **The “I’ll just grab a coffee” crowd:** glance back at your PC without doing the office chair swivel. Wallflower shows the active app and whether the screen is split between two windows.
- **IT and office support:** check the health and visible desktop state of a workstation you’re authorized to support, from another device on the same Wi-Fi. Handy for “what’s running over there?” moments.
- **Managers and executives:** get a quick status view of an authorized PC. Wallflower is a single-computer dashboard, not fleet-management software.

A little clarity for everyone: the screen panel reads window names, titles, and placement. It does not capture screenshots, keystrokes, browsing history, or anyone’s physical movements. Use it transparently on computers you own or are authorized to manage, and tell people what the dashboard displays. No secret-agent soundtrack included.

## What you need

- A Windows PC with Node.js 18 or newer
- An Android phone on the same private Wi-Fi network as the PC
- Android Studio (or Java 17, Android SDK 35, and Gradle 8.13) to build the Android app locally

## Start the PC server

1. Download or clone this repository.
2. In PowerShell, run:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\server\start-windows.ps1
   ```

3. The script starts the local telemetry server and shows the PC's Wi-Fi address, such as `http://192.168.1.20:8765`. If Windows Firewall asks, allow access on **Private networks only**.
4. Keep the PC and phone on the same Wi-Fi. Avoid public or guest Wi-Fi networks that isolate devices.

The server needs no Node packages. It reads Windows CPU, process, disk, GPU, NPU, and adapter data locally, then serves the dashboard on port 8765.

## Install the Android app

### Download a ready-to-install APK

GitHub Actions builds a debug APK on every push. Download the `The-Wallflower-debug-apk` artifact from the repository's Actions page. A tagged release (for example, `v1.0.0`) also publishes the APK in GitHub Releases.

On Android, open the downloaded APK and approve the one-time install-from-this-source prompt if Android asks. Then open **The Wallflower**, enter the PC address shown by the server, and tap **Connect**.

### Build it yourself

Open the `android` folder in Android Studio and build the `app` debug variant, or run from that folder with JDK 17 and Gradle 8.13:

```
 gradle assembleDebug
```

The APK is written to `android/app/build/outputs/apk/debug/app-debug.apk`.

## What the app reports

- **CPU:** overall load, processor name, logical processor count
- **Memory:** system RAM in use and total
- **GPU:** installed adapters and adapter memory capacity; the load tile combines Windows 3D-engine counters across adapters
- **NPU:** reports detected hardware; live NPU percent is shown only if a driver exposes it (many drivers do not)
- **Storage:** used and free space for local Windows volumes
- **Network:** active adapter, link speed, live receive and send rates
- **Apps:** top processes by CPU and RAM

Hardware counters depend on Windows and the installed drivers. Unsupported readings are omitted or marked unavailable rather than fabricated.

## Creator attribution integrity

A hidden **Made by Animesh Dey** attribution is stored in the Android resources and the PC server metadata. The Android app and server verify its SHA-256 seal at startup; changing the attribution without updating the seal stops that component from running. The web page also carries a hidden creator marker that the server checks.

This is a tamper check for ordinary edits, not an unchangeable signature. Because the project is open source, someone with the source can also edit or remove the verification code.

## Network and privacy

The PC server binds to the PC's network interfaces so the phone can connect wirelessly. It has no sign-in and is intended for a trusted private LAN. Do not expose port 8765 to the public internet. Telemetry is served from the PC directly to the phone; this project has no analytics or cloud relay.

## License

MIT. See [LICENSE](LICENSE).
