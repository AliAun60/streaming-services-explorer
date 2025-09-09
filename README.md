# Streaming Services Explorer

A Java **Swing** desktop app for exploring a streaming-content catalog across platforms (Netflix, Hulu, Prime Video, Disney+).  
Backed by **MySQL** with clean SQL schema + CSV imports, and a rich UI for title search, filters, genre analytics, and a simple watchlist.

---

## ✨ Features

- **Title search with recommendations cap**  
  Search by show name/description and optionally limit results by recommendations count.

- **Filter by Platform / Type / Year**  
  Query Movies vs TV Shows for a specific platform and release year.

- **Genre lookup & “Genres per Platform over Time”**  
  - Find titles by a given genre.  
  - Analyze which genres appear on a selected platform between a start and end year.

- **Show random titles** (quick browse)

- **Optional columns**  
  Toggle extra columns like **Filming Country**, **Cast**, and **Director**.

- **Watchlist**  
  Add/remove rows from the main results table into a simple in-app watchlist.

- **Robust JDBC code**  
  Uses `PreparedStatement`, safe parameter binding, and a reusable table update routine.

---


## 🛠️ Tech Stack

- **Java 17+** (Swing UI)
- **MySQL 8+** (schema + CSV imports)
- **JDBC** (`mysql-connector-j`)
- CSV data loaders (via SQL `LOAD DATA` or your preferred import tool)

---

## 🗃️ Database Setup

1. Create a database:
   ```sql
   CREATE DATABASE streaming_services;

Run the schema:

-- open MySQL client pointed at streaming_services
SOURCE "create tables.sql";


Import CSVs into the corresponding tables
(MySQL Workbench: Table → Import; or use LOAD DATA INFILE with proper columns/encodings.)


🔧 App Configuration

Open finalProject.java and update credentials:

private static final String JDBC_URL  = "jdbc:mysql://localhost:3306/streaming_services";
private static final String USERNAME  = "root";
private static final String PASSWORD  = "*******";


Security tip: in a public repo, replace hard-coded credentials with environment variables or a local config.properties that is .gitignore’d.


▶️ Build & Run
Option A — Plain javac/java

Download the MySQL driver (e.g., mysql-connector-j-8.4.x.jar).

Compile:

javac -cp .;mysql-connector-j-8.4.x.jar finalProject.java


(macOS/Linux use : instead of ;)

Run:

java -cp .;mysql-connector-j-8.4.x.jar cse385.finalProject
