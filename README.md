# earnapp-java-watcher
An Earnapp Discord Webhook that shows total earnings each hour, made for personal use, using my custom unofficial Earnapp API in Java. Also features MySQL support for my own earnapp-earnings-graphs.

# Build:
Run gradle build, then use the jar in your build/libs/ Folder with the name of "java-earnapp-watcher-X.X-SNAPSHOT-all.jar".

# Run:
To run the program, <b>You need Java 18 or higher</b>, then just run: 
- java -jar java-earnapp-watcher-X.X-SNAPSHOT-all.jar
Replacing X.X with the version of earnapp.

Then edit your config.json with the required data, MySQL is option as well as the webhook (you may want to disable it for troubleshooting).


# Features:
- Work in progress Earnapp-API Java, still buggy and not fully working.
- Bandwidth usage is calculated on your earnings which isn't the best approach, one day I'll make it properly.
- Each hour earnings reports to MySQL and Discord Webhooks.
- Java is easy to use if you know even just a little of code (For me at least).
