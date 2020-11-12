import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart' as auth;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import 'package:myshoppinglist/model/element.dart';
import 'package:geolocator/geolocator.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart' as auth;
import 'package:wakelock/wakelock.dart';

final Map<String, Marker> _markers = {};

const double _defaultZoom = 15;
CameraPosition _cameraPosition;
LatLng _currentPosition;

class MapPage extends StatefulWidget {
  final auth.User user;

  MapPage({Key key, this.user}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _MapPageState();
}


class _MapPageState extends State<MapPage> with SingleTickerProviderStateMixin {

  Completer<GoogleMapController> _controller = Completer();
  Timer _timerLocationUpdate;
  Timer _timerCameraUpdate;

  @override
  void initState() {
    super.initState();

    Wakelock.enable();

    _updateLocation();
    _loadListMarkers();

    _timerLocationUpdate = new Timer.periodic(new Duration(seconds: 1), (Timer t) => _updateLocation());
    _timerCameraUpdate = new Timer.periodic(new Duration(seconds: 2), (Timer t) => _updateCamera());

    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
  }


  @override
  void dispose() {
    super.dispose();
    _timerLocationUpdate.cancel();
    _timerCameraUpdate.cancel();
  }


  void _onMapCreated (GoogleMapController controller) {
    setState(() {
      _controller.complete(controller);
    });
  }

  void _updateLocation() async {
    Position position = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    setState(()  {
      _currentPosition = LatLng(
          position.latitude,
          position.longitude);
      _markers['self'] = Marker(
        markerId: MarkerId('self'),
        position: LatLng(
            _currentPosition.latitude,
            _currentPosition.longitude),
        icon: BitmapDescriptor.defaultMarker,
        infoWindow: InfoWindow(
          title: "You",
        ),
      );
      _cameraPosition = new CameraPosition(
          target: _currentPosition,
          zoom: _defaultZoom);
    });
  }


  _updateCamera() async {
    if (_currentPosition != null) {
      final GoogleMapController controller = await _controller.future;
      double newZoom = await controller.getZoomLevel();
      setState(() {
        controller.animateCamera(
            CameraUpdate.newCameraPosition(
                new CameraPosition(
                    target: _currentPosition,
                    zoom: newZoom)));
      });
    }
  }


