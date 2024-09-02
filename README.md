# AI-Based_Personalized_Calendar(Final year project) 

## Introduction
This project developed an Android mobile AI-based calendar application that enables users to manage their events through AI technology better and provide customized analysis.

## System Design
### 3.1 Calendar UI and Event Management
The Android mobile app's calendar interface is designed to be user-friendly, featuring a simple and clean layout. Function icons are intuitive and closely related to their respective functions, minimizing the learning curve for users. The app supports essential event management features, including creating, searching, updating, deleting events, and setting event alarms. Events are stored in a local database containing key entities such as event ID (PK), title, start time, end time, and category, allowing users to access their calendars anytime.

### 3.2 Chat Box
To streamline event management and reduce user effort, a chat box is integrated into the app. This chat box communicates with users by receiving messages and passing them to the Dialogflow server via API (Google Cloud). After processing, Dialogflow returns responses that the chat box uses to guide users and execute calendar management actions, acting as a bridge between users and the event management system.

![image](https://github.com/user-attachments/assets/6548428c-3da0-4c8d-a678-27448b15b4cf)

### 3.2.1 User Flow
When users open the app and interact with the chat box, they can input commands such as “add meeting at 7:00 PM,” “delete the meeting,” or “search events today.” The chat box processes these inputs and returns appropriate messages based on the actions taken. Users can then review their calendars at any time from the home page.

![image](https://github.com/user-attachments/assets/4fdcb9e2-0c03-4209-a257-c5846a9c80d6)


### 3.3 AI Detection
Upon receiving messages from the chat box, the **Dialogflow** server processes them using **rule-based grammar and machine learning algorithms**. Different intents are created to recognize actions like creating, searching, updating, and deleting events. Various patterns and structured sentences are employed for training, enabling the system to extract entity information and return the relevant actions to the chat box.

![image](https://github.com/user-attachments/assets/99c036e9-6007-49b5-8c39-c04db908e87f)


### 3.4 Statistical Result
With the local database in place, users can select specific periods to review their event patterns. This data is visualized in a pie chart, categorized by event type, helping users gain insights into their activities and improve their daily lives.

![image](https://github.com/user-attachments/assets/05769f2e-a2a1-4b72-80aa-b2f43628b220)


## Install and environment
- Android Studio Giraffe | 2022.3.1 Beta 5 Build
- Virtual device with API 30 or above(Suggest using Pixel 6 Pro 34)
Download the project and open it in Android Studio. After building all files, click "Run".














    


