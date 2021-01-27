# My Shopping List

## How to run

The application automatically logs on to Firestore using an anonymous login, assigning the devices' unique id to its key.

Our application uses a Bottom Navigation menu to navigate through the application. There are 3 main screens (Map, Lists and Settings).

The lists page is where all our lists are displayed. There is a button that can be used to add new lists and the lists’ name can be edited with a swipe to the right or deleted with a swipe to the left.
The lists can be clicked to add and interact with its items.

Inside the list instance we have a new page where we can see all the items inside the list. They can be edited or deleted with a swipe as well and checked/unchecked on click. There are 3 floating buttons; one to add new items, other to add a location for that specific list and another one to add an item through barcode scanning (not yet implemented).

On the map menu we can see the markers for all our lists as well as our position. Upon clicking the marker we can see which list is associated with each marker.