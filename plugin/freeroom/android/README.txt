FreeRoom - Client README
Authors: Valentin Minder / Julien Weber

The FreeRoom client use the MVC scheme. The controller handles I/O with the server, the model stores data (results received, permanent settings and non-permanent shared values), as the views handles the UI and user interaction.

Interaction with the server is dont with thrift object, using child class from Request and ASyncTask. The thrift objects are defined in the shared folder.

*** More about the main view and popup ***
The main view, FreeRoomHomeView, is the entry of the plugin. It displays the availabilities for the specified search given, which can chosen or automatically done at launch time according to the model settings.

Rooms can directly be (un)marked as favorites. If the room is free, the user can directly share its location with his friend (share icon) or with the server only (people +1 icon).

All others “views” (=user interaction) are supposed to be dialog/popup windows, therefore the main view is always visible on the background. There are 12 different pop-up, but the more important are the following.

SEARCH: actionnable from the action bar, allow the user to make a specific search (change the time, check only for free room or set/change the room range, select options…). Previous search are kept permanently and the user may reuse, edit and manage them in the bottom of the search popup. 

INFODETAIILSROOM: to show the detailed occupancy of room, particularly relevant for multiple hours. It’s opened by clicking on the room name on home view.

SETTINGS: allow the use to change the settings. The home behaviour can be changed for the room range (any free room or favorites) and the time range (current hour, up to end of the day, whole day).

*** More about sharing ***
When sharing the location/time with friends (using any texting/messaging application on the device), the server is also notified, in order to estimate the number of people. Sharing only with the server is done with the specific button or by the “people +1” icon.

The message (activity) is shared with the server ONLY if the checkbox is ticked. Otherwise, only a blank message is shared. For example, if you share with your friends that your working on “SwEng” in BC01 now, but you unselect “share activity with server”, you friends will receive a message including your activity, but the server (and therefore other user of FreeRoom) will ONLY know that there’s somebody working there, without its activity.

Please note a “copy to clipboard” service was added, in order to provide a solution to texting message that accepts text Intent but dont use them. For example, crypto secure text messaging TELEGRAM dont make a correct usage of text Intent. Sharing with Facebook (on your timeline/in a group) requires usage of the FB API, but sharing with text Intent is discarded afterwards even if it’s accepted. Please note Google Drive application also provide a “copy to clipboard” service, which can be a duplicate. But not all EPFL users have Google Drive installed, so this is not an issue.