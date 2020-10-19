import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:my_shopping_list/ui/page_done.dart';
import 'package:my_shopping_list/ui/page_settings.dart';
import 'package:my_shopping_list/ui/page_task.dart';

Future<Null> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  _currentUser = await _signInAnonymously();

  runApp(new MyShoppingListApp());
}
final FirebaseAuth _auth = FirebaseAuth.instance;

FirebaseUser _currentUser;

Future<FirebaseUser> _signInAnonymously() async {
  final user = await _auth.signInAnonymously();
  return user;
}

class HomePage extends StatefulWidget {
  final FirebaseUser user;

  HomePage({Key key, this.user}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _HomePageState();
}

class MyShoppingListApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: "My Shopping List",
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

  final List<Widget> _children = [
    DonePage(
      user: _currentUser,
    ),
    TaskPage(
      user: _currentUser,
    ),
    SettingsPage(
      user: _currentUser,
    )
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      bottomNavigationBar: BottomNavigationBar(
        onTap: onTabTapped,
        currentIndex: _currentIndex,
        fixedColor: Colors.deepPurple,
        items: <BottomNavigationBarItem>[
          BottomNavigationBarItem(
              icon: new Icon(FontAwesomeIcons.calendarCheck),
              title: new Text("")),
          BottomNavigationBarItem(
              icon: new Icon(FontAwesomeIcons.calendar), title: new Text("")),
          BottomNavigationBarItem(
              icon: new Icon(FontAwesomeIcons.slidersH), title: new Text(""))
        ],
      ),
      body: _children[_currentIndex],
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