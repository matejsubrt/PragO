We designed our application in a way that only leaves as little work as possible to the client. This means that the client application can stay relatively lightweight and simple to implement and thus it would be easy to adapt it to work on a different platform. We chose to implement it for the Android operating system using the Kotlin programming language together with the Jetpack Compose UI development toolkit. The current industry standard for developing Android applications and UIs is to use the MVVM (Model View ViewModel) architectural pattern, and so that is what we have used.

With this approach, we split the application into three layers, each with its own responsibilities:

### Model

The Model layer is responsible for abstracting all the different data sources. In our case, this includes in-device storage of user settings and access to the server-side API. In particular, the model of our application is contained in the `model` package and contains the following parts:

- **`SettingsRepository`**: A class responsible for storing and retrieving the settings values selected by the user. Its main purpose is to preserve these values between app launches, so that it is not necessary to re-enter them every time the application is being used. It is implemented using Jetpack Compose's `Preferences DataStore` solution, which is used to allow key-value pairs, perfect for our simple use case where we only need to store a few distinct settings values.
- **`StopListRepository`**: Another class used for long-term data storage. This time, however, the data being stored are not simple key-value pairs, but rather structured data describing all of the stop name suggestions that should be shown to the user. Due to this, we have used Jetpack Compose's `Proto DataStore` here. This is a solution for structured data storage, for which we needed to describe the data's structures through a protocol buffer schema, which is included in the `proto` directory. 
- **`ConnectionSearchApi`**: Contains all the implementation related to sending requests to the API of the server-side application and receiving the responses. This is what the `ViewModel` uses to perform, expand, and update searches. It contains three public functions, corresponding to the three endpoints of the server's API.
- The last item contained within the `model` package is all the data classes used to represent all the data used by the application and data sent to the server as part of the requests. These can be found in the `model/dataClasses` package.

### View

The View layer is responsible for presenting the UI to the user and informing the `ViewModel` about the user's actions. With Jetpack Compose, this is done using special `@Composable` functions. Every one of these functions defines a single UI component. It can observe relevant values in the `ViewModel` and is recomposed anytime any of these observed values changes. The reference to the `ViewModel` itself is passed to those functions that require it. Notifying the `ViewModel` of the user's actions is done through setting callback functions that call the `ViewModel`'s functions.

As our app is very simple, it only contains one activity (`MainActivity`), which initiates the `Model` and `ViewModel` and launches the top-level composable function, `PragOApp`.

We have split the `View`'s files into sub-packages according to the section of the app they correspond to. Thus, there are the packages `searchScreen`, `stopSearchScreen`, `settingsScreen`, and `resultScreen`. Their content corresponds to the screens described in [user documentation](user_docs.md). Navigation between the separate screens is done through callback functions that use the `NavController` object created in the `MainActivity` file. Finally, there is also a `common` sub-package containing composables that are common between multiple screens.

### ViewModel

The `ViewModel` is the main functional part of the application. It handles all the user's actions and responds to them. Furthermore, it exposes the relevant data streams for the `View` to use. As our application is very simple, we decided to only use a single slightly larger `ViewModel` instead of using multiple smaller ones for every different functionality. This was done to keep the complexity of the code structure to a minimum.

Our `ViewModel` has direct access to all of the `Model`'s repositories and APIs. It exposes the relevant data to the `View` through its properties. This includes both the settings values needed by the `SearchScreen` and `SettingsScreen` and the stop suggestion list needed by the `StopSearchScreen`. It also provides a way to change those values if the corresponding function is called from within the `View`.

Furthermore, it also holds all the information on the current search query (i.e. the user's input from the `SearchScreen`) and on the current state of the results (if some have already been fetched). Lastly, it provides the most important functions that can be called by the user from within the `View` and that use the `ConnectionSearchApi` to send requests to the server. These include the `startSearch` and `expandSearch` methods (both of which use the server's `/connection endpoint` through the `ConnectionSearchApi`'s `searchForConnections` method), the `fetchAlternatives` method used to provide the user with alternatives for a single displayed trip segment (corresponding to the `/alternative-trips` endpoint of the server), and the `updateDelays` method used to call the server's `/update-delays` endpoint to refresh the delay values displayed to the user.