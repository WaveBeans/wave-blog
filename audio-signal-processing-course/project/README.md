Kotlin project reference implementation
=====

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Usage](#usage)
- [How it works](#how-it-works)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

Overview
-----

[GitHub link](https://github.com/WaveBeans/wave-blog/tree/master/audio-signal-processing-course/project)

This is an implementation of tasks of [Audio Signal Processing course on Coursera](https://www.coursera.org/learn/audio-signal-processing) using the WaveBeans framework. That implementation cannot replace whatever is done for the sake of completing course, however gives an idea of another implementation of provided tasks using different approach.

Prerequisites
-----

You need to have JDK 8+ installed on your machine.

Usage
-----

To run these example you need to clone the repository:

```bash
git clone https://github.com/WaveBeans/wave-blog.git
```

Go to project directory:

```bash
cd wave-blog/audio-signal-processing-course/project
```

And run the following task via gradle *(it's better to use gradle wrapper)* specifying the main class parameter from one of the supported:

```kotlin
./gradlew run -PmainClass="week1.DownsamplingKt"
```

Supported main classes:
* Week 1
    * `week1.ReadingWavFileKt` -- reads wav-file and stores part of it as CSV.
    * `week1.UsingSequenceApiKt` -- reads wav-file 
    * `week1.DownsamplingKt` -- reads wav-file, downsamples it and stores to CSV.
* Week 2
    * `week2.GenerateSineKt` -- generates the sinusoid and stores it to CSV.
    * `week2.GenerateComplexSineKt` -- generates the complex sinusoid and stores it to CSV.
    * `week2.DftKt` -- calculates the Digital Fourier Transform over provided signal and stores results into CSV.
    * `week2.IdftKt` -- calculates the Inverse Digital Fourier Transform over provided signal and stores results into CSV.
    * `week2.MagnitudeKt` -- calculates the magnitude based on previous DFT implementation.

How it works
------

Each output has an extension function defined in `Helpers` file with the name `evaluate()`. It runs the specified output on Local Overseer using WaveBeans framework, everything else is up to [documentation](https://wavebeans.io/docs/api/)