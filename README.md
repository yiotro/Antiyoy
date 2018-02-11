
# Setting up

1. `git clone https://github.com/yiotro/Antiyoy`
2. Open the folder in android studio. The project should get generated from the gradle files.

## Running the app

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

