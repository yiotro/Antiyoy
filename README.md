
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

### Ideas from the GIT repository
Translated with Google Translate from [origin](https://translate.google.co.il/translate?sl=ru&u=https%3A%2F%2Fraw.githubusercontent.com%2Fyiotro%2FAntiyoy%2Fmaster%2Fcore%2Fsrc%2Fyio%2Ftro%2Fantiyoy%2Fideas.txt)

TODO - rewrite these sentences in proper english
* Make that the AI built a peasant, not a farm on the first turn.
* Ability to turn off movement limit
* Ability to switch language.
* Button to switch between units that are ready to move. Disabled by default. Show below the center when a unit is selected.
* When a loss is not "good" and "okay"
* Ask if to end the turn only when there are units that are ready to move
* In the proposal to replace the winning "continue" to "collapse".
* Difficulty "misanthrope" and "balancer"
* Ability to disable the fast moving units.
* Above the unused units put an exclamation mark
* Add setting to water on the background was black instead. (Just replace teksturku)
* Confirmation of import in the editor
* Textures on the cells to people with color blindness can play
* The separation of the province to give money to that province which was a house.
* Add a "huge" size card
* The campaign at the start of the level to simulate the first few turns.
* Fix the "new" button
* Confirm unit merge
* Option "Advanced course". Each AI goes separately, that is, by turns.
* Try all the same to make long-range units.
* Make a normal AI a little bit more to smooth the transition to the campaign.
* Make that the units can be set up just beside the farm and the capital.
* Add a monk who turns enemy units in their.
* AI on Expert remove "risk" when AI creates too steep units and they die.
* Reset campaign progress
* Achievements (achivki)
* Minimize button in the dialog of victory (to look back at the map)
* Add the ability to somehow kill the foreign units for money to fixed a stalemate. There is another way to solve this problem: the activation of empty cells for the money
* In the editor:
   * Cancel button
   * 0 players
   * Scrolling with two fingers
