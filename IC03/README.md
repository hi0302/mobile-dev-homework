# IC03 — HorizontalScrollView

**Moments** is a standalone Android gallery built with Kotlin and XML Views. It follows the lecture's thumbnail → tap → large ImageView flow, with an iOS-inspired large title, rounded cards, blue selection accents, and light/dark themes.

## Implemented plan

1. Create an independent Android Studio project inside IC03.
2. Put a horizontal LinearLayout inside a real HorizontalScrollView near the top of the screen.
3. Inflate frame_icon_caption.xml for each photo, bind its thumbnail/caption, assign a unique ID and zero-based position tag, then attach a click listener.
4. Update the large ImageView, selected position, counter, border, and checkmark on selection. Keep the selected thumbnail visible.
5. Bundle ten stock photos as separate small/large pairs and use image.png as the launcher icon and header logo.
6. Preserve selection during recreation and verify scrolling, preview replacement, navigation limits, and stock-photo credits on an emulator.

## Included photos and logo

The submission contains **ten stock photos**, each with a small and a large JPEG in `app/src/main/res/drawable-nodpi/`:

| Slot | Thumbnail | Large image | Title |
| --- | --- | --- | --- |
| 01 | small_photo_01.jpg | large_photo_01.jpg | Beyond the pines |
| 02 | small_photo_02.jpg | large_photo_02.jpg | River meadow |
| 03 | small_photo_03.jpg | large_photo_03.jpg | Low tide |
| 04 | small_photo_04.jpg | large_photo_04.jpg | Pebble beach |
| 05 | small_photo_05.jpg | large_photo_05.jpg | Soft horizon |
| 06 | small_photo_06.jpg | large_photo_06.jpg | Hidden falls |
| 07 | small_photo_07.jpg | large_photo_07.jpg | Open water |
| 08 | small_photo_08.jpg | large_photo_08.jpg | The path ahead |
| 09 | small_photo_09.jpg | large_photo_09.jpg | Summer grass |
| 10 | small_photo_10.jpg | large_photo_10.jpg | Forest details |

Photos are by Paul Jarvis on Unsplash, downloaded through Lorem Picsum. Photographer credit appears below each stock preview. Full source links and licensing information are in [STOCK_PHOTO_CREDITS.md](docs/STOCK_PHOTO_CREDITS.md), with source metadata in [stock_photos.json](docs/stock_photos.json). Photos are bundled; the app works offline.

Thumbnails have a longest edge of **240 px** and large images **1600 px**. Both are derived from the same original, preserve aspect ratio, and apply camera orientation before resizing. Both ImageViews use `fitCenter`, so the complete image remains visible without stretching or cropping. Decoding runs off the UI thread with sampling to bound memory usage.

`image.png` is the supplied logo. Its unchanged copy is `app/src/main/res/drawable-nodpi/app_logo.png`. Launcher XML wraps it with a matching yellow background, preserves its 600:315 ratio, and provides legacy and adaptive icons. The same icon appears in the gallery header.

## Lecture mapping

| Lecture element | Implementation |
| --- | --- |
| activity_main.xml | Gallery screen, HorizontalScrollView, and large preview |
| frame_icon_caption.xml | Inflated frame with icon and caption |
| viewgroup / scrollViewgroup | Horizontal LinearLayout containing the frames |
| Small/large image arrays | PhotoCatalog pairs each thumbnail with its large resource |
| Frame IDs and positions | Unique generated ID plus zero-based frame.tag |
| Individual onClick listener | selectPhoto(position) in MainActivity |
| txtMsg | Photo-N · Selected position: X, with X starting at zero |
| imageSelected / showLargeImage | Main ImageView and its image-loading function |

Previous/next buttons stop at the collection ends. TalkBack descriptions, selected states, headings, and 48 dp navigation buttons are provided. Vertical scrolling keeps content reachable on short screens.

## Open and run

1. Extract the ZIP.
2. Android Studio → File → Open → select the extracted **IC03** folder.
3. Set the local Android SDK path if prompted and allow Gradle to sync.
4. Select a phone/emulator running Android 7.0 / API 24 or newer and press Run.

The project matches the repository's HW02 toolchain: AGP 9.3.3, Gradle 9.6.0, compile/target SDK 37, and Java 25 for the Gradle daemon. Kotlin support is built into this AGP version. A fresh machine needs network access for Gradle/dependencies and the appropriate Android SDK.

```powershell
.\gradlew.bat assembleDebug lintDebug
# With a connected phone or emulator:
.\gradlew.bat connectedDebugAndroidTest
```

## Replace or add photos

Replace a matching small/large pair using the same lowercase base names. JPEG, PNG, and WebP work, but keep only one extension per base name. Rebuild after changing resources. Edit `photo_XX_title` in `app/src/main/res/values/strings.xml` to rename a photo.

For new photos, put originals outside Android's res tree and run:

```powershell
python -m pip install Pillow
python tools/prepare_photos.py --source path/to/new-originals --start-slot 11
```

The helper preserves source files and refuses to overwrite existing pairs. Add a matching title string and Slot entry to PhotoCatalog for new numbers, and update test expectations for a different collection size. Intentional resource-name lookup allows missing pairs to fall back to one of four bundled vector illustrations. Resource shrinking is not enabled; lint ignores unused-resource reports only for dynamically loaded photo filenames.

## Submission ZIP

The source-only ZIP contains the project, Kotlin/XML source, tests, Gradle wrapper, ten photo pairs, logo, helper scripts, and credits. It excludes APK/AAB files, build outputs, IDE/Gradle caches, logs, local.properties, downloaded originals, and earlier screenshots. The optimized copies are sufficient to build and run the app.

To recreate the archive from IC03:

```powershell
python tools/package_submission.py
```

The helper writes IC03-submission.zip beside the project and verifies every archived file plus ZIP integrity. Rename the ZIP to your student ID if required by your course.

## Verification

The final ten-photo source compiles successfully with `assembleDebug`, and `lintDebug` passes with no errors. Lint reports three advisories: one about a newer AGP version and two about optional monochrome launcher icons. The project keeps the repository's existing toolchain and the supplied full-color logo.

Five instrumentation tests cover frame inflation, preview changes, recreation/checkmarks, navigation limits, and stock-photo pairs/credits. The tests compiled successfully; the final test run needs a connected phone or emulator. The emulator was no longer connected during source packaging.
