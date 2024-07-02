# Project 1

## Table of Contents
1. [Description](#description)
2. [Contributors](#contributors)
3. [Requirements](#requirements)
4. [Setup Instructions](#setup-instructions)
    - [Set up Database](#set-up-database)
    - [Running the Application](#running-the-application)

## Description
A JavaFX server-client file transferring system using TCP Socket

## Contributor
| Student ID  | Student Name |
| ------------- | ------------- |
| 20225577 | Lê Trần Long  |
| 20225588  | Nguyễn Minh Tú  |

## Requirements
- Docker and Docker compose
- Java
- Maven
- Have some ports open to the internet

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
    export DB_PASSWORD=`your-password`
    ```
    Restart your device after this step for the change to take effect

2. Update the `src/main/java/org/example/Config.java` file with your database details:
    ```java
    protected static final String DB_USER = "your-username";
    protected static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    ```
- Replace `your-username` and `your-password` with the value you initialize in your `docker-comse.yml`

### Configure client and server
- Update the Config.java file in the source code folder to your IP and port

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

