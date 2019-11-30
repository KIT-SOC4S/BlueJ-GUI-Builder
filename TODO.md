# Stuff needed to do and some explanations
## Informations regarding debugging
Take a look into `<BLUEJ_HOME>\lib\bluej.defs`! This file contains this setting-flag:

    #######################################################################
    ## Debugging. When true, debug output goes to console; when false, it
    ##  is written to a log file in the user's bluej settings directory.
    #######################################################################

    bluej.debug=true

You should definitely set this to true if you are planning on continuing development on this project. Otherwise you won't see the output to `System.out/err` which can be quite frustrating, especially if you couldn't even attach your favourites IDE's debugger onto the BlueJ processes.

## Information regarding the plugin api
BlueJ itself was migrated to JavaFX with version 4.0.0, which was release March 2007. The Plugin API is still from the Swing-UI era, which could create some troubles, although it does its best to wrap everything into JavaFX. It is also [planned](http://bugs.bluej.org/browse/BLUEJ-932) to be revamped in future "to get rid of the old Swing-oriented extension API
in favour of one that allows more JavaFX access", but this feature hasn't a high priority. This could also break the current integration.

New BlueJ versions in general could break its plugins anyway. The library to build plugins is compiled each time BlueJ is and the plugin library "is only guaranteed to work with the version of BlueJ it is shipped with"

I've also included a part in the buildscript which compiles BlueJ and extracts the extension-library. But, as there is no public sourcecode repository of any kind for BlueJ, the download URL would need to be changed each time there is a BlueJ update. So it isn't that promising for CI/CD usage

## About bidirectionality
This is a feature we wanted to implement - unfortunately we came to the conclusion that we can't make it happen.

The Editor itself uses FXML to save its state and generates the code using this FXML. There is no possibility to convert user modified code into fxml again. Not even the reference JavaFX-GUI-Builder "Scene Builder" has this ability.
You would need a parser that parses all java source files where the GUI-Object is accessed and possibly changed. Even then there are far more possibilites in code than in FXML.

The currently used method to modify user changed code is realized by using [java-diff-utils](https://github.com/java-diff-utils/java-diff-utils). There are 2 diffs it creates; one between the modified user code and generated code and another one between generated code of the unmodified fxml and the generated code of the modified fxml. Those patches are always both generated, so in case that one fails to apply the other one will be applied.

## About the branches
At first I thought I'd like to start on the codebase of [gediwes](https://github.com/gediwes/GUI-Builder), but somehow the code wasn't that clean and the commits didn't help much either. So I continued with the `original_project`-branch, which is a fork of the [original code](https://github.com/JavaFX-GUI-Builder/GUI-Builder), so this was the one I was working on. The `master` branch is mainly buildscript-related stuff, which is also done on the `original_project` branch. The fork was quite nice to figure out how to get the original code to compile as it included some eclipse project files.

## Features to implement
- [ ] Multiple Locales (and their selection)
- [ ] Configuration file for teachers, to restrict students abilities inside the GUI-Editor
- [ ] More GUI-Elements
- [ ] Maybe some FXML Editor/Viewer
- [ ] Listener-Style selection (to Lambda or not to)
- [ ] a fully featured Java-Code to FXML converter
- [ ] a fully featured FXML to Java-Code converter
- [ ] if you did those two above, you probably should search some better paid work

## Bugs to fix
See [Bugtracker](https://github.com/KIT-SOC4S/BlueJ-GUI-Builder/issues)