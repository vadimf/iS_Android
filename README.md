# Android Mobile App for Peer-to-Peer (P2P) Communication

This repository contains a native Android application developed by **Globalbit**, enabling **real-time, decentralized communication** between devices using peer-to-peer (P2P) protocols.  
The app is designed for high-resilience environments where direct device-to-device messaging is required — such as field operations, mobility networks, or infrastructure monitoring.

---

## 📱 App Overview

This app allows Android devices to:
- 🔄 Discover nearby peers via Wi-Fi Direct, Bluetooth, or LAN
- 💬 Exchange encrypted messages (text, signals, commands)
- 🛜 Operate **offline or with minimal infrastructure**
- 📡 Optionally sync with a central server for backup or broadcast
- 📁 Share files and structured data between nodes

---

## 🧰 Tech Stack

- **Language**: Kotlin
- **Communication Protocols**: Wi-Fi Direct, Bluetooth, LAN (UDP/TCP)
- **Encryption**: AES-256 / RSA key exchange
- **Architecture**: MVVM + Clean Architecture
- **UI**: Jetpack Compose or XML layouts
- **Optional**: SignalR, WebRTC, Firebase for cloud fallback

---

## 🔐 Security & Performance

- End-to-end encrypted messages
- Peer authentication with public key infrastructure (PKI)
- Adaptive retry logic in case of connectivity loss
- Background service support for persistent communication

---

## 🧩 Use Cases

- 🚔 Emergency & first responder communications
- 🛠️ Field service operations (oil, energy, construction)
- 🚗 Mobility & transportation systems
- 🌍 Remote or off-grid communication apps

---

## 🏗 Built by Globalbit

**Globalbit** is an Israeli software company delivering scalable and secure digital systems for enterprise, defense, healthcare, and government.  
We’ve deployed mission-critical mobile platforms used by **over 200 million users**, including decentralized and real-time communication solutions.

---

## 📎 Getting Started

Clone the repository and open it in Android Studio.  
Follow the setup instructions in `/docs/setup.md` to configure emulator-to-emulator or device-to-device testing.

---

## 📞 Let’s Build Secure, Resilient Communication Together

Globalbit builds robust mobile platforms for challenging environments — from public safety to connected mobility.

📩 [info@globalbit.co.il](mailto:info@globalbit.co.il)  
🌐 [globalbit.co.il](https://globalbit.co.il)
