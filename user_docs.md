# Client Installation and Usage

This document provides details on how to install the client application and how to use all of its features.

## Client Installation

As we have not yet published the application on the official Play Store, it is necessary to first allow unknown app installation on the used Android device in order to install and use it. This permission needs to be granted to the browser application through which the user wishes to download the app. After this step is performed, the user only needs to visit the application's online [GitHub Repository](https://github.com/matejsubrt/PragO), select the desired release of the app and download the `prago-<version>.apk` file from [Releases](https://github.com/matejsubrt/PragO/releases/tag/0.1.0). After it is downloaded, simply tap the file in the downloads section and follow the installation guide. Alternatively, it is possible to build and install the project manually using Android Studio.

Please note that after first downloading the app, it first needs to download the current data on stops within the network in order to provide stop name suggestions. As this step uses a significant amount of data, it will only be performed when the device is connected to the internet using Wi-Fi. This step may also take a longer time depending on your internet download speed. Thus, we recommend first downloading and launching the app while connected to a Wi-Fi network. Subsequent updates of this data will be performed automatically in the background when connected using Wi-Fi connections and thus do not require any special care.

## Client Usage

In this section, we will go over the different parts of the application, their user interface and the features they provide.

### Search Screen

The search screen is the first screen that the user will be presented with after launching the application. It contains the main input fields and most of the toggles and sliders necessary to meet the functional requirements. Only a few settings that are not expected to be changed frequently have been moved to the settings screen.

The search screen contains the inputs for the source and destination stop names. A click on these leads the user to the stop selection screens, where they can actually select the stop they want. There is also a toggle to flip the direction of the search.

Under the stop name inputs is the row with time settings. First, it contains an icon indicating whether the search is currently set to consider the set time to be the earliest possible departure, or latest possible arrival time. Next to this, the currently set time is displayed. When clicked, this takes the user to the time select screen, where they can select both the time and the departure/arrival setting. Finally, there is a "Now" button at the end that resets the time settings to earliest departure at the current time.

Under the time row, there is a simple toggle for enabling shared bikes usage within the search.

Finally, there is a section with extended settings. These contain some of the search customization options we decided to provide with our application. The section is minimized by default, but clicking on it reveals its content. Within this section, the user can set the transfer buffer (corresponds to the transfer buffer requirement), the maximum transfer length (max transfer distance), and the balance between shortest time and least transfers (i.e. comfort preference). Furthermore, if "Use Shared Bikes" is set to true, this section also contains the slider for setting a bike trip buffer and a toggle to limit the maximum bike trip duration to 15 minutes.

Apart from the source and destination points and the time settings, all other entered values are preserved in between app launches to prevent having to modify them every time the app is being used.

### Stop Select Screen

This screen will appear when the user clicks on any of the two stop selection fields on the search screen. Apart from being able to return back to the search screen via the return button at the top, the user can select the source or destination stops here by typing out their names. The application supports improved searching, which means that for multiple-word stop names, the user can only type the beginnings of each word (i.e. "mal nam" for "Malostranské náměstí"), and the application will provide the corresponding suggestions. Upon clicking on any suggestion, the application saves this preference and returns the user back to the search screen. There is also a delete button that deletes the entered text if the user wants to start from the beginning.

Additionally, when setting the source, the user also has the opportunity to select the "My location" suggestion, which will start the search using the user's current coordinate location instead of using a stop name. Note that for this to work, the user must allow the application to use their location in the popup that will appear.

### Time Select Screen

On this screen, the user can adjust all the time settings. They can change the date via the date setting wheel, the time via the time setting wheel, and the meaning of the selected time, i.e. whether it sets the earliest departure or latest arrival boundary. Upon clicking the "Done" button, the user is taken back to the search screen.

### Settings Screen

This screen provides access to all the settings that typically do not change very often and thus do not need to be included on the main search screen. These include setting the walking pace, cycling pace, and the shared bike lock and unlock times. By clicking the "Save and return" button, the user saves the values and returns to the search page.

### Results Screen

This is where the results of the search get displayed. Our app does not just search for a single connection, but rather searches for multiple best connections across a time range. The app first searches for the best connection according to the given parameters. Then, if the user scrolls further down the page, it loads further later connections. If the user wants to view earlier connections, they can pull down while on top of the page, which will expand the results into the past.

Every individual result is displayed in the form of a single connection card. This card contains all the necessary information about all the segments of the connection. At the top, there is a countdown until the connection leaves and the total duration of the connection. The countdown has three variations:
- When the connection immediately begins by boarding a trip at the source stop, only a countdown until boarding the trip is displayed.
- When the connection only consists of transfers and bike trips, there is no time limitation as to when the connection may be performed, and it is marked as "Anytime."
- When the connection begins with a transfer and later continues with a trip, two countdowns are displayed — one showing the time left until the first trip leaves and a second one showing the time left until the user should start the initial transfer. If the first trip has not yet departed, but the time at which the user was supposed to start walking to make the connection has already elapsed, the first trip countdown is highlighted to remind the user of this situation.

Every segment within the connection has a separate section within the result card. For a trip, the line name, together with the boarding and disembarking stops and exact times and the current delay of the trip, are displayed. For a transfer, the distance and expected duration are shown. Finally, for a bike trip, the application displays its source and destination bike stations' names, the distance and expected duration of the trip, and the current number of free bikes available at the source station.

The user can also swipe left or right on any of the displayed public transit trips to reveal its earlier/later alternatives. To provide the users with easy navigation during their bike trips, it is also possible to click on any of the displayed bike trips, which (when connected to the internet) will take the user to the *Mapy.cz* application (if installed) and display the route they are supposed to take. Note that the length of this displayed route might vary slightly from the distance specified in our app, as we use a different routing engine and map data for our searches.
