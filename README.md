Throwback For Last.fm
=========

Throwback by Rahul Khanna and Sameer Lal

How to run and compile the code: When in the directory with the java source files type these commands:

On a mac:

javac -classpath jsoup-1.7.2.jar *.java

java -classpath .:jsoup-1.7.2.jar Throwback

On a pc:

javac -classpath jsoup-1.7.2.jar *.java

java -classpath .;jsoup-1.7.2.jar Throwback

Brief Description Of Program:

Throwback attempts to look through a person’s last.fm listening history and uncover songs that users used to listen to a lot and now have forgotten. There are many songs that people listen to and like, but ultimately forget about after some time. This doesn’t mean that the person has stopped liking the song, rather he/she has gotten bored of it and is looking for something new. That something new though could be replaced by something old that is presented to the user as something new. Based on three attributes, play count, date added (first time played), and time length( the middle 80% of the times a person listened to a song), songs are ranked. Then a weighted composite score is created for each song. The weighting of the date added component is a fixed value, which will ultimately be able to be controlled by the user, but the weighing of the other two attributes is done relative to the coefficient of variation of the data for each attribute. The songs with the lowest composite score are considered the most likely to fit the throwback criteria of a song for that user and so a playlist of songs with the lowest composite scores, and current music a user is listening to is displayed to the user when prompted. The user can also view what the program thinks are good throwback songs for him/her.

In determining what a user considers throwback the program will play a “game” with the user, prompting the user to answer questions about songs in his/her library. At the end of the game the program will have a good understanding of what songs to pick out for the user. If the user ever feels that the playlists being generated are not of a high quality, he/she can go back and play the “game” with the user and reteach the computer what he/she considers throwback.

last.fm : http://www.last.fm/
