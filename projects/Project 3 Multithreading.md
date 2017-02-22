PProject 3 Multithreading
=================================================

For this project, you will extend your previous project to support multithreading. You must make a thread-safe inverted index, and use a work queue to build and search an inverted index using multiple threads.

The input and output requirements of this project are identical to the previous project. In addition to the normal testing of your project, you must also compare the execution time of this project to your previous code.

## Functionality ##

For this project, your code must pass all of the previous project requirements and support the following additional functionality:

- Create a custom lock class that allows multiple concurrent read operations, but non-concurrent write and read/write operations.

- Create a thread-safe inverted index using the custom lock class above.

- Use a work queue to build your inverted index from a directory of files using multiple worker threads. Each worker thread should parse a single text file.

- Use a work queue to search your inverted index from a file of multiple word queries. Each worker thread should handle an individual query (which may contain multiple words).

- Exit gracefully without calling `System.exit()` when all of the building and searching operations are complete.

- You may NOT use any of the classes in the `java.util.concurrent` package.

Consider extending your classes from previous projects for this project. Details regarding execution and benchmarking are below.

## Execution ##

Your `main` method must be placed in a class named `Driver`. The `Driver` class should accept the following **additional** command-line arguments:

- `-threads num` where `-threads` indicates the next argument is the number of threads to use. If an invalid number of threads are provided, please default to 5 threads.

  If the `-threads` flag is **not** provided, then assume your project should be single-threaded and should execute _exactly_ as project 2.

The command-line flag/value pairs may be provided in any order. **Your code should support all of the command-line arguments from the previous projects as well.**

:warning: The `Driver` class should be the only file that is not generalized and specific to the project. If the proper command-line arguments are not provided, the `Driver` class should output a user-friendly error message to the console and exit gracefully.

## Output ##

The output of your inverted index should be the same from the previous projects. However, you should only generate output files if the necessary flags are provided.

## Hints ##

It is important to develop the project **iteratively**. One possible breakdown of tasks are:

- Get `log4j2` working and start adding debug messages in your code. Once you are certain a class is working, disable debug messages for that class in your `log4j2.xml` file.

- Consider extending your previous classes to create multithreaded versions. You may need to add additional functionality to your single-threaded versions, but this tends to make the debugging process simpler.

- Create a thread-safe inverted index using the `synchronized` keyword. (Do not worry about efficiency yet.)

- Modify how you build your index to use multithreading and a work queue. Make sure you still pass the unit tests.

- Modify how you search your index to use multithreading and a work queue. Make sure you still pass the unit tests.

- Once you are sure this version works, convert your inverted index to use a custom lock class. Make sure you still pass the unit tests.

- Start worrying about efficiency. Make sure you are not under or over synchronizing, and that your multithreaded code is faster on average than your single-threaded code.

- Test your code in a multitude of settings and with different numbers of threads. Some issues will only come up occasionally, or only on certain systems.

- Test your code with logging enabled, and then test your code with logging completely disabled. Your code will run faster without logging, which sometimes causes some concurrency problems.

- Lastly, do not start on this project until you understand the multithreading code shown in class. If you are stuck on the code shown in class, PLEASE SEEK HELP. You do not need to figure it out on your own—you can ask the CS tutors, the teacher assistant, or the instructor for help.

  :confused: I cannot stress this enough! If you do not understand the simpler examples shown in class, you will get sucked into a black hole of debugging for the project. We are here to help prevent this from happening.

The important part will be to **test your code as you go**. The tests provided only test the entire project as a whole, not the individual parts. You are responsible for testing the individual parts themselves.

:bulb: These hints may or may _not_ be useful depending on your approach. Do not be overly concerned if you do not find these hints helpful for your approach for this project.

## Submission ##

You _must_ pass `ThreadTest.java` to be eligible for code review. You may sign up for code review even if you are not passing `StressTest.java`, but know that this means your code has efficiency issues.

To receive a 100% and pass code review, you must also pass `Project3Test.java` and your project 3 code must be faster than your project 2 code. You can test this on a lab computer using the `benchmark` command as follows:

```
/home/public/cs212/benchmark -u csuser -g gituser r2 r3
 -u specifies your cs username (optional)
 -g specifies your github username (optional)
 r2 specifies the release version of project 2 to test
 r3 specifies the release version of project 3 to test
```

This script will automatically download, compile, and compare your project 2 and project 3 releases.

:bulb: See the **[Project README](../README.md)** for additional details on testing, submission, and code review.
