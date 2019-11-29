# Antiyoy
Antiyoy is a simple turn-based android strategy. Easy to learn, hard to master.

Features:
- Hotseat multiplayer up to 10 players
- Random map generator
- Smooth animations and good optimization
- Easy tutorial

# What is this
This repository contains source code of antiyoy. You are free to download, modify and run it. This is non-commercial project, so commercial use is forbidden.

Here is how you set up this project on your computer:

1. Create blank libgdx project. Make sure that you can compile and run it.
2. Swap source code of that project with source from this repo.
3. Do same thing with assets.

# Making a sample libgdx project
Now, in more details. Here is how you make sample libgdx project:
1. Download libgdx project generator: https://libgdx.badlogicgames.com/download.html
2. Launch it.
3. Name: Antiyoy
4. Package: yio.tro.antiyoy
5. Game class: YioGdxGame
6. Destination: doesn't matter
7. Android SDK: path to your android sdk folder. You should have it already installed.
8. Uncheck 'Html' and 'Box2D'
9. Check 'Freetype'
10. Press 'Advanced' and check 'IDEA'.
11. Press 'Generate'. It should take some small time and finish with 'BUILD SUCCESSFULL' message.

# Compile and run in IDEA
Now, here is how you open this project in Intellij IDEA, compile and run it:
1. Launch IDEA.
2. Press 'File - Open'
3. Find '.ipr' file in project folder and choose it.
4. Now in IDEA go to 'DesktopLauncher.java' file. To do it just press 'Shift' couple times and type in 'dekstoplauncher'. IDEA should find it.
5. Press Ctrl+Shift+F10. This will create run configuration for desktop version and launch it. 
6. First time it will fail with mistake: something about being unable to load some assets. That's because assets folder is not set up correctly for some reason.
7. Press 'Alt+Shift+F10'. This will open list on run configurations. Choose 'Desktop' and press 'right'. Then choose 'Edit' and press enter.
8. Change 'Working directory' to '<Project name>/android/assets'.
9. Now desktop run configuration should work properly.

# Finally
At this point you should have a working sample libgdx project (just a window with single image). Now just replace source and assets in this project with source and assets from the repo and check if it works. Final touch: go to DesktopLauncher.java and change window dimension there.

To run in on android just do the same thing as with 'DekstopLauncher.java' but with 'AndroidLauncher.java'. Note: you'll also have to change 'screenOrientation' in 'AndroidManifest.xml' file (which you can also find by pressing shift few times and typing 'anman').

If you have some feedback about this game then please email me (yiotro93@gmail.com) about it.


