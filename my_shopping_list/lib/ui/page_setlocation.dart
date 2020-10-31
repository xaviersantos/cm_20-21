import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart' as auth;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:myshoppinglist/model/element.dart';


class SetLocationPage extends StatefulWidget {
  final auth.User user;
  ///
  // final int i;
  // final Map<String, List<ElementItem>> currentList;
  // final String color;

  // SetLocationPage({Key key, this.user, this.i, this.currentList, this.color})
  //     : super(key: key);
  ///


  SetLocationPage({Key key, this.user}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _SetLocationPageState();
}

final Map<String, Marker> _markers = {};

class _SetLocationPageState extends State<SetLocationPage>
    with SingleTickerProviderStateMixin {
  Completer<GoogleMapController> _controller = Completer();

  static const LatLng _center = const LatLng(40.630107, -8.657132);

  void _onMapCreated(GoogleMapController controller) {
    _controller.complete(controller);

    // setState(() {
    //   _markers.clear();
    // });
  }

  @override
  Widget build(BuildContext context) {
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
                        flex: 2,
                        child: new Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: <Widget>[
                            Text(
                              'Set',
                              style: new TextStyle(
                                  fontSize: 30.0, fontWeight: FontWeight.bold),
                            ),
                            Text(
                              'Location',
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
            ],
          ),
          _getToolbar(context),
          Padding(
              padding:
              EdgeInsets.only(top: 35.0, left: 20.0, right: 20.0),
              child: Column(
                  children: [
                    Container(
                        width: MediaQuery.of(context).size.width,  // or use fixed size like 200
                        height: MediaQuery.of(context).size.height - 250.0,
                        child: GoogleMap(
                          onMapCreated: _onMapCreated,
                          initialCameraPosition: CameraPosition(
                            target: _center,
                            zoom: 11.0,
                          ),
                          onTap: (LatLng latLng) {
                            setState(() {
                              var lat = latLng.latitude;
                              var lng = latLng.longitude;
                              var i = _markers.length.toString();
                              final marker = Marker(
                                markerId: MarkerId(i),
                                position: LatLng(lat, lng),
                                infoWindow: InfoWindow(
                                  title: "Nome do sitio?",
                                  snippet: "Nome da lista?",
                                ),
                              );
                              _markers[i] = marker;
                            });
                          },
                          markers: _markers.values.toSet(),     // Add markers
                        )
                    )
                  ]
              )
          ),
        ],
      ),
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

  Padding _getToolbar(BuildContext context) {
    return new Padding(
      padding: EdgeInsets.only(top: 10.0, left: 20.0, right: 12.0),
      child:
      new Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
        new GestureDetector(
          onTap: () {
            Navigator.of(context).pop();
          },
          child: new Icon(
            FontAwesomeIcons.arrowLeft,
            size: 40.0,
            // color: currentColor, //TODO
          ),
        ),
      ]),
    );
  }
}

