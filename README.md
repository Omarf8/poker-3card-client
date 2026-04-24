# 3-Card Poker Client UI

This repository contains the interactive client application for a multi-client 3-Card Poker game. To play, the [server](https://github.com/Omarf8/poker-3card-server) must be running first. Using JavaFX, the client communicates with a local server to facilitate gameplay logic, and the client program serves as the main application to play 3-Card Poker.

## Technical Highlights
* **Network:** Implemented a reliable communication layer with Java Sockets to exchange real-time game data with the server.
* **JavaFX GUI:** Developed an interactive interface using FXML and CSS files, applying event-driven programming for dynamic gameplay and visual updates.
* **Build Process:** Utilized Maven to manage the project's lifecycle, providing a portable and consistent environment.

## Screenshots
Video Preview: [Watch the Demo](https://youtu.be/ovPkozp2bUU)

### Setup
| Server Setup | Client Login |
| :--- | :--- |
| <img width="480" alt="server setup image" src="https://github.com/user-attachments/assets/0cc3832d-d5af-4474-8a5c-e45261d2b6e5" /> | <img width="480" alt="client setup image" src="https://github.com/user-attachments/assets/4098da8c-8aa3-427a-a3be-3ed9b912eb52" /> |
| **Step 1:** Define the port (e.g., 5555). | **Step 1:** Enter Server IP (`127.0.0.1` or `localhost`). |
| **Step 2:** Click 'Start Server'. | **Step 2:** Enter Port and 'Connect'. |

### Gameplay Scenes
| Original Theme | Gold Theme |
| :--- | :--- |
| <img width="480" alt="original cards image" src="https://github.com/user-attachments/assets/4a36e539-69d2-47f7-9543-02b70b4442cb" /> | <img width="480" alt="gold cards image" src="https://github.com/user-attachments/assets/a57d4d0b-fcc6-476e-9b41-1318c99b99de" /> |
| <img width="480" alt="original end screen" src="https://github.com/user-attachments/assets/b9cb3154-667d-469e-9b2a-dcb0d6411ff0" /> | <img width="480" alt="gold end screen" src="https://github.com/user-attachments/assets/06b4dfe0-cdd1-4a2d-b5c0-6f9fca2c0c86" /> |
<!-- <img width="998" height="688" alt="image" src="https://github.com/user-attachments/assets/b9cb3154-667d-469e-9b2a-dcb0d6411ff0" />
<img width="998" height="688" alt="image" src="https://github.com/user-attachments/assets/06b4dfe0-cdd1-4a2d-b5c0-6f9fca2c0c86" /> -->

## How to Install and Run
### Prerequisites
* Java JDK 11: I used Oracle's [Java 11 LTS](https://www.oracle.com/java/technologies/downloads/#java11)
* Maven: Install via [Homebrew](https://brew.sh/)

### Running the Application
1. **Clone the Repo:**
``` text
git clone https://github.com/Omarf8/poker-3card-client.git
cd poker-3card-client
```
2. **Launch the Server:** Ensure the [3-Card Poker Server](https://github.com/Omarf8/poker-3card-server) is running.
3. **Launch the Client:**
``` text
mvn compile exec:java
```
_Maven will automatically resolve JavaFX dependencies and launch the GUI_
