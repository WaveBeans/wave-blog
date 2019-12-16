Audio Signal Processing. Week 1
============

There are four parts inside this week, they rather simple but is very useful for getting used to API.

Part 1
-----------

Here you would need to write the function that simply reads the file and outputs some some sample out of it. What we can do here within WaveBeans framework is just simply read the file, get the range of it, and store it to the CSV. 

The script will look like this:

```kotlin
wave("file:///path/to/file/here.wav")
    .rangeProjection(1133, timeUnit = MILLISECONDS)
    .trim(226, MICROSECONDS)
    .toCsv("file:///path/to/ouput/file/here.csv")
```

What is happening there:
* On 1st line you're reading the file from the file system. You need to specify the path to read from, it automatically converts it to a stream. Streams are infinite infinite, so you need to define how to convert finite stream into infinite stream, default strategy is fill them in with zero samples when the original content is over.
* On 2nd line we're getting the subrange of the stream. In the course we have number of samples, however WaveBeans uses time line instead as the sample rate is defined during processing and the inputs are automatically converted to desired one. So we've just converting number of samples to the time in MILLISECONDS by simple formula: `NUMBER_OF_SAMPLES / SAMPLE_RATE * 1000`, where SAMPLE_RATE=44100Hz, and NUMBER_OF_SAMPLES=50000. We don't define the end of the range, so it's gonna be opened, however you may do this.
* The 3rd line contains the conversion of the stream into a finite stream to be able to store it to a file, it is also defined in time. You need to convert number of samples to time usig similar formula, but taking into account, that as the number of samples is too small, you may need to use different time unit, in this case it micro-seconds.
* The 4th line defines the type of the output and the file to get output from.

NOTE: to use this script with WaveBeans CLI tool, you would need to add `.out()` call at the end.

For complete example and source code, take a look on [`sh` file](part1.sh)

One thing to point out regarding the output, it won't be the same as the lesson provides, as internally WaveBeans uses 64 bit floating point representation, and input signal is normalized to the range `[-1.0, 1.0]`.

Part 2
------------

This part aims to find a minimum and maximum within the stream.

[TODO currently not possible within WaveBeans, should it be?]

Part 3
------------

This part is about using specific API within Python numpy library, not much to cover as it is related to implementation part of Audio Processing which is approach differently within WaveBeans.

However, there is a way to do that, but you'll be using Kotlin Sequence API for that. WaveBeans Streams provide an interface to kotlin Sequence API which is actually is used internally. So the code would look like this:

```kotlin
val M = 2
val hopSamples = wave("file://${INPUT_FILE}")
    .trim(1)
    .asSequence(44100.0f)
    .windowed(size = M, step = M) { it.last() }
    .toList()
println(hopSamples)
```

In this example we're trying to read a file, limit its content to 1 milliseconds and the print every second sample. **NOTE: that code won't work on distributed environment, but loca one can be sufficient for a various tasks.**

Line by line explanation:

1. Defining a variable with value 2. It is gonna be the hop size
2. Defining an input to read from. It just the easiest way to do it here, but technically it can be anything you want. Also we store the result of the whole execution into `hopSamples` variable, but only after the terminal action is called.
3. We're limitting our stream with 1 millisecond by trimming the stream and converting it into finite stream. Otherwise, this code would never finish the execution as stream are infinite.
4. `asSequence()` is the method to instantiate the stream. It has the parameter of sample rate which tell what stream should work with. It automatically propagates to any upstream. The nice thing, that method returns regular [Kotlin Sequence](https://kotlinlang.org/docs/reference/sequences.html) and we may use builtin API to work with it.
5. To perform a hop we'll use the [window function of Kotlin Sequence API](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/windowed.html). It basically moves the pointer by M elements every time and gets M elements after that pointer. And it lasts until the stream is finished.
6. `toList()` is a terminal action of the sequence and hence our stream. This is where the whole defined logic starts executing and the results are stored as list. That list will be assigned to `hopSamples` variable when it finishes.
7. Printing out the result of our execution.

Part 4
------------

The last part is more about reusing the part 3 code for downsampling, however you may use WaveBeans windowing to achieve similar functionality. That doesn't look simple as in the course, but downsampling overall is not a simple problem, that can't be always solved by simple filtering out elements.

[TODO downsampling using windows?]