# Search Engine Project

Your final project for this class will be a custom search engine. You will work in this project in 5 parts:

  - [Project 1 Inverted Index](projects/Project 1 Inverted Index.md)
  - [Project 2 Partial Search](projects/Project 2 Partial Search.md)
  - [Project 3 Multithreading](projects/Project 3 Multithreading.md)
  - [Project 4 Web Crawler](projects/Project 4 Web Crawler.md)
  - [Project 5 Search Engine](projects/Project 5 Search Engine.md)

For each project, you must pass 100% of the functionality tests **and** pass a one-on-one code review with the instructor before moving on to the next project. Most projects will require more than 1 submission to pass. See below for more details.

## Initial Setup

Students will have a private GitHub repository created by the instructor with the following format:

```
cs212-username-project
```

Replace `username` with your myUSF username.  Initial project files, including input files, expected output files, project specifications, and test files, will be pushed directly to your project repository by the instructor. 

To setup your Eclipse project with these files, use the "Import Projects from Git" feature using the Clone URL for your repository. You may need to enter your GitHub username and password under the "Authentication" settings if you did not setup your SSH keys.

## Local Testing

You should perform your own testing while you develop the project. The test code provided only tests your final result, and may not be suitable for debugging your project during development. It is *highly* recommended that you use some sort of logging to help debug.

To see which test files are relevant for each project, look at the test suites provided in the `Project#Test.java` files. During development, it is recommended you focus only on one of those files at a time. For example, the `Project2Test.java` file references `IndexTest` and `SearchTest`. You must pass all of the tests in those two classes for project 2 to be eligible for code review, but during development you may want to focus primarily on the `SearchTest` class. 

You can run a an individual test by selecting the test method name, right-clicking, and selecting "Run As » JUnit Test" from the popup menu. This is helpful for debugging an individual test that is failing.

You can also compare files side-by-side in Eclipse. Select two relevant files (for example, `expected/index-simple.json` and `output/index-simple.json`) using Control + Click on Windows or Command + Click on Mac. Right-click and select "Compare With » Each Other" in the popup menu.

## Remote Testing

When you are ready to test your code on the lab machines in preparation for code review, you need to [create a release](https://help.github.com/articles/creating-releases/) for your project in Github (not Eclipse). Name the release as `vP.R` where `P` is the project number and `R` is the release number. For example, the first release of project 1 should be `v1.0` and the next release should be `v1.1` if you have to resubmit. For project 2, it will be `v2.0`, then `v2.1`, and so on.   

This basically creates a tag that we will use as a reference point for testing and code review. Every time you want to re-test or re-submit for code review, you must create a new release with your latest changes. See <https://help.github.com/categories/releases/> for more information about releases in Github.

Once you have created the release, run the `project` script on a lab computer as follows:

```
/home/public/cs212/project -g gituser vP.R
```

Replace `gituser` with your Github username and `vP.R` with the release version you created. 

If all of the tests pass, you are ready to sign up for code review. If not, seek help from the instructor, TA, or CS Tutoring Center. See the [Tutoring Center Resources](http://tutoringcenter.cs.usfca.edu/resources/) if you need help logging into a lab computer and using the command-line interface.

:warning: Please note that if you do not follow the naming convention for releases, the test script on the lab computers will fail.

## Code Review

Once you have verified your project is properly submitted and passing the project test script, you may sign up for code review. Each code review is 20 minutes, and will be held in the instructor's office.

### Signup

To sign up for a code review, [create an issue](https://help.github.com/articles/creating-an-issue/) titled `Code Review vP.R` where `P` and `R` are the project and release numbers that you want to test. Next, [set the milestone](https://help.github.com/articles/associating-milestones-with-issues-and-pull-requests/) to `Project #` where `#` is the project number (1, 2, 3, or 4). Finally, [assign the issue](https://help.github.com/articles/assigning-issues-and-pull-requests-to-other-github-users/) to the teacher assistant [@Richard-Wang-USF](https://github.com/Richard-Wang-USF). Include a nice message like "Please verify my code is passing the test script," and click "Submit new issue" when done.

If you are passing the project test script, the TA will provide you additional instructions on how sign up for a code review appointment.

**:warning: If you sign up for code review but you are not passing the `project` script on the lab computers, your project score will be deducted 5 points.**

### Process

During the code review, the instructor will discuss your code design with you and make suggestions for improvement. These will be provided as `// TODO` comments directly in a special `review` branch of your code. *You may remove these comments and close the issues after addressing them.*

After the review, your project will be given a `PASS`, `WARN`, or `FAIL` status:

  - The `PASS` status signifies you passed the code review and may move on to the next project. Congratulations!

  - The `WARN` status signifies you passed the code review and may move on to the next project, but must address some lingering issues before submitting the next project.

  - The `FAIL` status signifies you did not pass the code review, and must resubmit. The resubmission process is outlined below.

  **:persevere: Do not fret!** This is expected to happen at least once per project. If you can pass the first code review, then you may not be in the right class!

:hourglass: Each code review is 20 minutes. If we do not have a chance to review your entire project within this time frame, it is likely you will need another code review. As such, make sure you address any easy fixes such as proper formatting and commenting **prior** to the code review to avoid wasting time.

:warning: Remember to [pull any changes](https://help.github.com/articles/fetching-a-remote#pull) made during code review to your local repository! Otherwise, you will end up with [merge conflicts](https://help.github.com/articles/resolving-a-merge-conflict-from-the-command-line) that may be difficult to resolve.

## Resubmission 

Everyone is expected to resubmit each project 1 to 3 times after their first code review, depending on the project. To resubmit your project:

  1. Make sure you address *all* of the comments by the instructor during the last code review. (You may delete the comments as you address them.)

  2. Make sure you push your latest code to your project repository on GitHub and create a new release.
  
  3. Make sure you re-run the `project` script on the lab computers.

  4. Sign up for another code review appointment using the process outlined above.

:memo: For the most part, project resubmissions will not negatively impact your project grade. However, if you ignore a comment or sign up for code review before you are passing the necessary tests, your final project score will be docked 5 points for each offense.
