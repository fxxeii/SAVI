# Codename One Project

This is a multi-module Maven project for a Codename One app.
You can write the app in Java and/or Kotlin, and build for Android, iOS, desktop, and web.

## Getting Started

Use JDK 17 or newer for this project's selected Java level. Configure your IDE's project SDK and Maven runner, and set JAVA_HOME for terminal builds. An IDE's bundled runtime does not necessarily configure either.

Extract the entire ZIP, then open the root pom.xml as a Maven project. Use a terminal so errors remain visible.

Windows PowerShell or Command Prompt:

```
.\mvnw.cmd -v
.\build.bat javascript_cloud
```

macOS/Linux:

```
./mvnw -v
./build.sh javascript_cloud
```

Check that Maven reports the required JDK. Complete browser login when prompted. The first build downloads dependencies and can take several minutes. If a download fails, check your connection or Maven proxy settings and retry.

The javascript command builds locally; javascript_cloud submits a hosted build. With no target, build makes a local JAR and run starts the local simulator. These local operations do not create a cloud build.

## IntelliJ Users

This project should work in IntelliJ out of the box.
You usually don't need to copy or tweak any project files.

## Signing

Use the Certificate Wizard to configure Apple signing assets, Android keystores, and desktop signing settings:

```
mvn cn1:certificatewizard
```

Generated IDE projects include a Certificate Wizard action under their tools/favorites area.

## Help and Support

- Codename One website: https://www.codenameone.com
- Codename One GitHub: https://github.com/codenameone/CodenameOne
