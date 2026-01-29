# Design System Master File — ErgoGuard

> **Logic:** When building a specific screen, first check `design-system/ergoguard/pages/[screen-name].md`.
> If that file exists, its rules **override** this Master. Otherwise use this file only.
> **Cross-platform:** Android (Jetpack Compose) and iOS (SwiftUI) MUST use the same palette, typography scale, and spacing tokens below.

---

## 1. Color Palette (Single Source of Truth)

Both platforms MUST use these hex values. Android: `Color.kt` + `MaterialTheme.colorScheme`. iOS: `ErgoGuardColors` / `ErgoGuardColors.Dark` (no raw `Color(hex:)` in screens).

### Light theme

| Role                     | Hex       | Android token                       | iOS token                            |
| ------------------------ | --------- | ----------------------------------- | ------------------------------------ |
| Primary                  | `#006874` | `md_theme_light_primary`            | `ErgoGuardColors.primary`            |
| On Primary               | `#FFFFFF` | `md_theme_light_onPrimary`          | `ErgoGuardColors.onPrimary`          |
| Primary Container        | `#97F0FF` | `md_theme_light_primaryContainer`   | `ErgoGuardColors.primaryContainer`   |
| On Primary Container     | `#001F24` | `md_theme_light_onPrimaryContainer` | `ErgoGuardColors.onPrimaryContainer` |
| Secondary                | `#4A6267` | `md_theme_light_secondary`          | `ErgoGuardColors.secondary`          |
| Secondary Container      | `#CDE7EC` | `md_theme_light_secondaryContainer` | `ErgoGuardColors.secondaryContainer` |
| Tertiary (CTA / success) | `#006C4C` | `md_theme_light_tertiary`           | `ErgoGuardColors.tertiary`           |
| Tertiary Container       | `#89F8C6` | `md_theme_light_tertiaryContainer`  | `ErgoGuardColors.tertiaryContainer`  |
| Error                    | `#BA1A1A` | `md_theme_light_error`              | `ErgoGuardColors.error`              |
| Error Container          | `#FFDAD6` | `md_theme_light_errorContainer`     | `ErgoGuardColors.errorContainer`     |
| Warning (severity)       | `#B98200` | hardcoded in Result/History         | `ErgoGuardColors.warning`            |
| Background               | `#F8FDFF` | `md_theme_light_background`         | `ErgoGuardColors.background`         |
| On Background            | `#001F25` | `md_theme_light_onBackground`       | `ErgoGuardColors.onBackground`       |
| Surface                  | `#F8FDFF` | `md_theme_light_surface`            | `ErgoGuardColors.surface`            |
| On Surface               | `#001F25` | `md_theme_light_onSurface`          | `ErgoGuardColors.onSurface`          |
| Surface Variant          | `#DBE4E6` | `md_theme_light_surfaceVariant`     | `ErgoGuardColors.surfaceVariant`     |
| On Surface Variant       | `#3F484A` | `md_theme_light_onSurfaceVariant`   | `ErgoGuardColors.onSurfaceVariant`   |
| Outline                  | `#6F797A` | `md_theme_light_outline`            | `ErgoGuardColors.outline`            |

### Dark theme

| Role               | Hex       | Android                           | iOS                                      |
| ------------------ | --------- | --------------------------------- | ---------------------------------------- |
| Primary            | `#4FD8EB` | `md_theme_dark_primary`           | `ErgoGuardColors.Dark.primary`           |
| Background         | `#001F25` | `md_theme_dark_background`        | `ErgoGuardColors.Dark.background`        |
| On Background      | `#A6EEFF` | `md_theme_dark_onBackground`      | `ErgoGuardColors.Dark.onBackground`      |
| Surface            | `#001F25` | `md_theme_dark_surface`           | `ErgoGuardColors.Dark.surface`           |
| On Surface         | `#A6EEFF` | `md_theme_dark_onSurface`         | `ErgoGuardColors.Dark.onSurface`         |
| Surface Variant    | `#3F484A` | `md_theme_dark_surfaceVariant`    | `ErgoGuardColors.Dark.surfaceVariant`    |
| On Surface Variant | `#BFC8CA` | `md_theme_dark_onSurfaceVariant`  | `ErgoGuardColors.Dark.onSurfaceVariant`  |
| Tertiary           | `#6CDAB6` | `md_theme_dark_tertiary`          | `ErgoGuardColors.Dark.tertiary`          |
| Tertiary Container | `#005138` | `md_theme_dark_tertiaryContainer` | `ErgoGuardColors.Dark.tertiaryContainer` |

