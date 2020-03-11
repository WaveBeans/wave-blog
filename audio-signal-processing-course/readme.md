Audio Signal Processing Course
======

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Introduction](#introduction)
- [Content](#content)
- [Script reference implementation](#script-reference-implementation)
- [Kotlin project reference implementation](#kotlin-project-reference-implementation)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

Introduction
-----

This is a set of articles aiming to show how you can solve problems brought up during [Audio Signal Processing course on Coursera](https://www.coursera.org/learn/audio-signal-processing) using WaveBeans technology. There is no way you can use it to submit your solution but you can get the idea, and, more importantly, understand how you can solve similar issues within modern framework.

Overall, for beginners, it is recommended to take that course, as it explains in a great way all basis of audio processing.

The set of articles are provided on per week basis. Each lesson describes a problem briefly and how you may approach it using WaveBeans. All lessons can be simply launched locally via [CLI](https://wavebeans.io/docs/cli/readme.html) or [write your own application](https://wavebeans.io/wavebeans/getting_started.html). For convenience, each week has implementation as a script and gradle project.

NOTE: to use this script with WaveBeans CLI tool, you would need to add `.out()` call at the end of each output. Follow [CLI tool docs](https://wavebeans.io/docs/cli/readme.html).

*Disclaimer: all the rights for the course belong to its owners, that set of articles just extends whatever is covered during the course and can't substitute it in any way.*

Content
------

* [Week 1. Basics of audio processing](week1/readme.md)
    * [Bash Script reference implementation](https://github.com/WaveBeans/wave-blog/tree/master/audio-signal-processing-course/week1/scripts)
    * [Kotlin project reference implementaion](https://github.com/WaveBeans/wave-blog/tree/master/audio-signal-processing-course/project/src/main/kotlin/week1)
* [Week 2. Discreate Fourier Transform](week2/readme.md)
    * [Bash Script reference implementation](https://github.com/WaveBeans/wave-blog/tree/master/audio-signal-processing-course/week2/scripts)
    * [Kotlin project reference implementaion](https://github.com/WaveBeans/wave-blog/tree/master/audio-signal-processing-course/project/src/main/kotlin/week2)

Script reference implementation
------

Script is implemented using `zsh` on MacOS X, and may work properly on Windows or Linux. 

To use make sure the [CLI](https://wavebeans.io/docs/cli/readme.html) is installed and environment variable `WAVEBEANS_CLI_HOME` pointing to the its home directory, so the `$WAVEBEANS_CLI_HOME/bin/wavebeans` was pointing to correct binary file. Also make sure the kotlin is installed. Anyway follow the documentation for setting it up.

Kotlin project reference implementation
------

The kotlin project is implemented using gradle. Follow [readme](https://github.com/WaveBeans/wave-blog/blob/audio-signal-processing/audio-signal-processing-course/project/README.md) file in the directory.