// import 'package:cloud_firestore/cloud_firestore.dart';
// import 'package:firebase_auth/firebase_auth.dart' as auth;
// import 'package:flutter/material.dart';
// import 'package:flutter_colorpicker/flutter_colorpicker.dart';
// import 'package:flutter_slidable/flutter_slidable.dart';
// import 'package:font_awesome_flutter/font_awesome_flutter.dart';
// import 'package:myshoppinglist/model/element.dart';
// import 'package:myshoppinglist/utils/diamond_fab.dart';
// import 'dart:async';
//
// import 'package:flutter/services.dart';
//
// class SetLocationPage extends StatefulWidget {
//   final auth.User user;
//   final int i;
//   final Map<String, List<ElementItem>> currentList;
//   final String color;
//
//   SetLocationPage({Key key, this.user, this.i, this.currentList, this.color})
//       : super(key: key);
//
//   @override
//   State<StatefulWidget> createState() => _SetLocationPageState();
// }
//
// class _SetLocationPageState extends State<SetLocationPage> {
//   TextEditingController itemController = new TextEditingController();
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       backgroundColor: Colors.white,
//       resizeToAvoidBottomPadding: false,
//       body: new Stack(
//         children: <Widget>[
//           _getToolbar(context),
//           Container(
//             child: NotificationListener<OverscrollIndicatorNotification>(
//               onNotification: (overscroll) {
//                 overscroll.disallowGlow();
//                 return false;
//               },
//               child: new StreamBuilder<QuerySnapshot>(
//                   stream: FirebaseFirestore.instance
//                       .collection(widget.user.uid)
//                       .snapshots(),
//                   builder: (BuildContext context,
//                       AsyncSnapshot<QuerySnapshot> snapshot) {
//                     if (!snapshot.hasData)
//                       return new Center(
//                           child: CircularProgressIndicator(
//                             backgroundColor: currentColor,
//                           ));
//                     return new Container(
//                       child: getExpenseItems(snapshot),
//                     );
//                   }),
//             ),
//           ),
//         ],
//       ),
//       floatingActionButton: DiamondFab(
//         onPressed: () {
//           showDialog(
//             context: context,
//             builder: (BuildContext context) {
//               return AlertDialog(
//                 content: Row(
//                   children: <Widget>[
//                     Expanded(
//                       child: new TextField(
//                         autofocus: true,
//                         decoration: InputDecoration(
//                             border: new OutlineInputBorder(
//                                 borderSide: new BorderSide(
//                                     color: currentColor)),
//                             labelText: "Item",
//                             hintText: "Item",
//                             contentPadding: EdgeInsets.only(
//                                 left: 16.0,
//                                 top: 20.0,
//                                 right: 16.0,
//                                 bottom: 5.0)),
//                         controller: itemController,
//                         style: TextStyle(
//                           fontSize: 22.0,
//                           color: Colors.black,
//                           fontWeight: FontWeight.w500,
//                         ),
//                         keyboardType: TextInputType.text,
//                         textCapitalization: TextCapitalization.sentences,
//                       ),
//                     )
//                   ],
//                 ),
//                 actions: <Widget>[
//                   ButtonTheme(
//                     //minWidth: double.infinity,
//                     child: RaisedButton(
//                       elevation: 3.0,
//                       onPressed: () {
//                         if (itemController.text.isNotEmpty &&
//                             !widget.currentList.values
//                                 .contains(itemController.text.toString())) {
//                           FirebaseFirestore.instance
//                               .collection(widget.user.uid)
//                               .doc(
//                               widget.currentList.keys.elementAt(widget.i))
//                               .update(
//                               {itemController.text.toString(): false});
//
//                           itemController.clear();
//                           Navigator.of(context).pop();
//                         }
//                       },
//                       child: Text('Add'),
//                       color: currentColor,
//                       textColor: const Color(0xffffffff),
//                     ),
//                   )
//                 ],
//               );
//             },
//           );
//         },
//         child: Icon(Icons.add),
//         backgroundColor: currentColor,
//       ),
//     );
//   }
//
//   @override
//   void dispose() {
//     super.dispose();
//   }
//
//   getExpenseItems(AsyncSnapshot<QuerySnapshot> snapshot) {
//     List<ElementItem> listElement = new List();
//     int nbIsDone = 0;
//
//     if (widget.user.uid.isNotEmpty) {
//       snapshot.data.docs.map<Column>((f) {
//         if (f.id == widget.currentList.keys.elementAt(widget.i)) {
//           f.data().forEach((a, b) {
//             if (b.runtimeType == bool) {
//               listElement.add(new ElementItem(a, b));
//             }
//           });
//         }
//       }).toList();
//
//       listElement.forEach((i) {
//         if (i.isDone) {
//           nbIsDone++;
//         }
//       });
//
//       return Column(
//         children: <Widget>[
//           Padding(
//             padding: EdgeInsets.only(top: 150.0),
//             child: new Column(
//               children: <Widget>[
//                 Padding(
//                   padding: EdgeInsets.only(top: 5.0, left: 50.0, right: 20.0),
//                   child: Row(
//                     mainAxisAlignment: MainAxisAlignment.spaceBetween,
//                     children: <Widget>[
//                       Flexible(
//                         fit: FlexFit.loose,
//                         child: Text(
//                           widget.currentList.keys.elementAt(widget.i),
//                           softWrap: true,
//                           overflow: TextOverflow.fade,
//                           style: TextStyle(
//                               fontWeight: FontWeight.bold, fontSize: 35.0),
//                         ),
//                       ),
//                       GestureDetector(
//                         onTap: () {
//                           showDialog(
//                             context: context,
//                             builder: (BuildContext context) {
//                               return new AlertDialog(
//                                 title: Text("Delete: " + widget.currentList.keys.elementAt(widget.i).toString()),
//                                 content: Text(
//                                   "Are you sure you want to delete this list?", style: TextStyle(fontWeight: FontWeight.w400),),
//                                 actions: <Widget>[
//                                   ButtonTheme(
//                                     //minWidth: double.infinity,
//                                     child: RaisedButton(
//                                       elevation: 3.0,
//                                       onPressed: () {
//                                         Navigator.pop(context);
//                                       },
//                                       child: Text('No'),
//                                       color: currentColor,
//                                       textColor: const Color(0xffffffff),
//                                     ),
//                                   ),
//                                   ButtonTheme(
//                                     //minWidth: double.infinity,
//                                     child: RaisedButton(
//                                       elevation: 3.0,
//                                       onPressed: () {
//                                         FirebaseFirestore.instance
//                                             .collection(widget.user.uid)
//                                             .doc(widget.currentList.keys
//                                             .elementAt(widget.i))
//                                             .delete();
//                                         Navigator.pop(context);
//                                         Navigator.of(context).pop();
//                                       },
//                                       child: Text('YES'),
//                                       color: currentColor,
//                                       textColor: const Color(0xffffffff),
//                                     ),
//                                   ),
//                                 ],
//                               );
//                             },
//                           );
//                         },
//                         child: Icon(
//                           FontAwesomeIcons.trash,
//                           size: 25.0,
//                           color: currentColor,
//                         ),
//                       ),
//                     ],
//                   ),
//                 ),
//                 Padding(
//                   padding: EdgeInsets.only(top: 5.0, left: 50.0),
//                   child: Row(
//                     children: <Widget>[
//                       new Text(
//                         nbIsDone.toString() +
//                             " of " +
//                             listElement.length.toString() +
//                             " items",
//                         style: TextStyle(fontSize: 18.0, color: Colors.black54),
//                       ),
//                     ],
//                   ),
//                 ),
//                 Padding(
//                   padding: EdgeInsets.only(top: 5.0),
//                   child: Row(
//                     children: <Widget>[
//                       Expanded(
//                         flex: 2,
//                         child: Container(
//                           margin: EdgeInsets.only(left: 50.0),
//                           color: Colors.grey,
//                           height: 1.5,
//                         ),
//                       ),
//                     ],
//                   ),
//                 ),
//                 Padding(
//                   padding: EdgeInsets.only(top: 30.0),
//                   child: Column(
//                     children: <Widget>[
//                       Container(color: Color(0xFFFCFCFC),child:
//                       SizedBox(
//                         height: MediaQuery.of(context).size.height - 350,
//                         child: ListView.builder(
//                             physics: const BouncingScrollPhysics(),
//                             itemCount: listElement.length,
//                             itemBuilder: (BuildContext ctxt, int i) {
//                               return new Slidable(
//                                 actionPane: SlidableDrawerActionPane(),
//                                 actionExtentRatio: 0.25,
//                                 child: GestureDetector(
//                                   onTap: () {
//                                     FirebaseFirestore.instance
//                                         .collection(widget.user.uid)
//                                         .doc(widget.currentList.keys
//                                         .elementAt(widget.i))
//                                         .update({
//                                       listElement.elementAt(i).name:
//                                       !listElement.elementAt(i).isDone
//                                     });
//                                   },
//                                   child: Container(
//                                     height: 50.0,
//                                     color: listElement.elementAt(i).isDone
//                                         ? Color(0xFFF0F0F0)
//                                         : Color(0xFFFCFCFC),
//                                     child: Padding(
//                                       padding: EdgeInsets.only(left: 50.0),
//                                       child: Row(
//                                         mainAxisAlignment:
//                                         MainAxisAlignment.start,
//                                         children: <Widget>[
//                                           Icon(
//                                             listElement.elementAt(i).isDone
//                                                 ? FontAwesomeIcons.checkSquare
//                                                 : FontAwesomeIcons.square,
//                                             color: listElement
//                                                 .elementAt(i)
//                                                 .isDone
//                                                 ? currentColor
//                                                 : Colors.black,
//                                             size: 20.0,
//                                           ),
//                                           Padding(
//                                             padding:
//                                             EdgeInsets.only(left: 30.0),
//                                           ),
//                                           Flexible(
//                                             child: Text(
//                                               listElement.elementAt(i).name,
//                                               overflow: TextOverflow.ellipsis,
//                                               maxLines: 1,
//                                               style: listElement
//                                                   .elementAt(i)
//                                                   .isDone
//                                                   ? TextStyle(
//                                                 decoration: TextDecoration
//                                                     .lineThrough,
//                                                 color: currentColor,
//                                                 fontSize: 27.0,
//                                               )
//                                                   : TextStyle(
//                                                 color: Colors.black,
//                                                 fontSize: 27.0,
//                                               ),
//                                             ),
//                                           ),
//                                         ],
//                                       ),
//                                     ),
//                                   ),
//                                 ),
//                                 secondaryActions: <Widget>[
//                                   new IconSlideAction(
//                                     caption: 'Delete',
//                                     color: Colors.red,
//                                     icon: Icons.delete,
//                                     onTap: () {
//                                       FirebaseFirestore.instance
//                                           .collection(widget.user.uid)
//                                           .doc(widget.currentList.keys
//                                           .elementAt(widget.i))
//                                           .update({
//                                         listElement.elementAt(i).name:
//                                         ""
//                                       });
//                                     },
//                                   ),
//                                 ],
//                               );
//                             }),
//                       ),),
//                     ],
//                   ),
//                 ),
//               ],
//             ),
//           ),
//         ],
//       );
//     }
//   }
//
//   @override
//   void initState() {
//     super.initState();
//     pickerColor = Color(int.parse(widget.color));
//     currentColor = Color(int.parse(widget.color));
//   }
//
//   Color pickerColor;
//   Color currentColor;
//
//   ValueChanged<Color> onColorChanged;
//
//   changeColor(Color color) {
//     setState(() => pickerColor = color);
//   }
//
//   Padding _getToolbar(BuildContext context) {
//     return new Padding(
//       padding: EdgeInsets.only(top: 50.0, left: 20.0, right: 12.0),
//       child:
//       new Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
//         new GestureDetector(
//           onTap: () {
//             Navigator.of(context).pop();
//           },
//           child: new Icon(
//             FontAwesomeIcons.arrowLeft,
//             size: 40.0,
//             color: currentColor,
//           ),
//         ),
//         RaisedButton(
//           elevation: 3.0,
//           onPressed: () {
//             pickerColor = currentColor;
//             showDialog(
//               context: context,
//               builder: (BuildContext context) {
//                 return AlertDialog(
//                   titlePadding: const EdgeInsets.all(0.0),
//                   contentPadding: const EdgeInsets.all(0.0),
//                   content: SingleChildScrollView(
//                     child: ColorPicker(
//                       pickerColor: currentColor,
//                       onColorChanged: changeColor,
//                       colorPickerWidth: 300.0,
//                       pickerAreaHeightPercent: 0.7,
//                       enableAlpha: false,
//                       displayThumbColor: true,
//                       showLabel: false,
//                       paletteType: PaletteType.hsv,
//                       pickerAreaBorderRadius: const BorderRadius.only(
//                         topLeft: const Radius.circular(2.0),
//                         topRight: const Radius.circular(2.0),
//                       ),
//                     ),
//                   ),
//                   actions: <Widget>[
//                     FlatButton(
//                       child: Text('Got it'),
//                       onPressed: () {
//
//                         FirebaseFirestore.instance
//                             .collection(widget.user.uid)
//                             .doc(
//                             widget.currentList.keys.elementAt(widget.i))
//                             .update(
//                             {"color": pickerColor.value.toString()});
//
//                         setState(
//                                 () => currentColor = pickerColor);
//                         Navigator.of(context).pop();
//                       },
//                     ),
//                   ],
//                 );
//               },
//             );
//           },
//           child: Text('Color'),
//           color: currentColor,
//           textColor: const Color(0xffffffff),
//         ),
//         GestureDetector(
//           onTap: () {
//             Navigator.of(context).push(
//               new PageRouteBuilder(
//                 pageBuilder: (_, __, ___) => new SetLocationPage( // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
//                   user: widget.user,
//                   // i: i,
//                   // currentList: this.userMap,
//                   // color: cardColor.elementAt(index),
//                 ),
//                 transitionsBuilder:
//                     (context, animation, secondaryAnimation, child) =>
//                 new ScaleTransition(
//                   scale: new Tween<double>(
//                     begin: 1.5,
//                     end: 1.0,
//                   ).animate(
//                     CurvedAnimation(
//                       parent: animation,
//                       curve: Interval(
//                         0.50,
//                         1.00,
//                         curve: Curves.linear,
//                       ),
//                     ),
//                   ),
//                   child: ScaleTransition(
//                     scale: Tween<double>(
//                       begin: 0.0,
//                       end: 1.0,
//                     ).animate(
//                       CurvedAnimation(
//                         parent: animation,
//                         curve: Interval(
//                           0.00,
//                           0.50,
//                           curve: Curves.linear,
//                         ),
//                       ),
//                     ),
//                     child: child,
//                   ),
//                 ),
//               ),
//             );
//           },
//           child: new Icon(
//             FontAwesomeIcons.mapMarkedAlt,
//             size: 40.0,
//             color: currentColor,
//           ),
//         ),
//       ]),
//     );
//   }
// }