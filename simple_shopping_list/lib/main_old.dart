import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart' as auth;
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';


import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

import 'package:myshoppinglist/ui/page_done.dart';
import 'package:myshoppinglist/ui/page_settings.dart';
import 'package:myshoppinglist/ui/page_task.dart';
import 'package:myshoppinglist/ui/page_detail.dart';

Future<Null> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  _currentUser = await _signInAnonymously();

  runApp(new MyShoppingListApp());
}
final FirebaseAuth _auth = FirebaseAuth.instance;

auth.User _currentUser;

Future<auth.User> _signInAnonymously() async {
  final user = await _auth.signInAnonymously();
  return user.user;
}

class HomePage extends StatefulWidget {
  final auth.User user;

  HomePage({Key key, this.user}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _HomePageState();
}

class MyShoppingListApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: HomePage(
        user: _currentUser,
      ),
      theme: new ThemeData(primarySwatch: Colors.blue),
    );
  }
}



class _HomePageState extends State<HomePage>
    with SingleTickerProviderStateMixin {
  int _currentIndex = 1;
  
  List<String> entries = <String>['1','2','3'];

  final List<Widget> _children = [
    DonePage(
      user: _currentUser,
    ),
    TaskPage(
      user: _currentUser,
    ),
    //SettingsPage(
    //  user: _currentUser,
    //)
  ];


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: new AppBar(
        title: Text("Simple Shopping List"),
        actions: <Widget> [
          IconButton(
            icon: Icon(Icons.add),
            onPressed: () {
              //TODO: route to new list creation
              setState(() {
                String newEntry = (entries.length + 1).toString();
                entries.add(newEntry);
              });
            }
          ),
        ],
      ),
      body: ListView.separated(
        padding: const EdgeInsets.all(8),
        itemCount: entries.length,
        itemBuilder: (BuildContext ctx, int idx) {
          return ListTile(
            title: Text('Shopping list ${entries[idx]}'),
            onTap: () {
              Navigator.push(
                ctx,
                MaterialPageRoute(
                  builder: (ctx) => DetailPage(), //entries[idx]),
                )
              );
            }
          );
        },
        separatorBuilder: (BuildContext ctx, int idx) => const Divider(),

      )
    );
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  void initState() {
    super.initState();

    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
  }

  void onTabTapped(int index) {
    setState(() {
      _currentIndex = index;
    });
  }
}