# BlueJ JavaFX GUI Builder
[![Build Status](https://travis-ci.org/KIT-SOC4S/BlueJ-GUI-Builder.svg?branch=master)](https://travis-ci.org/KIT-SOC4S/BlueJ-GUI-Builder)

This repository contains a GUI-Builder, which was further developed within the SoC4S project to enable and facilitate the development of a GUI for students in the BlueJ development environment.
## Usage
Build using

    gradle fatJar

This will produce a ``BlueJ-GUI-Builder-fat-1.0-SNAPSHOT.jar`` which you can find in ``build/libs/``. 

This file has to be copied to one of the following locations:

| OS      | Folder                                                                                                                   | Scope                         |
| ------- | ------------------------------------------------------------------------------------------------------------------------ | ----------------------------- |
| Unix    | `<BLUEJ_HOME>/lib/extensions`                                                                                            | For all users in all projects |
| Unix    | `<USER_HOME>/.bluej/extensions`                                                                                          | For this user in all projects |
| Unix    | `<BLUEJ_PROJECT>/extensions`                                                                                             | For this project              |
| Windows | `<BLUEJ_HOME>\lib\extensions`                                                                                            | For all users in all projects |
| Windows | `<USER_HOME>\bluej\extensions`                                                                                           | For this user in all projects |
| Windows | `<BLUEJ_PROJECT>\extensions`                                                                                             | For this project              |
| Mac     | `<BLUEJ_HOME>/BlueJ.app/Contents/Resources/Java/extensions` (Control-click `BlueJ.app` and choose Show Package Contents) | For all users in all projects |
| Mac     | `<USER_HOME>/Library/Preferences/org.bluej/extensions`                                                                   | For this user in all projects |
| Mac     | `<BLUEJ_PROJECT>/extensions`                                                                                             | For this project              |

After installation restart BlueJ, open your project, create a new Class, right-click on it and open the GUI-Builder from within the context menu.

## Bedienungsanleitung
TODO

## Goals

 - [x] fully run within BlueJ
 - [x] easy installation
 - [x] CI/CD
 - [X] good documentation

## Credits

 - David Hodgson, [Leon Atherton](https://github.com/leonatherton) and [Ben Goodwin](https://github.com/beng92) - the creators of the [original project](https://github.com/JavaFX-GUI-Builder/GUI-Builder)
 - [Georg Dick](https://github.com/gediwes/) for continuing the [project](https://github.com/gediwes/GUI-Builder)
 - [Jan Keim](https://github.com/Gram21) for being my supervisor

## License
This project is licensed under the GPLv3
