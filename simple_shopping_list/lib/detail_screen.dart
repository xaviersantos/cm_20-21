import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'class_shopping_list.dart';

class DetailScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final ShoppingList list = ModalRoute.of(context).settings.arguments;

    // Use the ShoppingList to create the UI.
    return Scaffold(
      appBar: AppBar(
        title: Text(list.title),
      ),
      body: Padding(
        padding: EdgeInsets.all(16.0),
        child: Text(list.description),
      ),
    );
  }
}
