# Database Setup Guide

This guide explains how to set up a local PostgreSQL database using Docker with automatic schema initialization.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose (usually included with Docker Desktop)
- Access to the project repository

## Overview

We use Docker Compose to run a PostgreSQL container that automatically:
- Creates the database on first startup
- Runs the SQL schema script (`database.sql`) to create all tables
- Persists data in a Docker volume

## Step-by-Step Instructions

### Step 1: Verify Docker is Running

1. Open Docker Desktop application
2. Verify it's running (you should see the Docker icon in your system tray)
3. Open a terminal/command prompt

### Step 2: Navigate to Project Directory

Navigate to the backend main directory where `docker-compose.yml` is located:

```bash
cd backend/main
```

### Step 3: Start the PostgreSQL Container

Start the PostgreSQL container using Docker Compose:

```bash
docker-compose up -d
```

The `-d` flag runs the container in detached mode (in the background).

**What happens:**
- Docker downloads the PostgreSQL 15 Alpine image (if not already present)
- Creates and starts the container named `project3-postgres`
- Creates a Docker volume to persist database data
- Automatically runs the SQL script from `src/datasources/database.sql`
- Database is initialized with all tables and indexes

### Step 4: Verify Container is Running

Check the container status:

```bash
docker-compose ps
```

You should see the `project3-postgres` container with status "Up".

### Step 5 (Optional): Check Initialization Logs

Verify that the SQL script ran successfully:

```bash
docker-compose logs postgres
```

Look for messages like:
- `/docker-entrypoint-initdb.d/01-init.sql` - Script execution
- `CREATE TABLE` - Tables created successfully
- `CREATE INDEX` - Indexes created successfully
- `database system is ready to accept connections` - Database ready

### Step 6: Update Datasource Configuration

Update the datasource configuration to connect to your local Docker database.

Edit `src/datasources/project-3-database.datasource.ts`:

**Option 1: Using Connection URL (Recommended)**

Replace the `config` object with:

```typescript
const config = {
  name: 'project3database',
  connector: 'postgresql',
  url: 'postgresql://admin:admin@localhost:5432/project3database',
  host: 'localhost',
  port: 5432,
  user: 'admin',
  password: 'admin',
  database: 'project3database'
};
```

**Option 2: Using Individual Properties**

You can also use individual connection properties:

```typescript
const config = {
  name: 'project3database',
  connector: 'postgresql',
  host: 'localhost',
  port: 5432,
  user: 'admin',
  password: 'admin',
  database: 'project3database'
};
```

**Note:** If you want to keep the production URL for production deployments, you can use environment variables instead. See "Environment Variables" section below.

### Step 7: Test Database Connection

Test the connection by starting your LoopBack application:

```bash
npm start
```

If successful, the application will start without database connection errors.

Alternatively, test directly with PostgreSQL client:

```bash
docker exec -it project3-postgres psql -U admin -d project3database
```

This opens a PostgreSQL shell. You can run:
- `\dt` - List all tables
- `\q` - Quit

## Database Connection Details

- **Host:** `localhost`
- **Port:** `5432`
- **Database:** `project3database`
- **Username:** `admin`
- **Password:** `admin`

## Useful Docker Commands

### Stop the Container

```bash
docker-compose stop
```

### Start the Container (if already created)

```bash
docker-compose start
```

### Stop and Remove Container (keeps data volume)

```bash
docker-compose down
```

### Stop and Remove Container + Data Volume

⚠️ **Warning:** This will delete all database data!

```bash
docker-compose down -v
```

After this, running `docker-compose up -d` again will recreate everything and re-run the initialization script.

### View Real-time Logs

```bash
docker-compose logs -f postgres
```

### Access PostgreSQL Shell

```bash
docker exec -it project3-postgres psql -U admin -d project3database
```

## Environment Variables (Optional)

For better security and flexibility, you can use environment variables in your datasource configuration:

1. Create a `.env` file in `backend/main/`:

```env
DB_HOST=localhost
DB_PORT=5432
DB_USER=admin
DB_PASSWORD=admin
DB_DATABASE=project3database
```

2. Install `dotenv` package:

```bash
npm install dotenv
```

3. Update `src/datasources/project-3-database.datasource.ts`:

```typescript
import {inject, lifeCycleObserver, LifeCycleObserver} from '@loopback/core';
import {juggler} from '@loopback/repository';
import * as dotenv from 'dotenv';

dotenv.config();

const config = {
  name: 'project3database',
  connector: 'postgresql',
  host: process.env.DB_HOST || 'localhost',
  port: parseInt(process.env.DB_PORT || '5432'),
  user: process.env.DB_USER || 'admin',
  password: process.env.DB_PASSWORD || 'admin',
  database: process.env.DB_DATABASE || 'project3database'
};

// ... rest of the file
```

**Remember to add `.env` to `.gitignore` to avoid committing sensitive credentials!**

## Troubleshooting

### Port Already in Use

If you see an error like "port 5432 is already allocated":

1. Check if another PostgreSQL instance is running:
   ```bash
   docker ps | grep postgres
   ```

2. Either stop the conflicting container, or change the port in `docker-compose.yml`:
   ```yaml
   ports:
     - "5433:5432"  # Use 5433 on host instead
   ```

3. Update your datasource configuration to use the new port.

### SQL Script Didn't Run

If tables are missing:

1. Check the logs: `docker-compose logs postgres`
2. Ensure the SQL file path is correct in `docker-compose.yml`
3. Remove the volume and recreate:
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

### Container Won't Start

1. Check Docker Desktop is running
2. Check logs: `docker-compose logs postgres`
3. Verify `docker-compose.yml` syntax is correct
4. Try removing and recreating:
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

### Connection Refused

1. Verify container is running: `docker-compose ps`
2. Check if port 5432 is accessible: `docker-compose port postgres 5432`
3. Verify datasource configuration matches Docker setup
4. Check firewall settings

## Important Notes

- **Data Persistence:** Data is stored in a Docker volume named `main_postgres_data`. This persists even if you stop/remove the container.

- **Initialization Script:** The SQL script (`database.sql`) only runs on the **first** container creation. To re-run it, you must remove the volume with `docker-compose down -v`.

- **Table Names:** Tables follow LoopBack 4 conventions (lowercase, pluralized): `users`, `stores`, `products`, `admins`, `tests`.

- **Production vs Development:** Consider using environment-based configuration to switch between local Docker database (development) and production database (production).

## Additional Resources

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)
- [LoopBack 4 Database Configuration](https://loopback.io/doc/en/lb4/Database-connectors.html)

