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
  final int i;
  final Map<String, List<ElementItem>> currentList;
  final String color;

  SetLocationPage({Key key, this.user, this.i, this.currentList, this.color})
      : super(key: key);
  ///


  // SetLocationPage({Key key, this.user}) : super(key: key);

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