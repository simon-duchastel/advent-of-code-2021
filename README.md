# advent-of-code-2021

My solutions for Advent of Code 2021. https://adventofcode.com/2021/

Written in Kotlin utilizing Kotlin Multiplatform (although not well 🙂).

All solutions written primarily in Multiplatform Kotlin, accessible via a Kotlin Native command line runner.

The solutions are runnable as a command line tool, currently available as an .exe on Windows machines.

## Using the Command Line Tool - Windows

To build the command line tool for windows, run:
```
gradlew linkReleaseExecutableMingw
```

The output is placed at `\build\bin\mingw\releaseExecutable\AdventOfCode.exe`. To run the tool, simply run `AdventOfCode.exe` on your command line.

The tool will ask you which problem to run. Select your problem by selecting a number from "one" to "twenty four", written out in english. Each problem has sub-commands, asking you to provide various inputs such as which part of the solution you want or the problem input to use (usually from a file).

If at any point you're confused about your options, type "help" to see a list of available commands.
