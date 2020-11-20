import 'dart:async';

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart' as auth;
import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:myshoppinglist/model/element.dart';

const double _defaultZoom = 14;


class SetLocationPage extends StatefulWidget {
  final auth.User user;
  final int i;
  final Map<String, List<ElementItem>> currentList;
  final String color;

  SetLocationPage({Key key, this.user, this.i, this.currentList, this.color})
      : super(key: key);


  @override
  State<StatefulWidget> createState() => _SetLocationPageState();
}

final Map<String, Marker> _markers = {};


class _SetLocationPageState extends State<SetLocationPage>
    with SingleTickerProviderStateMixin {
  Completer<GoogleMapController> _controller = Completer();
  CameraPosition _cameraPosition;
  LatLng _currentPosition;
  Color currentColor;

  void _onMapCreated(GoogleMapController controller) {
    _controller.complete(controller);
  }

  @override
  void initState() {
    super.initState();
    currentColor = Color(int.parse(widget.color));
    _initMap();
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
  }

  @override
  void dispose() {
    super.dispose();
  }

  _initMap() async {
    print("Initiating map");
    bool saved = await _loadSavedLocation();
    if(!saved) {
      print("No saved location");
      await _getCurrentPos();
      return;
    }
    print("Using saved location");
    // await _setInitCamera();
  }


  _loadSavedLocation() {
    // Returns true if there's a saved location
    FirebaseFirestore.instance
        .collection(widget.user.uid)
        .doc(widget.currentList.keys.elementAt(widget.i))
        .get()
        .then((DocumentSnapshot documentSnapshot) {
          // Check if there's an existing setlocation
          if (documentSnapshot.exists && documentSnapshot.data()['_location'] != null) {
            _markers.clear();
            var location = documentSnapshot.data()['_location'];
            // Put it on the map
            setState(() {
              _currentPosition = LatLng(location[0], location[1]);
              final marker = Marker(
                markerId: MarkerId(widget.currentList.keys.elementAt(widget.i)),
                position: _currentPosition,
                infoWindow: InfoWindow(
                  title: widget.currentList.keys.elementAt(widget.i),
                  snippet: "",
                ),
              );
              _markers[widget.currentList.keys.elementAt(widget.i)] = marker;
            });
            return true;
          }
        });
    return false;
  }

  _getCurrentPos() async {
    Position position = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    setState(() {
      _currentPosition = LatLng(
          position.latitude,
          position.longitude);
    });
  }

  _setInitCamera() async {
    if (_currentPosition != null) {
      setState(() {
        _cameraPosition = new CameraPosition(
            target: _currentPosition,
            zoom: _defaultZoom);
      });
    }
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
            color: currentColor,
          ),
        ),
      ]),
    );
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
                  children: _currentPosition == null ?
                  [ Container(
                    child: Center(child:Text('Loading map...', style: TextStyle(fontFamily: 'Avenir-Medium', color: Colors.grey[400]),),),)]
                      :
                  [ Container(
                        width: MediaQuery.of(context).size.width,  // or use fixed size like 200
                        height: MediaQuery.of(context).size.height - 250.0,
                        child: GoogleMap(
                          gestureRecognizers: Set()
                            ..add(Factory<PanGestureRecognizer>(() => PanGestureRecognizer()))
                            ..add(Factory<VerticalDragGestureRecognizer>(
                                    () => VerticalDragGestureRecognizer())),
                          onMapCreated: _onMapCreated,
                          initialCameraPosition: CameraPosition(
                            target: _currentPosition,
                            zoom: _defaultZoom),
                          onTap: (LatLng latLng) {
                            FirebaseFirestore.instance
                                .collection(widget.user.uid)
                                .doc(
                                widget.currentList.keys.elementAt(widget.i))
                                .update(
                                {"_location": latLng.toJson()});
                            setState(() {
                              final marker = Marker(
                                markerId: MarkerId(widget.i.toString()),
                                position: latLng,
                                infoWindow: InfoWindow(
                                  title: widget.i.toString(),
                                  snippet: "",
                                ),
                              );
                              _markers[widget.i.toString()] = marker;
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
}


