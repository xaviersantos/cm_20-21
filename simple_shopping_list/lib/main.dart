import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'class_shopping_list.dart';
import 'detail_screen.dart';


void main() {
  runApp(MaterialApp(
    title: 'My Shopping List',
    home: ListsScreen(),

      // Send the list as arg
      // lists: List.generate(
      //   20,
      //   (i) => ShoppingList(
      //     'Shopping list $i',
      //     'stuff',
      //   ),
      // ),
    ),
  );
}



class ListsScreen extends StatelessWidget {
  // ListsScreen({Key key, @required this.lists}) : super(key: key); // Receive the list as arg
  ListsScreen({Key key}) : super(key: key);

  // final List<ShoppingList> lists = new List<ShoppingList>();
  final List<ShoppingList> lists = <ShoppingList>[
    ShoppingList('Shopping list 1',"stuff"),
    ShoppingList('Shopping list 2',"stuff"),
    ShoppingList('Shopping list 3',"stuff"),
    ShoppingList('Shopping list 4',"stuff"),
    ShoppingList('Shopping list 5',"stuff"),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(

      appBar: AppBar(
        title: Text('My Shopping List'),
        actions: <Widget> [
          IconButton(
            icon: Icon(Icons.add),
            onPressed: () {
              //TODO: route to new list creation
                ShoppingList newEntry = new ShoppingList((lists.length + 1).toString(), "stuff");
                lists.add(newEntry);
              }
          ),
        ],
      ),

      body: ListView.builder(
        itemCount: lists.length,
        itemBuilder: (context, index) {
          return ListTile(
            title: Text(lists[index].title),
            // When a user taps the ListTile, navigate to the DetailScreen.
            // Notice that you're not only creating a DetailScreen, you're
            // also passing the current todo through to it.
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => DetailScreen(),
                  // Pass the arguments as part of the RouteSettings. The
                  // DetailScreen reads the arguments from these settings.
                  settings: RouteSettings(
                    arguments: lists[index],
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

