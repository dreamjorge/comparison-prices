FROM eclipse-temurin:17-jdk-jammy

ARG ANDROID_SDK_ROOT=/opt/android-sdk
ARG GRADLE_VERSION=9.1.0

ENV ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT
ENV ANDROID_HOME=$ANDROID_SDK_ROOT
ENV PATH=$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:/opt/gradle/gradle-$GRADLE_VERSION/bin:$PATH

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl git unzip \
  && rm -rf /var/lib/apt/lists/*

RUN mkdir -p $ANDROID_SDK_ROOT/cmdline-tools \
  && curl -sSLo /tmp/cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
  && unzip -q /tmp/cmdline-tools.zip -d $ANDROID_SDK_ROOT/cmdline-tools \
  && mv $ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools $ANDROID_SDK_ROOT/cmdline-tools/latest \
  && rm /tmp/cmdline-tools.zip

RUN yes | sdkmanager --licenses
RUN sdkmanager --install "platforms;android-35" "build-tools;35.0.0" "platform-tools"

RUN curl -sSLo /tmp/gradle.zip https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
  && unzip -q /tmp/gradle.zip -d /opt/gradle \
  && rm /tmp/gradle.zip

WORKDIR /workspace/app-android
