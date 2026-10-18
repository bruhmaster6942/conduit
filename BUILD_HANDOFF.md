# Conduit x Anthropic fonts — build handoff

Tree: /opt/conduit (branch `anthropic-fonts`, commits 88a3cfb + da571ef on top of upstream 3f95de1)

## What's in the patch
- assets/fonts/: all 42 Anthropic TTFs (Sans+Serif full weight/italic sets, Mono also downloaded).
- pubspec.yaml: AnthropicSans + AnthropicSerif families registered with weight/italic variants.
- lib/shared/theme/theme_extensions.dart:
  - `fontFamily = 'AnthropicSans'` (Oct tree) → applied via `_primaryFont` to all display/headline/title/body/label getters.
  - NEW `serifFontFamily = 'AnthropicSerif'`; `chatMessageStyle` (and therefore all chat
    bubbles, markdown body, follow-ups, channel messages) now Serif with Sans fallback.
  - NEW: `AppTypography.textTheme()` + `materialChromeTextTheme()` now `.apply()` the sans —
    fixes the Oct gap where `Theme.of(context).textTheme` text stayed system-font.
- .github/workflows/font-apk.yml: dispatchable arm64 APK build; auto self-signed keystore
  when fork has no ANDROID_KEYSTORE_BASE64 secret.
- android/gradle.properties: low-RAM (-Xmx2G, daemon off, workers 2) — Oct proven values.
- android/settings.gradle.kts: forces single NDK 29.0.14206865 for all subprojects.

## Route A — fork + GitHub Actions (needs a PAT, ~25-40 min)
1. Create PAT (repo+workflow scope): https://github.com/settings/tokens
2. `cd /opt/conduit && git push https://<PAT>@github.com/<user>/conduit.git anthropic-fonts:anthropic-fonts`
3. On the fork: enable Actions (Actions tab → enable). Run: Actions → Font APK → Run workflow → branch anthropic-fonts.
4. Download artifact "Font-APK" → app-arm64-v8a-release.apk (~90 MB, self-signed, installable after "allow unknown sources").
   Optional server copy: `scp` APK here, drop in `/var/www/mp3/`, link `https://search.bruhsite.com/mp3/<slug>.apk`.

## Route B — Termux on the Pixel (needs ~6GB free storage, 1-2 h)
- Install: `pkg install openjdk-21 git curl unzip` + Flutter via `git clone https://github.com/flutter/flutter -b stable` (or fvm).
- Fetch bundle: `curl -o /opt/conduit.tar.gz https://search.bruhsite.com/mp3/conduit.tar.gz` (bundle path TBD after upload)
- `tar xzf conduit.tar.gz && cd conduit && flutter pub get && flutter build apk --release --target-platform android-arm64`
- Same codegen + low-RAM gradle.properties already baked in the tree.
- Termux quirks: gradle needs `org.gradle.jvmargs=-Xmx4G` headroom, disable daemon; possible NDK download ~2G.

## What the user sees
- UI everywhere (nav, buttons, settings, inputs): AnthropicSans.
- Chat replies + user bubbles + markdown body: AnthropicSerif (code blocks stay mono).