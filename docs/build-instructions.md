# Build & Signing Instructions

Instructions to generate the production-ready Android App Bundle (AAB).

## 1. Keystore Configuration
Create a `keystore.properties` file in the root directory (DO NOT COMMIT THIS):
```properties
storePassword=your_password
keyPassword=your_password
keyAlias=your_alias
storeFile=path/to/your/upload-keystore.jks
```

## 2. Generate Release Bundle
Run the following command in the terminal:
```bash
./gradlew bundleRelease
```

## 3. Locate the Artifact
The resulting `.aab` file will be located at:
`app/build/outputs/bundle/release/app-release.aab`

## 4. Testing the Release Build
Use `bundletool` to install the AAB on a test device:
```bash
bundletool build-apks --bundle=app-release.aab --output=app.apks --ks=keystore.jks
bundletool install-apks --apks=app.apks
```
