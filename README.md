# Chin

---

A library for Minecraft.

---

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.tt432.chin/chin)

## Features

### Collections

Tuple - a heterogeneous list

### Codec

TupleCodec - Codec for Tuple

ChinExtraCodecs - util class for Codec

### Rpc

Chin's rpc using [AspectJ](https://eclipse.dev/aspectj/).

build.gradle:

```groovy
plugins {
    // ...
    id "io.freefair.aspectj.post-compile-weaving" version aspectj_gradle_plugin_version
}

dependencies {
    // ...
    implementation("org.aspectj:aspectjrt:{aspectj_version}")
    aspect implementation("io.github.tt432:chin:{chin_version}")
}
```