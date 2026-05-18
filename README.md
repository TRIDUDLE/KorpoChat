# KorpoChat

Platforma komunikacyjna dla pracowników korporacyjnych, zbudowana w architekturze mikroserwisów z wykorzystaniem Java Spring Boot, PostgreSQL i Vanilla JavaScript.

## O projekcie

KorpoChat to webowa aplikacja umożliwiająca pracownikom wymianę wiadomości w kanałach tematycznych, zarządzanie tagami użytkowników oraz administrowanie kontami pracowników.

## Główne funkcjonalności

### Dla użytkowników
- **Czat w kanałach** - komunikacja w dedykowanych kanałach tematycznych
- **System tagów** - organizowanie użytkowników za pomocą tagów (np. HR, IT, Marketing)
- **Zarządzanie kontem** - logowanie i wylogowanie
- **Interfejs responsywny** - optymalizacja dla urządzeń mobilnych i desktopowych
- **WebSocket** - komunikacja w czasie rzeczywistym

### Dla administratorów
- **Panel administratora** - zarządzanie całą platformą
- **Zarządzanie użytkownikami** - tworzenie, edycja i usuwanie kont
- **Zarządzanie rolami** - przydzielanie ról (User, Admin)
- **Zarządzanie tagami** - tworzenie i organizowanie tagów dla użytkowników
- **Monitoring użytkowników** - podgląd statusu i ostatniej aktywności

## Architektura

Projekt wykorzystuje architekturę trójwarstwową z konteneryzacją Docker:

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend (Nginx)                       │
│              Vanilla JavaScript + HTML + CSS             │
│                    Port: 80                              │
└──────────────────────────┬──────────────────────────────┘
        ┌──────────────────┴──────────────────┐
        │                                     │
┌───────▼────────────────────────┐  ┌────────▼──────────────┐
│   Backend (Spring Boot 4.0.3)  │  │  Database PostgreSQL  │
│   - REST API                   │  │  Port: 5432           │
│   - WebSocket (STOMP)          │  │  Alpine 15            │
│   - JPA/Hibernate              │  │                       │
│   Port: 8080                   │  │                       │
│   Java 21                      │  │                       │
└────────────────────────────────┘  └───────────────────────┘
```

### Komponenty

| Komponent | Technologia | Port | Rola |
|-----------|------------|------|------|
| Frontend | Nginx Alpine | 80 | Serwowanie interfejsu użytkownika |
| Backend | Spring Boot 4.0.3 | 8080 | API i WebSocket |
| Database | PostgreSQL 15 Alpine | 5432 | Przechowywanie danych |

## Wymagania systemowe

### Dla uruchomienia z Docker
- Docker 20.10+
- Docker Compose 2.0+

### Dla lokalnego rozwoju
- Java 21 JDK
- Maven 3.8+
- PostgreSQL 15+
- Node.js 18+ (opcjonalnie, do narzędzi deweloperskich)
- Git 2.0+

## Instalacja

### Krok 1: Klonowanie repozytorium

```bash
git clone https://github.com/TRIDUDLE/KorpoChat.git
cd KorpoChat
```

### Krok 2: Przygotowanie zmiennych środowiskowych

Skopiuj plik `.env.example` do `.env` i uzupełnij wymagane zmienne:

```bash
cp .env.example .env
```

## Konfiguracja

### Zmienne środowiskowe (.env)

```env
# Database Configuration
DB_USER=admin
DB_PASSWORD=twoje_bezpieczne_hasło
DB_NAME=korpochat

```
### Z Docker Compose (rekomendowana metoda)

```bash
# Budowanie i uruchomienie wszystkich kontenerów
docker-compose up -d

# Sprawdzenie statusu usług
docker-compose ps

# Wyświetlenie logów
docker-compose logs -f

# Zatrzymanie usług
docker-compose down
```

Po uruchomieniu aplikacja będzie dostępna pod adresami:
- Frontend: http://localhost
- Backend API: http://localhost:8080
- Database: localhost:5432

## Struktura projektu

```
KorpoChat/
├── backend/                 # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/korpochat/
│   │   │   │       ├── controller/     # REST Controllers
│   │   │   │       ├── service/        # Business Logic
│   │   │   │       ├── entity/         # JPA Entities
│   │   │   │       ├── repository/     # Data Access Layer
│   │   │   │       └── config/         # Spring Configuration
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/            # Unit Tests
│   ├── pom.xml              # Maven Configuration
│   └── Dockerfile
│
├── frontend/                # Vanilla JavaScript Frontend
│   ├── index.html          # HTML Entry Point
│   ├── style.css           # Styles
│   ├── js/
│   │   └── app.js          # JavaScript Application
│   └── favicon.ico
│
├── db-init/                # Database Initialization
│   └── init.sql            # SQL Script
│
├── docker-compose.yml      # Docker Compose Configuration
├── nginx.conf              # Nginx Configuration
├── .env.example            # Environment Variables Template
├── .gitignore              # Git Ignore
└── README.md               # This File
```
## Zarządzanie użytkownikami

### Panel Administratora

Administratorzy mogą uzyskać dostęp do panelu zarządzania pod przyciskiem "Panel Administratora" w głównym interfejsie.

### Operacje administracyjne

#### Dodawanie nowego użytkownika
1. Przejdź do panelu administratora
2. Wpisz login nowego pracownika
3. Ustaw hasło początkowe
4. Wybierz rolę (User lub Admin)
5. 5. Przydziel tagi (np. HR, IT) - *Wskazówka: przytrzymaj Ctrl, aby zaznaczyć wiele tagów jednocześnie*.
6. Kliknij "Zarejestruj użytkownika"

#### Zarządzanie tagami
1. W sekcji "Tworzenie nowych tagów" wpisz nazwę tagu
2. Kliknij "Utwórz tag"
3. Tag będzie dostępny przy dodawaniu użytkowników

#### Przeglądanie użytkowników
- Tabela "Lista Użytkowników" pokazuje:
  - Login użytkownika
  - Przydzieloną rolę
  - Przydzielone tagi
  - Aktualny status
  - Czas ostatniej aktywności
  - Opcje edycji i usunięcia

## Wdrożenie

### Wdrożenie na produkcję z Docker Compose

```bash
# 1. Clone repository
git clone https://github.com/TRIDUDLE/KorpoChat.git
cd KorpoChat

# 2. Configure environment
nano .env  # Ustaw bezpieczne hasła i inne zmienne

# 3. Build and start
docker-compose -f docker-compose.yml up -d

# 4. Verify services
docker-compose ps

# 5. View logs
docker-compose logs backend
```

### Rekomendacje bezpieczeństwa

- **Zmienne środowiskowe**: Nigdy nie commituj `.env` - używaj `.env.example`
- **Hasła**: Użyj silnych, losowych haseł do bazy danych
- **SSL/TLS**: Skonfiguruj certyfikaty SSL dla Nginx w produkcji
- **Firewall**: Ogranicz dostęp do portu 5432 (PostgreSQL) do backendu
- **Backup**: Regularnie wykonuj kopie zapasowe bazy danych
- **Aktualizacje**: Regularnie aktualizuj obrazy Docker



> **Nota**: Plik README został napisany przez sztuczną inteligencję (LLM) na podstawie analizy kodu i struktury repozytorium.