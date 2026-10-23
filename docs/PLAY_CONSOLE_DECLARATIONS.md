# Google Play Console declarations for sensor-stream sensors

This page lists the Google Play Console declarations you need to update before
you release a build that contains the `lamp.gps1m` and
`lamp.accelerometer{10,20,50,100}hz` sensors to any Google Play track.

Merging into `master` publishes to Google Play, so complete this checklist
before you merge.

## What changed in the app

| Change | Where | Play Console impact |
|---|---|---|
| `LampForegroundService` declares `android:foregroundServiceType="health\|location"` | `app/src/main/AndroidManifest.xml` | Foreground service permissions declaration |
| The service adds the `location` type at runtime when `lamp.gps1m` is configured and the participant granted `ACCESS_FINE_LOCATION` | `ForegroundServiceTypes`, `LampForegroundService.startForegroundWithTypes` | Foreground service and location permissions declarations |
| `lamp.gps1m` requests one high-accuracy fix per minute, also in the background | `Gps1mCollector` | Location permissions declaration, Data safety form |
| High-rate accelerometer collection at up to 100 Hz | `HighRateAccelerometerCollector` | Health apps declaration, Data safety form |
| New permission `android.permission.HIGH_SAMPLING_RATE_SENSORS` | `AndroidManifest.xml` | None (see [High sampling rate sensors](#high-sampling-rate-sensors)) |

The manifest already declared `FOREGROUND_SERVICE_HEALTH`,
`FOREGROUND_SERVICE_LOCATION`, `ACCESS_FINE_LOCATION`,
`ACCESS_COARSE_LOCATION`, and `ACCESS_BACKGROUND_LOCATION` before this change.

## Foreground service types

In Play Console, go to **Policy and programs > App content > Foreground service
permissions**, and declare both types that `LampForegroundService` uses.

### `FOREGROUND_SERVICE_HEALTH`

- **Use case:** Continuous collection of motion-sensor data (accelerometer at 10
  to 100 Hz) for a research study the participant enrolled in. The study team
  configures the sampling rate per participant.
- **Impact if the task is deferred or interrupted:** The study loses motion data
  for the time the service isn't running. The data can't be recollected later,
  so gaps reduce the scientific value of the participant's record. The app
  doesn't lose data it already collected: it stores frames on the device and
  uploads them later.
- **Video:** Record a short video that shows the participant signing in, the
  persistent "active data collection" notification, and the app collecting data
  while it's in the background.

### `FOREGROUND_SERVICE_LOCATION`

- **Use case:** Participant-consented collection of research GPS data, one fix
  per minute (`lamp.gps1m`), for the duration of the study.
- **Impact if the task is deferred or interrupted:** The study loses location
  samples for that period, and mobility features that need regular sampling
  (for example, time at home or distance travelled) become unreliable.
- **Video:** Record a short video that shows the location permission flow, the
  persistent notification, and location collection continuing while the app is
  in the background.

The app requests the `location` type only when the study configured
`lamp.gps1m` and the participant granted `ACCESS_FINE_LOCATION`. If Android
refuses the `location` type (for example, when the service starts from the
background with only while-in-use access), the app falls back to `health` alone.
Background `lamp.gps1m` fixes might then not arrive until the service restarts
with the `location` type.

## Background location

In Play Console, go to **Policy and programs > App content > Location
permissions**. `ACCESS_BACKGROUND_LOCATION` was already declared; keep the
declaration current for the new sensor:

- **Core feature:** Describe research data collection, including the new
  one-fix-per-minute `lamp.gps1m` sensor, as the feature that needs background
  location.
- **Prominent disclosure:** Show an in-app disclosure before the runtime
  location request. It must say that the app collects location data in the
  background, even when the app is closed or not in use, and explain how the
  study uses it. Review the existing disclosure text against the new 1-minute
  rate.
- **Video:** Attach a video that shows the prominent disclosure, the runtime
  permission prompt, and the feature that uses background location.

## Health apps declaration and Data safety form

In Play Console, go to **Policy and programs > App content**:

1. In **Health apps**, confirm that the declaration covers research data
   collection with motion sensors and precise location.
2. In **Data safety**, update the data types:

   | Data type | Collected | Shared | Purpose | Notes |
   |---|---|---|---|---|
   | Location > Precise location | Yes | No | Research (app functionality) | Now up to one fix per minute, including in the background |
   | Health and fitness > Fitness info (motion data) | Yes | No | Research (app functionality) | Accelerometer at up to 100 Hz |

   For both, state that the data is encrypted in transit (HTTPS uploads).
   Confirm the data-deletion answers still match the study's deletion process.

## High sampling rate sensors

`android.permission.HIGH_SAMPLING_RATE_SENSORS` is a normal, install-time
permission. It needs no Play Console declaration, and Android doesn't prompt the
user for it. Declaring it also satisfies one of the prerequisites of the
`health` foreground-service type on Android 14 (API level 34) and higher.

## Distribution outside Google Play

If you distribute the app only through a mobile device management (MDM) system
or a private enterprise channel, Google Play review doesn't apply. Any closed or
open testing track on Google Play still needs every declaration on this page.

## Checklist

- [ ] Foreground service permissions: `health` and `location` declared, each
      with a use case, deferral impact, and video.
- [ ] Location permissions: background location declaration updated, including
      the prominent disclosure and video.
- [ ] Health apps declaration reviewed.
- [ ] Data safety form updated for precise location and motion data.
- [ ] Declarations approved before the build reaches any track.
