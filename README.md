# earnapp-java-watcher
An Earnapp **Discord Webhook** that shows total earnings each hour, made for personal use, using my custom unofficial Earnapp API in Java. Also features MySQL support for my own _earnapp-earnings-graphs_.

# Build:
Download the repository by cloning it or downloading the .zip file, then open the earnapp-java-watcher folder.

To build you can use your IDE or run something like gradlew build from the command line (not tested).

Run `gradle build`, then find the jar in your `build/libs/` folder with the name of `java-earnapp-watcher-X.X-SNAPSHOT-all.jar`, make sure to use
the jar with `-all` at the end of the name.

# Run:
To run the program, _**You need Java 18 or higher**_, then just run: 
- `java -jar java-earnapp-watcher-X.X-SNAPSHOT-all.jar`

Replacing `X.X` with the build version.

Then edit your `config.json` with the required data, MySQL is optional as well as the webhooks (you may want to disable it for troubleshooting).


# Features:
- Work in progress Earnapp-API Java, still buggy and not fully working.
- Bandwidth usage is calculated on your earnings which isn't the best approach, one day I'll make it properly.
- Each hour earnings reports to MySQL and Discord Webhooks.
- Java is easy to use if you know even just a little of code (For me at least).