**Rule:** Follow system light/dark. Android: `isSystemInDarkTheme()`. iOS: `@Environment(\.colorScheme)` and use `ErgoGuardColors` vs `ErgoGuardColors.Dark`.

---

## 2. Typography scale (align Android ↔ iOS)

Use the same semantic roles and similar sizes. Android uses `MaterialTheme.typography`; iOS MUST use a shared scale (e.g. `ErgoGuardTypography`).

| Role           | Android (sp) | iOS (pt) | Use for                     |
| -------------- | ------------ | -------- | --------------------------- |
| displayLarge   | 57           | 57       | Big numbers (e.g. % result) |
| displayMedium  | 45           | 48       | Secondary big numbers       |
| displaySmall   | 36           | 36       | —                           |
| headlineLarge  | 32           | 28       | Screen titles               |
| headlineMedium | 28           | 24       | Section titles              |
| headlineSmall  | 24           | 22       | —                           |
| titleLarge     | 22           | 22       | Card titles                 |
| titleMedium    | 16           | 17       | Buttons, list titles        |
| titleSmall     | 14           | 14       | Labels                      |
| bodyLarge      | 16           | 17       | Body text                   |
| bodyMedium     | 14           | 15       | Secondary body              |
| bodySmall      | 12           | 13       | Captions                    |
| labelLarge     | 14           | 14       | —                           |
| labelMedium    | 12           | 12       | —                           |
| labelSmall     | 11           | 11       | —                           |

**Rule:** No arbitrary `.font(.system(size: X))`; use tokens so both platforms stay in sync.

---

## 3. Spacing (ui-ux-pro-max aligned)

| Token    | Value | Usage                          |
| -------- | ----- | ------------------------------ |
| spaceXs  | 4     | Tight gaps                     |
| spaceSm  | 8     | Icon gaps, chips               |
| spaceMd  | 16    | Standard padding, list padding |
| spaceLg  | 24    | Section spacing                |
| spaceXl  | 32    | Large gaps, screen padding     |
| space2xl | 48    | Section margins                |
| space3xl | 64    | Hero / empty state             |

**Android:** Prefer `x.dp` with these values (e.g. `16.dp`, `24.dp`).
**iOS:** Prefer same values in points (e.g. `16`, `24`).

---

## 4. Component rules (both platforms)

- **Buttons:** Primary = tertiary (green) or primary (cyan) per design. Padding vertical ~12–14, horizontal ~20–24. Corner radius 8–12. Transitions 150–300 ms.
- **Cards:** Background = surface or surfaceVariant. Corner radius 12–16. Elevation/shadow: subtle (Android `2.dp` / iOS equivalent). Padding 16–24.
- **Icons:** One icon set only (Material Icons Android, SF Symbols iOS). No emoji as icons.
- **Camera screen:** Status bar transparent; overlay controls may use black/white for contrast. Other screens: status bar color = background; follow system light/dark.

---

## 5. Pre-delivery checklist (ui-ux-pro-max)

- [ ] No emojis as icons (use SVG / SF Symbols / Material Icons).
- [ ] All interactive elements have clear tap/hover feedback.
- [ ] Transitions 150–300 ms.
- [ ] Light mode: text contrast ≥ 4.5:1.
- [ ] Focus states visible for a11y.
- [ ] Respect `prefers-reduced-motion` where applicable.
- [ ] Android & iOS use **theme tokens only** (no raw hex in screens except camera overlay).
- [ ] Typography uses **scale tokens** on both platforms.

---

## 6. Anti-patterns (do not use)

- Raw hex in screen UI (use `MaterialTheme.colorScheme` / `ErgoGuardColors`).
- Arbitrary font sizes (use typography scale).
- Emoji as icons.
- Missing status bar handling (transparent for camera; background for others; follow system dark/light).
