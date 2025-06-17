# Production Deployment Guide

Complete guide for deploying PartnerHub in production environment.

## 📚 Table of Contents

- [🚀 Quick Start](#-quick-start)
- [📋 Environment Variables](#-environment-variables)
- [🔒 Security Checklist](#-security-checklist)
- [🐳 Docker Commands](#-docker-commands)
    - [Build](#build)
    - [Start Production](#start-production)
    - [View Logs](#view-logs)
    - [Stop Production](#stop-production)
- [📊 Monitoring](#-monitoring)
    - [Health Check](#health-check)
    - [Metrics](#metrics)
    - [Grafana Dashboards](#grafana-dashboards)
- [🔄 Backup & Maintenance](#-backup--maintenance)
    - [Database Backup](#database-backup)
    - [Log Rotation](#log-rotation)
    - [Updates](#updates)
- [🚨 Troubleshooting](#-troubleshooting)
    - [Common Issues](#common-issues)
- [📞 Support](#-support)

---

## 🚀 Quick Start

### 1. Setup environment

```bash
cp .env.example .env
# Edit .env with your production values
```

### 2. Deploy
```bash
docker build -t partnerhub:latest .
docker-compose -f docker-compose.prod.yml --env-file .env up -d
```

---

## 📋 Environment Variables

| Variable                | Required | Description                 | Example                   |
|-------------------------|:--------:|-----------------------------|---------------------------|
| `DB_USER`               | ✅       | PostgreSQL username         | `partnerhub_user`         |
| `DB_PASS`               | ✅       | PostgreSQL password         | `SecureP@ssw0rd123!`      |
| `DB_NAME`               | ✅       | Database name               | `partnerhub_prod`         |
| `CORS_ORIGINS`          | ✅       | Allowed origins             | `https://app.example.com` |
| `GRAFANA_ADMIN_PASSWORD`| ✅       | Grafana admin password      | `GrafanaAdmin789!`        |

---

## 🔒 Security Checklist

- [ ] Strong passwords (12+ characters, mixed case, numbers, symbols)
- [ ] Different passwords for each service
- [ ] CORS restricted to your actual domains
- [ ] HTTPS URLs in production
- [ ] Database not exposed externally
- [ ] Swagger UI not exposed in production
- [ ] Environment file not committed to git
- [ ] Regular password rotation policy

> [!WARNING]
> **Admin user is created automatically on first deploy with username `admin@admin.com` and password `admin`.  
> Change the password via API or directly in the database after your first production login.**

---

## 🐳 Docker Commands

### Build

```bash
docker build -t partnerhub:latest .
```

### Start Production

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env up -d
```

### View Logs

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f app
```

### Stop Production

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml down
```

---

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

### Grafana Dashboards
- **Application Overview**: HTTP metrics, response times, errors
- **System Overview**: JVM memory, threads, CPU usage
- **Database**: Connection pool, query performance

---

## 🔄 Backup & Maintenance

### Database Backup

```bash
docker exec partnerhub-db-prod pg_dump -U $DB_USER $DB_NAME > backup.sql
```

### Log Rotation

Logs are automatically rotated by Logback configuration based on your current setup:

**Current configuration in [logback-spring.xml](src/main/resources/logback-spring.xml):**

- **Daily rotation**: New file created each day at midnight
- **Retention**: 7 days (logs older than 7 days are automatically deleted)
- **Pattern**: `app.YYYY-MM-DD.log` (e.g., `app.2024-01-15.log`)
- **Location**: `/app/logs/` directory
- **Format**: JSON structured logs (Logstash format)

**Log files you'll see:**

- Current log: `/app/logs/app.log`
- Yesterday's log: `/app/logs/app.2025-06-15.log`
- Day before: `/app/logs/app.2025-06-14.log`
- etc. (up to 7 days)

**Manual log management:**

#### View current log size

```bash
ls -lh /app/logs/
```

#### Check total disk usage

```bash
du -sh /app/logs/
```

#### View recent logs

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f app
```

#### Check specific day log

```bash
tail -f /app/logs/app.2025-06-15.log
```

**Notes:**
- Logs are NOT compressed (no .gz files)
- Only 7 days retention (configurable via `maxHistory`)
- No size limit per file (files grow until daily rotation)
- Automatic cleanup after 7 days

### Updates

#### 1. Build new image

```bash
docker build -t partnerhub:latest .
```

#### 2. Restart with new image

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env up -d
```

---

## 🚨 Troubleshooting

### Common Issues

→ **Database Connection Failed**

#### Check database logs

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs postgres
```

#### Verify environment variables

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml config
```

→ **Memory Issues**

#### Check JVM memory usage

```bash
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

Adjust JVM settings in Dockerfile if needed.

→ **CORS Errors**

Verify `CORS_ORIGINS` in `.env` file matches your frontend URL.

---

## 📞 Support

For issues and questions:
1. Check application logs
2. Verify environment configuration
3. Review health check endpoints
4. Check Grafana dashboards for metrics