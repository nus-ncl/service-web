# Security Policy - NCL.SG

This application specific security policy documents mitigations and fixes that addresses high risk security vulnerabilties as part of secure software development lifecycle and practices.

## Jackson Deserialization (jackson-databind 2.8.11.3)

Uses jackson-databind 2.8.11.3 which has 27 polymorphic deserialization CVEs. Not exploitable in this codebase because:
- No `enableDefaultTyping()` or `@JsonTypeInfo` usage
- Centralized ObjectMapper in [`AppConfig.java:42-51`](src/main/java/sg/ncl/AppConfig.java#L42-L51) with security properties
- All deserialization uses concrete types

Scanners still show these CVEs (expected). Jackson upgrade requires Spring Boot 3.x migration.

## Recent Dependency Upgrades

**Logback 1.1.11 → 1.2.13** (2025-12-29)
- Fixed: JNDI injection RCE (similar to Log4Shell)
- Location: [`build.gradle:110-111`](build.gradle#L110-L111)

**SnakeYAML 1.17 → 1.33** (2025-12-29)
- Fixed: 5 High severity RCE/DoS vulnerabilities
- Minimal impact (app uses .properties, not YAML)
- Location: [`build.gradle:112`](build.gradle#L112)

**Commons-IO 2.5 → 2.7** (2025-12-29)
- Fixed: Path traversal vulnerability
- Location: [`build.gradle:107`](build.gradle#L107)

**Gradle 6.8.3 → 7.2** (2026-01-07)
- Removed deprecated JCenter repository
- Location: [`build.gradle:211`](build.gradle#L211)

## Security Scanning

Run Grype scanner:
```bash
./gradlew securityScan
```

Current status (2026-01-07):
- 108 total CVEs (17 Critical, 54 High, 32 Medium, 5 Low)
- Majority are jackson-databind CVEs (mitigated via config)

## Update History

| Date | Change |
|------|--------|
| 2026-01-07 | Gradle 6.8.3 → 7.2 |
| 2025-12-29 | Logback, SnakeYAML, Commons-IO upgrades |
| 2025-12-26 | Jackson security configuration |

---
Last updated: 2026-01-07
