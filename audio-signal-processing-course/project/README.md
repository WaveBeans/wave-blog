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
git clone https://github.com/WaveBeans/wave-blog.git
```

Go to project directory:

```bash
cd wave-blog/audio-signal-processing-course/project
```

And run the following task via gradle *(it's better to use gradle wrapper)* specifying the main class parameter as one of the supported:

```kotlin
./gradlew run -PmainClass="week1.DownsamplingKt"
```

Supported main classes
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

Each output has an extension function defined in `Helpers` file with the name `evaluate()`. It runs the specified output on Local Overseer using WaveBeans framework.