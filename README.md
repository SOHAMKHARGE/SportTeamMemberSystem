# 🏆 Sports Team Member Entry System

## 📖 Overview
The **Sports Team Member Entry System** is a Java-based desktop application built using **Java Swing** and **MySQL**.  
It enables sports coordinators to **manage players**, **form selection panels**, and **track team compositions** efficiently through an intuitive GUI interface.

This project demonstrates the integration of **Java (Swing GUI)** with a **MySQL database**, covering CRUD operations, relational mapping, and user-level interaction panels.

---

## 🚀 Features

### 👥 Team Member Module
- Add, update, delete, and view team members.
- Stores essential details: Player ID, Name, Sport, and Age.
- Displays all players in a dynamic JTable linked to the database.

### 🧩 Panel Member Module
- Select players to form an official team panel.
- Remove players from the panel if needed.
- View the final selected team with player details.

### 🎯 Player Module
- Allows individual players to view their information.
- Shows selection status (Selected / Not Selected) using Player ID.

---
## 🗄️ Database Structure

### 1. `team_members` Table

| Column Name   | Type               | Description       |
|---------------|--------------------|-------------------|
| player_id     | INT (Primary Key)  | Unique Player ID  |
| name          | VARCHAR(100)       | Player Name       |
| sport         | VARCHAR(50)        | Type of Sport     |
| age           | INT                | Player Age        |

### 2. `panel_selection` Table

| Column Name | Type             | Description                           |
|-------------|------------------|---------------------------------------|
| player_id   | INT (Foreign Key)| References `team_members(player_id)`  |

---
