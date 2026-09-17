# Performance Notes - NCL.SG

This documents performance issues identified from VPN-based testing and the fixes applied.

## Oversized Homepage Images

Homepage downloaded 33.1 MB per visit (browser-verified), of which 4 images were 31.6 MB / 95%. All four load within ~3.6 s of navigation regardless of carousel state, since `<img>` and inline `background-image` are fetched by the HTML preload scanner even on hidden slides.

Print-resolution originals were being served for thumbnail/slider use:

| File | Before | Displayed at | After | Fix |
|---|---|---|---|---|
| `images/blue_background1.jpg` | 11 MB (6500×4000) | CSS `background-image`, `cover` | 96 KB (1920px wide) | Resized + re-encoded (sips, q78) |
| `images/research17.jpg` | 8.5 MB (4912×4039) | `<img width="350" height="300">` | 52 KB (700px) | Resized + re-encoded (sips, q78), 2x retina for its box |
| `images/teaching1.jpg` | 5.7 MB (3648×5472) | `<img width="350" height="300">` | 76 KB (700px) | Resized + re-encoded (sips, q78) |
| `images/aviation-simulation-animated.gif` | 7.2 MB (900×462, 228 frames, per-frame 256-color tables) | `<img class="img-responsive">` in slider | 215 KB, as `.jpg` (single frame, 900×462) | First replaced with `gifsicle -O3 --lossy=120 --colors=128` (3.7 MB), then superseded by extracting frame 0 as a static JPEG since the animation added little value for the byte cost |
| `images/slider/new1.png` | 204 KB (2842×990) | CSS `background-image`, `cover` | 90 KB, as `.jpg` (1920px wide) | Resizing while keeping PNG made it larger (247 KB, PNG compresses resampled gradients poorly), so resized + converted to JPEG instead |

Combined (first four): 31.6 MB down to ~439 KB (~99% reduction). Location: [`src/main/resources/static/images/`](src/main/resources/static/images/), referenced from [`index.html`](src/main/resources/templates/index.html#L112).

`images/aviation-simulation-animated.gif` was removed and replaced by `images/aviation-simulation.jpg`, with the reference in `index.html` updated accordingly. `images/slider/new1.png` was removed after conversion, with references in `index.html` (3 occurrences) updated to `slider/new1.jpg`. The remaining slider background, `images/NCL_index.jpg` (1366×687, 196 KB), was checked and is already sized correctly for its `cover` use, so it was left as-is.

Originals preserved at `original-images-backup/` (repo root, untracked, not shipped in the build).

## No Response Compression

`server.compression.enabled` was never set. Spring Boot 1.5 defaults it to `false`, so HTML/JS/CSS (~600 KB) were served uncompressed even when the client sent `Accept-Encoding: gzip, br`.

Fixed in [`application.properties`](src/main/resources/application.properties):
```properties
server.compression.enabled=true
server.compression.mime-types=text/html,text/css,text/plain,text/javascript,application/javascript,application/json,image/svg+xml
server.compression.min-response-size=1024
```

## GitHub API Rate Limit on Sub-pages

Sub-pages (`/overview`, `/recent_events`, etc.) fetched content and images from the GitHub Contents API (`api.github.com/repos/nus-ncl/static-web-content/contents/...`), which is:
- Capped at 60 requests/hour, shared across all visitors by source IP. `/recent_events` alone used 54 of the 60 requests per page view
- Base64-encoded (+37% size over the raw file)
- Capped at 1 MB per file (larger files return no content)
- Cached only 60s at the origin, vs. 300s+ for the raw CDN

Fixed in [`main.js`](src/main/resources/static/js/main.js#L1045). `loadStaticPage()` and `loadImage()` now use `raw.githubusercontent.com` instead:
- No rate limit (CDN, not the rate-limited API)
- No base64 inflation. `loadImage()` no longer needs an AJAX round trip at all, the raw URL is set directly as `img.src`

## Known Issues, Require Infrastructure Tweak

**TCP throughput capped at ~300 KB/s per connection at high RTT.** From a 200ms-RTT vantage point, every connection to `ncl.sg` (137.132.84.205) plateaus at ~300 KB/s regardless of connection count, consistent with a fixed ~64 KB receive window and no effective window scaling (64 KB / 0.2s is about 320 KB/s). Other Singapore hosts on the same path reach ~1.9 MB/s at the same RTT. With HTTP/2 multiplexing the whole page onto one connection, this alone costs ~100s to load the homepage from Europe. No `WebMvcConfigurer` or socket-level config exists in this codebase that would explain the cap. Needs investigation on the NUS firewall/load balancer in front of the origin.

**`cache-control: max-age=300` on static assets.** Not set anywhere in this codebase (no resource-handler config in `application.properties` or Java). Something upstream (reverse proxy/LB) is adding it. Needs the same infra-side investigation.

## Update History

| Date | Change |
|------|--------|
| 2026-09-15 | Resized/re-encoded 5 oversized homepage images |
| 2026-09-15 | Enabled `server.compression.enabled` for text assets |
| 2026-09-15 | Switched `loadStaticPage`/`loadImage` from `api.github.com` to `raw.githubusercontent.com` |
| 2026-09-17 | Replaced the animated GIF with a single-frame static JPEG |

---
Last updated: 2026-09-17
