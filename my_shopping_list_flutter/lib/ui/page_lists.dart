import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart' as auth;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:myshoppinglist/model/element.dart';
import 'package:myshoppinglist/ui/page_detail.dart';

import 'page_addlist.dart';

class ListsPage extends StatefulWidget {
  final auth.User user;

  ListsPage({Key key, this.user}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _ListsPageState();
}

class _ListsPageState extends State<ListsPage>
    with SingleTickerProviderStateMixin {
  int index = 1;

  @override
  Widget build(BuildContext context) {
    print("page_lists"); // debug
    return Scaffold(
      body: ListView(
        children: <Widget>[
          new Column(
            children: <Widget>[
              Padding(
                padding: EdgeInsets.only(top: 50.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: <Widget>[
                    Expanded(
                      flex: 1,
                      child: Container(
                        color: Colors.grey,
                        height: 1.5,
                      ),
                    ),
                    Expanded(
                        flex: 4,
                        child: new Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: <Widget>[
                            Text(
                              'My',
                              style: new TextStyle(
                                  fontSize: 28.0, color: Colors.grey),
                            ),
                            Text(
                              'Shopping',
                              style: new TextStyle(
                                  fontSize: 30.0, fontWeight: FontWeight.bold),
                            ),
                            Text(
                              'Lists',
                              style: new TextStyle(
                                  fontSize: 28.0, color: Colors.grey),
                            )
                          ],
                        )),
                    Expanded(
                      flex: 1,
                      child: Container(
                        color: Colors.grey,
                        height: 1.5,
                      ),
                    ),
                  ],
                ),
              ),
              Padding(
                padding: EdgeInsets.only(top: 50.0),
                child: new Column(
                  children: <Widget>[
                    new Container(
                      width: 50.0,
                      height: 50.0,
                      decoration: new BoxDecoration(
                          border: new Border.all(color: Colors.black38),
                          borderRadius: BorderRadius.all(Radius.circular(7.0))),
                      child: new IconButton(
                        icon: new Icon(Icons.add),
                        onPressed: _addItemPressed,
                        iconSize: 30.0,
                      ),
                    ),
                    Padding(
                      padding: EdgeInsets.only(top: 10.0),
                      child: Text('Add List',
                          style: TextStyle(color: Colors.black45)),
                    ),
                  ],
                ),
              ),
            ],
          ),
          Padding(
            padding: EdgeInsets.only(top: 50.0),
            child: Container(
              height: 360.0,
              padding: EdgeInsets.only(bottom: 25.0),
              child: NotificationListener<OverscrollIndicatorNotification>(
                onNotification: (overscroll) {
                  overscroll.disallowGlow();
                },
                child: new StreamBuilder<QuerySnapshot>(
                    stream: FirebaseFirestore.instance
                        .collection(widget.user.uid)
                        .orderBy("date", descending: true)
                        .snapshots(),
                    builder: (BuildContext context,
                        AsyncSnapshot<QuerySnapshot> snapshot) {
                      if (!snapshot.hasData)
                        return new Center(
                            child: CircularProgressIndicator(
                              backgroundColor: Colors.blue,
                            ));
                      return new ListView(
                        physics: const BouncingScrollPhysics(),
                        padding: EdgeInsets.only(left: 40.0, right: 40.0),
                        scrollDirection: Axis.horizontal,
                        children: getExpenseItems(snapshot),
                      );
                    }),
              ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    super.dispose();
  }

  getExpenseItems(AsyncSnapshot<QuerySnapshot> snapshot) {
    List<ElementItem> listElement = new List();
    List<ElementItem> listElement2;
    Map<String, List<ElementItem>> userMap = new Map();

    List<String> cardColor = new List();

    if (widget.user.uid.isNotEmpty) {
      cardColor.clear();

      // ignore: missing_return
      snapshot.data.docs.map<List>((f) {
        String color;
        // String location;
        f.data().forEach((a, b) {
          if (b.runtimeType == bool) {
            listElement.add(new ElementItem(a, b));
          }
          if (b.runtimeType == String && a == "color") {
            color = b;
          }
          // if (b.runtimeType == String && a == "_location") { //TODO <<<<<<<<<<<<<<<<<<<<<
          //   location = b;
          // }
        });
        listElement2 = new List<ElementItem>.from(listElement);
        for (int i = 0; i < listElement2.length; i++) {
          if (listElement2.elementAt(i).isDone == false) {
            userMap[f.id] = listElement2;
            cardColor.add(color);
            break;
          }
        }
        if (listElement2.length == 0) {
          userMap[f.id] = listElement2;
          cardColor.add(color);
        }
        listElement.clear();
      }).toList();

      return new List.generate(userMap.length, (int index) {
        return new GestureDetector(
          onTap: () {
            Navigator.of(context).push(
              new PageRouteBuilder(
                pageBuilder: (_, __, ___) => new DetailPage(
                  user: widget.user,
                  i: index,
                  currentList: userMap,
                  color: cardColor.elementAt(index),
                ),
                transitionsBuilder:
                    (context, animation, secondaryAnimation, child) =>
                new ScaleTransition(
                  scale: new Tween<double>(
                    begin: 1.5,
                    end: 1.0,
                  ).animate(
                    CurvedAnimation(
                      parent: animation,
                      curve: Interval(
                        0.50,
                        1.00,
                        curve: Curves.linear,
                      ),
                    ),
                  ),
                  child: ScaleTransition(
                    scale: Tween<double>(
                      begin: 0.0,
                      end: 1.0,
                    ).animate(
                      CurvedAnimation(
                        parent: animation,
                        curve: Interval(
                          0.00,
                          0.50,
                          curve: Curves.linear,
                        ),
                      ),
                    ),
                    child: child,
                  ),
                ),
              ),
            );
          },
          child: Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.all(Radius.circular(8.0)),
            ),
            color: Color(int.parse(cardColor.elementAt(index))),
            child: new Container(
              width: 220.0,
              //height: 100.0,
              child: Container(
                child: Column(
                  children: <Widget>[
                    Padding(
                      padding: EdgeInsets.only(top: 20.0, bottom: 15.0),
                      child: Container(
                        child: Text(
                          userMap.keys.elementAt(index),
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 19.0,
                          ),
                        ),
                      ),
                    ),
                    Padding(
                      padding: EdgeInsets.only(top: 5.0),
                      child: Row(
                        children: <Widget>[
                          Expanded(
                            flex: 2,
                            child: Container(
                              margin: EdgeInsets.only(left: 10.0, right: 10.0),
                              color: Colors.white,
                              height: 1.5,
                            ),
                          ),
                        ],
                      ),
                    ),
                    Padding(
                      padding:
                      EdgeInsets.only(top: 30.0, left: 15.0, right: 5.0),
                      child: Column(
                        children: <Widget>[
                          SizedBox(
                            height: 220.0,
                            child: ListView.builder(
                              //physics: const NeverScrollableScrollPhysics(),
                                itemCount:
                                userMap.values.elementAt(index).length,
                                itemBuilder: (BuildContext ctxt, int i) {
                                  return Padding(
                                    padding: EdgeInsets.only(bottom: 10.0),
                                      child: Row(
                                        mainAxisAlignment: MainAxisAlignment.start,
                                        children: <Widget>[
                                          Icon(
                                            userMap.values
                                                .elementAt(index)
                                                .elementAt(i)
                                                .isDone
                                                ? FontAwesomeIcons.checkCircle
                                                : FontAwesomeIcons.circle,
                                            color: userMap.values
                                                .elementAt(index)
                                                .elementAt(i)
                                                .isDone
                                                ? Colors.white70
                                                : Colors.white,
                                            size: 14.0,
                                          ),
                                          Padding(
                                            padding: EdgeInsets.only(left: 10.0),
                                          ),
                                          Flexible(
                                            child: Text(
                                              userMap.values
                                                  .elementAt(index)
                                                  .elementAt(i)
                                                  .name,
                                              style: userMap.values
                                                  .elementAt(index)
                                                  .elementAt(i)
                                                  .isDone
                                                  ? TextStyle(
                                                decoration: TextDecoration
                                                    .lineThrough,
                                                color: Colors.white70,
                                                fontSize: 17.0,
                                              )
                                                  : TextStyle(
                                                color: Colors.white,
                                                fontSize: 17.0,
                                              ),
                                            ),
                                          ),
                                        ],
                                      )
                                  );
                                }),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        );
      });
    }
  }

  @override
  void initState() {
    super.initState();

    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
  }

  void _addItemPressed() async {
    Navigator.of(context).push(
      new PageRouteBuilder(
        pageBuilder: (_, __, ___) => new NewItemPage(
          user: widget.user,
        ),
        transitionsBuilder: (context, animation, secondaryAnimation, child) =>
        new ScaleTransition(
          scale: new Tween<double>(
            begin: 1.5,
            end: 1.0,
          ).animate(
            CurvedAnimation(
              parent: animation,
              curve: Interval(
                0.50,
                1.00,
                curve: Curves.linear,
              ),
            ),
          ),
          child: ScaleTransition(
            scale: Tween<double>(
              begin: 0.0,
              end: 1.0,
            ).animate(
              CurvedAnimation(
                parent: animation,
                curve: Interval(
                  0.00,
                  0.50,
                  curve: Curves.linear,
                ),
              ),
            ),
            child: child,
          ),
        ),
      ),
    );
  }
}
