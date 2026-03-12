# Zava Social — Frontend

**The Java JSP frontend for Zava Social.** ☕

This is the frontend half of a two-app legacy system built for Zava Social. A Java JSP/Servlet web application running on Tomcat, communicating with a .NET ASMX backend via SOAP/XML. Users browse trending shoes, post their kicks, write reviews, and manage profiles.

---

## Quick Start

**Prerequisite:** The backend must be running first. Clone and start [ZavaSocialBackEnd](https://github.com/bradygaster/ZavaSocialBackEnd):

```bash
# In another terminal / directory:
cd ../ZavaSocialBackEnd
docker-compose up --build
```

Then start this frontend:

```bash
docker-compose up --build
```

The worker process is now in a separate [ZavaSocialWorker](https://github.com/bradygaster/ZavaSocialWorker) repository, but `docker-compose` handles running both the frontend and worker together.

| Service | URL |
|---------|-----|
| **Zava Social** (frontend) | [http://localhost:8080](http://localhost:8080) |

The frontend connects to the backend via a shared Docker network (`zava-network`). No Java installation needed — Docker handles everything.

---

## Features

- **Feed** — Browse trending and recent shoe posts
- **Post a Shoe** — Share your kicks with the community
- **Reviews** — Read and write shoe reviews with star ratings
- **Profiles** — View user profiles and their post history
- **Login** — Session-based auth (hardcoded users, no passwords — it's 2007)

### Test Users

Log in as any of these: `sneakerhead`, `kicksqueen`, `solecollector`, `shoefanatic`, `j_runner`, `vintagekicks`, `freshsteps`

---

## Running Without Docker

If you prefer to run the backend natively and this frontend in Docker:

```bash
ZAVA_SERVICE_URL=http://host.docker.internal:8081/ZavaService.asmx docker-compose up --build
```

---

## Tech Stack

- **Java 1.6** — JSPs, Servlets (pre-annotation), JSTL
- **Tomcat 6/8** — Servlet container
- **Maven** — Build tool, WAR packaging
- **SOAP/XML** — Hand-rolled SOAP client (no JAX-WS)
- **Docker** — Multi-stage Maven build → Tomcat deployment

---

## Project Structure

```
ZavaSocialFrontEnd/
├── zava-web/
│   ├── src/main/java/com/zava/
│   │   ├── beans/          # ShoePost, Review, UserProfile
│   │   ├── servlets/       # Feed, Post, Review, Profile, Login
│   │   ├── service/        # ZavaServiceClient (SOAP client)
│   │   └── util/           # XmlHelper, DateFormatter
│   ├── src/main/webapp/
│   │   ├── WEB-INF/views/  # JSP pages
│   │   ├── css/            # zava.css (era-appropriate design)
│   │   ├── images/         # Shoe photos
│   │   └── preview/        # Static HTML mockups
│   ├── Dockerfile          # Multi-stage Maven + Tomcat
│   └── pom.xml
├── docs/
│   ├── ZavaService.wsdl    # SOAP contract (reference copy)
│   └── design-spec.md      # Visual design specification
├── docker-compose.yml      # Runs frontend and worker (from sibling repo)
└── README.md
```
