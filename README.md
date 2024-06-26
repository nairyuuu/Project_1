# Project 1

## Table of Contents
1. [Description](#description)
2. [Requirements](#requirements)
3. [Technology](#technology)
4. [Setup Instructions](#setup-instructions)
    - [Set up Database](#set-up-database)
    - [Running the Application](#running-the-application)
5. [Screenshots](#screenshots)
6. [Contributors](#contributors)
7. [Troubleshooting](#troubleshooting)

## Description
A JavaFX server-client file transferring system using TCP Socket

## Requirements
Please refer to the [Requirement](Requirement.MD) document.

## Technology
- **Language**: `Java`
- **GUI**: `JavaFX`
- **Database**: `Docker` + `MySQL`
- **Build Tool**: `Maven`
- **Public Localhost Port**: `Ngrok`

## Setup Instructions

### Set up Database

**[!] You have to do this if you want to run client on your own**

#### Docker
- Ensure Docker and Docker Compose are installed on your devices.
- Move the `docker-compose.yml` to the directory you want to start docker on.
- Run the following command in that directory:
```bash
docker-compose up -d
```
- This will:
    - Create a MySQL server accessible at `localhost:3307` to store user credentials
    - Host Adminer (a database manager) at `localhost:8081`
     
- Finally, browse to `localhost:8081` (adminer)
    - Login with username and password init in the `docker-compose.yml`
    - Choose database `FileSenderDB` then run the sql init command in the file `databases.sql`

### Configuration Credentials
1. Set environmental variables for `DB_PASSWORD`
    - On Windows
    ```sh
    set X DB_PASSWORD `your-password`
    ```
    - On Linux/MacOS
    ```sh
    set X DB_PASSWORD `your-password`
    ```
    Restart your device after this step for the change to take effect

2. Update the `src/main/java/org/example/Config.java` file with your database details:
    ```java
    protected static final String DB_USER = "your-username";
    protected static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    ```
- Replace `your-username` and `your-password` with the value you initialize in your `docker-comse.yml`

### Running the Application

#### On Linux/macOS (Terminal)

1. Server
```bash
./Server/run.sh
```
2. Client
```bash
./Client/run.sh
```

#### On Windows

*Prerequisites* Make sure you have the parent folder of sh.exe (including in GitBash) in your PATH

1. Server
```bash
.\Server\run.sh
```
2. Client
```bash
.\Client\run.sh
```

## Screenshot

## Contributor
| Student ID  | Student Name |
| ------------- | ------------- |
| 20225577 | Lê Trần Long  |
| 20225588  | Nguyễn Minh Tú  |

## Troubleshooting
