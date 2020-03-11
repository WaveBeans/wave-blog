Week 1. Basics of Audio processing
============

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Overview](#overview)
- [Reading wav-file](#reading-wav-file)
- [Using Sequence API with WaveBeans](#using-sequence-api-with-wavebeans)
- [Simple Downsampling](#simple-downsampling)
- [Conlcusion](#conlcusion)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

Overview
----------

The first week of the course is about basic audio API. And this is what we'll try to cover here as well.

Reading wav-file
-----------

Here you would need to write the function that simply reads the file and outputs some samples out of it. What we can do here within WaveBeans framework is just simply read the file, get the sub-range of its content, and store some samples to the CSV file.

The script will look like this:

```kotlin
wave("file:///path/to/file/here.wav")               // 1
    .rangeProjection(1133, timeUnit = MILLISECONDS) // 2
    .trim(226, MICROSECONDS)                        // 3
    .toCsv("file:///path/to/ouput/file/here.csv")   // 4
```

What is happening there, line by line:
* Line #1. Reading the file from the file system. You need to specify the path to read from, it automatically converts it to a stream. Streams are infinite, so you need to define how to convert a finite stream into an infinite stream, default strategy is extend it with zero samples when the original content is over. More about [wave input](https://wavebeans.io/docs/api/inputs/wav-file.html).
* Line #2. Getting the subrange of the stream. In the course there is number of samples specified, however WaveBeans uses timeline approach instead. The sample rate is defined during processing and the inputs are automatically converted to desired one. So we've just converting number of samples to the time in MILLISECONDS by simple formula: `NUMBER_OF_SAMPLES / SAMPLE_RATE * 1000`, where SAMPLE_RATE=44100Hz, and NUMBER_OF_SAMPLES=50000. We don't define the end of the range, so it's going to be opened, though you may do this. More about [projection operation](https://wavebeans.io/docs/api/operations/projection-operation.html).
* Line #3. Conversion of the stream into a finite stream. It is done to be able to store it to a file, it is also defined in time units (default are milliseconds always). So, you need to convert the number of samples to time using similar formula, but, taking into account that as the number of samples is too small, you may need to use different time unit, in this case it is microseconds. More about [trim operation](https://wavebeans.io/docs/api/operations/trim-operation.html).
* Line #4. The type of the output and the file to make output to. More about [CSV output](https://wavebeans.io/docs/api/outputs/csv-outputs.html)

One thing to point out regarding the output, internally WaveBeans uses 64-bit floating point representation, and input signal is normalized to the range `[-1.0, 1.0]`, and this is what you would see in that file output. More about [Sample type](https://wavebeans.io/docs/api/#sample).

Using Sequence API with WaveBeans
------------

In Python processing you may use regular `numpy` API to work with sample arrays as with simple arrays. WaveBeans approaches such things a little different. Despite the fact it is not very correct, you may use Kotlin Sequence API for that. WaveBeans provides an interface to it which actually is used internally. So the script looks like this:

```kotlin
val M = 2                                                // 1
val hopSamples = wave("file:///path/to/file/here.wav")   // 2
    .trim(1)                                             // 3
    .asSequence(44100.0f)                                // 4
    .windowed(size = M, step = M) { it.last() }          // 5
    .toList()                                            // 6
println(hopSamples)                                      // 7
```

In this example we're trying to read a file, limit its content to 1 milliseconds and then print every second sample. 

**NOTE: that code won't work on distributed environment, but local one can be sufficient for a variety of tasks.**

Line by line explanation:

* Line #1. Defining a variable with value 2. It is the hop size.
* Line #2. Defining an input to read from. It just the easiest way to do it here, but technically it can be anything you want. Also we store the result of the whole execution into `hopSamples` variable, but (important!) only after the terminal action is called. More about [wave input](https://wavebeans.io/docs/api/inputs/wav-file.html).
* Line #3. Limiting the stream with 1 millisecond by trimming the stream and converting it into finite stream. Otherwise, this code would never finish the execution as stream are infinite. More about [trim operation](https://wavebeans.io/docs/api/operations/trim-operation.html).
* Line #4. `asSequence()` is the method to instantiate the stream. It has parameter which tells what sample rate the stream should work with. It automatically propagates to any upstream. Nice thing, that method returns regular [Kotlin Sequence](https://kotlinlang.org/docs/reference/sequences.html) and we may use builtin API to work with it.
* Line #5. To perform a hop we'll use the [window function of Kotlin Sequence API](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/windowed.html). In out case, it basically moves the pointer by M elements forward every time and gets M elements after that pointer. And it lasts unless the stream is over.
* Line #6. `toList()` is a terminal action of the sequence and hence our stream. This is where the whole defined logic starts executing and the results are stored as list. That list will be assigned to `hopSamples` variable when it finishes. It is the list of samples `List<Sample>`.
* Line #7. Printing out the result of our execution.

Simple Downsampling
------------

Here we'll try to use previous idea to downsample the wav file. For that purpose we'll use window and map functions provided by WaveBeans API. But we won't be able to store that value as wav file as that process is called resampling and is provided by different mechanism in WaveBeans which is out of the scope of that task.

Here is how the script may look like:

```kotlin
val downsamplingFactor = 2                                  // 1
wave("file:///path/to/input/file.wav")                      // 2
    .window(downsamplingFactor).map { it.elements.last() }  // 3
    .trim(1)                                                // 4
    .toCsv("file:///path/to/output/file.csv")               // 5

```

Line by line explanation:
* Line #1. Defining a variable with value 2. It is our downsampling factor.
* Line #2. Opening the file which we're going to downsample.
* Line #3. The idea of that downsampling approach is to choose one out of few samples. On that line `window()` function groups samples into a a set of samples which should become one, and `map()` function defines what sample to use instead. The `it` inside the map function has type of `Window<Sample>`, and it has a public property `elements` which is regular [Kotlin List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html).
In the script it's getting the last sample of the window, however you can define a different one, for example:
    * first sample in the group:    
    
        ```kotlin
        /* ... */.map { it.elements.first() }
        ```
    * average of all samples:

        ```kotlin
        /* ... */.map { it.elements.sum() / it.elements.count() }
        ```
    More about [window operation](https://wavebeans.io/docs/api/operations/window-operation.html) and [map operation](https://wavebeans.io/docs/api/operations/map-operation.html)
* Lines #4-5. The last two operations is to limit the amount of output to 1 millisecond and store everything to CSV file. You may try to call [`toMono16bitWav()`](https://wavebeans.io/docs/api/outputs/wav-output.html) instead, but it will store your samples as original sampling rate as it's not proper resampling mechanism for WaveBeans. But you get the idea, right?

Conlcusion
-----------

During that week we've got acquainted with the following topics:
* How to open the wav file using WaveBeans API and start the stream.
* How to do some basic processing over the stream: get the sub-range of the stream, how to define windows and how to work with specific windows or samples using map function. And all this with help of WaveBeans API.
* How to mix WaveBeans API and Kotlin Collection API. Though, not every mix is worth to use.
* How to define some output so your script will start working, and how to interpret it afterwards.