  _loadListMarkers() { //async {
    Stream<QuerySnapshot> snapshop = FirebaseFirestore.instance
        .collection(widget.user.uid)
        .snapshots();
    if (widget.user.uid.isNotEmpty) {
      print("\n\n\n-------------\nNot empty");
    }
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
        String location;
        // String location;
        f.data().forEach((a, b) {
          if (a == "_location") {
            location = b;
            print("Found location:" + b.toString());
          }
          // if (b.runtimeType == bool) {
          //   listElement.add(new ElementItem(a, b));
          // }
          // if (b.runtimeType == String && a == "color") {
          //   color = b;
          // }
          // if (b.runtimeType == String && a == "_location") { //TODO <<<<<<<<<<<<<<<<<<<<<
          //   location = b;
          // }
        });
        listElement2 = new List<ElementItem>.from(listElement);
        for (int i = 0; i < listElement2.length; i++) {
          if (listElement2
              .elementAt(i)
              .isDone == false) {
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
    }
    //
    //   return new List.generate(userMap.length, (int index) {
    //     return new GestureDetector(
    //       onTap: () {
    //         Navigator.of(context).push(
    //           new PageRouteBuilder(
    //             pageBuilder: (_, __, ___) => new DetailPage(
    //               user: widget.user,
    //               i: index,
    //               currentList: userMap,
    //               color: cardColor.elementAt(index),
    //             ),
    //             transitionsBuilder:
    //                 (context, animation, secondaryAnimation, child) =>
    //             new ScaleTransition(
    //               scale: new Tween<double>(
    //                 begin: 1.5,
    //                 end: 1.0,
    //               ).animate(
    //                 CurvedAnimation(
    //                   parent: animation,
    //                   curve: Interval(
    //                     0.50,
    //                     1.00,
    //                     curve: Curves.linear,
    //                   ),
    //                 ),
    //               ),
    //               child: ScaleTransition(
    //                 scale: Tween<double>(
    //                   begin: 0.0,
    //                   end: 1.0,
    //                 ).animate(
    //                   CurvedAnimation(
    //                     parent: animation,
    //                     curve: Interval(
    //                       0.00,
    //                       0.50,
    //                       curve: Curves.linear,
    //                     ),
    //                   ),
    //                 ),
    //                 child: child,
    //               ),
    //             ),
    //           ),
    //         );
    //       },
    //       child: Card(
    //         shape: RoundedRectangleBorder(
    //           borderRadius: BorderRadius.all(Radius.circular(8.0)),
    //         ),
    //         color: Color(int.parse(cardColor.elementAt(index))),
    //         child: new Container(
    //           width: 220.0,
    //           //height: 100.0,
    //           child: Container(
    //             child: Column(
    //               children: <Widget>[
    //                 Padding(
    //                   padding: EdgeInsets.only(top: 20.0, bottom: 15.0),
    //                   child: Container(
    //                     child: Text(
    //                       userMap.keys.elementAt(index),
    //                       style: TextStyle(
    //                         color: Colors.white,
    //                         fontSize: 19.0,
    //                       ),
    //                     ),
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
    //                           color: Colors.white,
    //                           height: 1.5,
    //                         ),
    //                       ),
    //                     ],
    //                   ),
    //                 ),
    //                 Padding(
    //                   padding:
    //                   EdgeInsets.only(top: 30.0, left: 15.0, right: 5.0),
    //                   child: Column(
    //                     children: <Widget>[
    //                       SizedBox(
    //                         height: 220.0,
    //                         child: ListView.builder(
    //                           //physics: const NeverScrollableScrollPhysics(),
    //                             itemCount:
    //                             userMap.values.elementAt(index).length,
    //                             itemBuilder: (BuildContext ctxt, int i) {
    //                               return Row(
    //                                 mainAxisAlignment: MainAxisAlignment.start,
    //                                 children: <Widget>[
    //                                   Icon(
    //                                     userMap.values
    //                                         .elementAt(index)
    //                                         .elementAt(i)
    //                                         .isDone
    //                                         ? FontAwesomeIcons.checkCircle
    //                                         : FontAwesomeIcons.circle,
    //                                     color: userMap.values
    //                                         .elementAt(index)
    //                                         .elementAt(i)
    //                                         .isDone
    //                                         ? Colors.white70
    //                                         : Colors.white,
    //                                     size: 14.0,
    //                                   ),
    //                                   Padding(
    //                                     padding: EdgeInsets.only(left: 10.0),
    //                                   ),
    //                                   Flexible(
    //                                     child: Text(
    //                                       userMap.values
    //                                           .elementAt(index)
    //                                           .elementAt(i)
    //                                           .name,
    //                                       style: userMap.values
    //                                           .elementAt(index)
    //                                           .elementAt(i)
    //                                           .isDone
    //                                           ? TextStyle(
    //                                         decoration: TextDecoration
    //                                             .lineThrough,
    //                                         color: Colors.white70,
    //                                         fontSize: 17.0,
    //                                       )
    //                                           : TextStyle(
    //                                         color: Colors.white,
    //                                         fontSize: 17.0,
    //                                       ),
    //                                     ),
    //                                   ),
    //                                 ],
    //                               );
    //                             }),
    //                       ),
    //                     ],
    //                   ),
    //                 ),
    //               ],
    //             ),
    //           ),
    //         ),
    //       ),
    //     );
    //   });
    //
    // }
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
                              'Items',
                              style: new TextStyle(
                                  fontSize: 30.0, fontWeight: FontWeight.bold),
                            ),
                            Text(
                              'Nearby',
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
          Padding(
              padding:
              EdgeInsets.only(top: 50.0, left: 20.0, right: 20.0),
              child: Column(
                  children: _cameraPosition == null ?
                    [Container(
                      child: Center(child:Text('Loading map...', style: TextStyle(fontFamily: 'Avenir-Medium', color: Colors.grey[400]),),),)]
                      :
                    [ Container(
                        width: MediaQuery.of(context).size.width,
                        height: MediaQuery.of(context).size.height - 250.0,
                        child: GoogleMap(
                            onMapCreated: _onMapCreated,
                            initialCameraPosition: _cameraPosition,
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
}


