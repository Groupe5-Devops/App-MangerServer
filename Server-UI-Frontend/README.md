# Server Management App (Spring Boot & Angular)

## Overview

The Server Management App is a web application built using Spring Boot and Angular, designed to simplify the management of servers in a network environment. It provides an intuitive user interface for monitoring, configuring, and maintaining multiple servers from a centralized location.

## Prerequisites

Before you begin, ensure you have the following installed:

- Java Development Kit (JDK) 8 or higher ( I used Java 20 and Java SE 20)
- Node.js and npm (Node Package Manager)
- Angular CLI (Command Line Interface)

## Installation

1. Clone the repository

```bash
git clone https://github.com/Sddilora/ServerManagement.git
cd ServerManagement
```

2. Run the backend

```bash
cd Backend
.\mvnw.cmd spring-boot:run
```

3. Build and Run the frontend

```bash
cd Frontend
npm install
npm start
```

## Usage

1. Build the database using the provided SQL script in the Backend folder. (I used MySQL Workbench)
2. Provide the database password in the .env file in the Backend folder.
3. Run the Spring Boot backend and Angular frontend as instructed in the Installation section.
4. Access the application in your web browser at <http://localhost:4200>.
5. Explore the various features and functionalities of the application.

## Example .env File (in Backend folder)

### App Screenshots

#### Retrieve All Servers

<h2>List servers :</h2>
<img src="assets/ListServers.PNG">
<hr/>
<h2>Add server :</h2>
<img src="assets/AddServers.PNG">
<hr/>
<p>Server Added :</p>
<img src="assets/Servers-aded.PNG">
<hr/>
<h2>Delete server :</h2>
<img src="assets/Servers-deleted.PNG">
<hr/>
<p>Server Deleted :</p>
<img src="assets/Servers-deleted.PNG">
<hr/>
<h2>Search server By IP:</h2>
<img src="assets/Search-server-by-ip.png">
<hr/>
<h2>Search Server By Name:</h2>
<img src="assets/Search-server-by-Name.PNG">
<hr/>
<h2>Search server By type:</h2>
<img src="assets/Search-server-by-type.PNG">
<hr/>
<h2>Search Server By Status:</h2>
<img src="assets/Search-server-by-status.PNG">
<hr/>
<h2>Search Server doesn't exist:</h2>
<img src="assets/SearchServersNotFound.PNG">
<hr/>
<h2>Ping to Server:</h2>
<img src="assets/Ping-failed.PNG">
<hr/>
<h2>Ping To Server:</h2>
<img src="assets/Ping-success.PNG">
<hr/>
<h2>Install Software:</h2>
<img src="assets/I nstall-Software.PNG">
<hr/>
<h2>Print list Servers :</h2>
<img src="assets/print-list.PNG">
<hr/>
<img src="assets/list-xls.PNG">
#### Error Screen

![Error Screen](https://i.imgur.com/jXVujZA.png)

#### Ping Server

![Ping Server](https://i.imgur.com/NYQYElj.gif)

#### Add Server

![Add Server](https://i.imgur.com/Gpp56GH.png)

#### Server Created Successfully

![Server Created Successfully](https://i.imgur.com/TtVqcJy.png)

#### Delete Server

![Delete Server](https://i.imgur.com/DR3ymOR.gif)

#### Print Report (Press the Print Report Button)

![Print Report](https://i.imgur.com/ZfgrEzg.png)

#### Downloaded Report (XLS)

![Downloaded Report (XLS)](https://i.imgur.com/a1JG4tk.png)

## Contributing

Contributions are welcome! If you wish to contribute to this project, follow these steps:

1. Fork the repository.
2. Create a new branch for your feature/bug fix.
3. Make your changes and commit them with descriptive messages.
4. Push your changes to your branch.
5. Create a pull request detailing your changes and their purpose.

> For major changes, please open an issue first to discuss what you would like to change.

## Contact

For any inquiries or feedback, please email us at <sumeyyedilaradogan@gmail.com> or create an issue in the GitHub repository.
