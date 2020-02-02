Audio Processing Course
=====

This is an implementation of tasks of Audio Processing Course on coursera.org using the WaveBeans framework. That implementation cannot replace whatever is done for the sake of completing course, however gives an idea of another implementation of provided tasks using different approach.

Please follow a better explanation in [related articles](../readme.md).

Prerequisites
-----

You need to have JDK 8+ installed on your machine.

Usage
-----

To run these example you need to clone the repository:

```bash
git clone [TODO]
```

and run the following task via gradle *(it's better to use gradle wrapper)* specifying the main class parameter as one of the supported:

```kotlin
gradlew run -PmainClass="week1.DownsamplingKt"
```

Supported main classes
* Week 1
    * `week1.DownsamplingKt` -- downsampling to CSV example.
