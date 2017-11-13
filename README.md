# Google Play

The source code of this game: https://play.google.com/store/apps/details?id=yio.tro.antiyoy.android&hl=en

# Setting up

## LibGDX Project Generator
(I used antiyoy-dd as the name, anything else is fine)

Download the libgdx project generator:
https://libgdx.badlogicgames.com/download.html

Use it to generate a project:
1. Fill details
   * Antiyoy:
      * antiyoy-dd
      * yio.tro.antiyoy
      * YioGdxGame
      * D:\Projects\Android\antiyoy-dd
1. Remove ios and html sub-projects
1. Under ‘Extensions’ enable the following:
   * Freetype
1. Inside ‘Advanced’ enable ‘IDEA’
1. Click ‘Generate’
1. Verify appropriate build-tools (23.0.1) & API (20) versions (NO,OK,NO,OK)

## IDEA Project Settings
Open ‘antiyoy-dd.ipr’ (do not import) in IDEA
Ignore ballon notification about ‘unlinked gradle project’ (better just press ‘do not show anymore’)

Open settings (Click Ctrl+Shift+Alt+S)
1. Set Android SDK (default of libGdx - API 20)
1. Set language level (Antiyoy minimum - 7)

Edit Run/Debug Configurations
* Android
   1. Click ‘+’ and select ‘Android Application’
   1. Name it RunAndroid
   1. Select module ‘android’
* Desktop
   1. Click ‘+’ and select ‘Application’
   1. Name it RunDesktop
   1. Set the ‘Main class’ by the button to the right of the field (Shift+Enter)
      * e.g. com. … .desktop.DesktopLauncher
   1. Set working directory to android/assets
   1. Select module ‘desktop’

Or it can be done easier:
* Android
   1. Go to AndroidLauncher.java
   1. Press Ctrl+Shift+F10
* Desktop
   1. Go to DesktopLauncher.java
   1. Press Ctrl+Shift+F10
   1. Go to run configuration and set working directoy to ‘android/assets’

## Test
At this stage you should be able to run the android and the desktop version of the generated example. Use the run button or press Shift+F10. Also you can use Alt+Shift+F10 to choose which run config to run.

##Copy Antiyoy Source Files
Delete ‘android/assets/badlogic.jpg’.

Download or clone the github repository of Antiyoy from:
https://github.com/yiotro/Antiyoy

Overwrite the files in ‘antiyoy-dd’ with the files from Antiyoy repository:
   1. The ‘assets’ directory shoud overwrite ‘android/assets’ directory
   1. The ‘core’ directory shoud overwrite ‘core’ directory (At least one file should be replaced, the one that extends ApplicationAdapter)

## AndroidManifest
Open ‘android/AndroidManifest.xml’
Change screenOrientation to portrait

## Test
At this stage you should be able to run Antiyoy, try it!

# TODO - How to make a pull request?

## Development Ideas

### dvb’s ideas

#### Progressively Learning AI
AI that can progressively learn to play better against the human player, without a constant predefined set of rules. 

#### Multiplayer Games
Using Google Play Games Services:
https://developers.google.com/games/services/android/turnbasedMultiplayer
AI players should still be an option, as described in:
http://stackoverflow.com/questions/26125273/how-to-create-a-computer-opponent-in-turn-based-google-play-game?rq=1

