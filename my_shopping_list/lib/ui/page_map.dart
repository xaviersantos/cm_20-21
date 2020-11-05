import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart' as auth;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import 'package:myshoppinglist/model/element.dart';
import 'package:geolocator/geolocator.dart';



final Map<String, Marker> _markers = {};

const LatLng _defaultCenter = const LatLng(40.630107, -8.657132);
const double _defaultZoom = 14;

bool firstCamUpdate = true;
CameraPosition _cameraPosition = new CameraPosition(
  target: _defaultCenter,
  zoom: _defaultZoom,);
final Duration refreshLocationTime = new Duration(seconds: 3);
LatLng _currentPosition;


class MapPage extends StatefulWidget {
  final auth.User user;

  MapPage({Key key, this.user}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _MapPageState();
}




class _MapPageState extends State<MapPage> with SingleTickerProviderStateMixin {

  Completer<GoogleMapController> _controller = Completer();
  Timer _timerLocation;
  Timer _timerCamera;

  void _onMapCreated(GoogleMapController controller) {
    _controller.complete(controller);
    _getCurrentLocation();
  }

  @override
  Widget build(BuildContext context) {
    print("\n\n----------\nbuild, _curr:"+_currentPosition.toString());
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
                  children: [
                    Container(
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

  @override
  void dispose() {
    super.dispose();
    _timerLocation.cancel();
  }

  @override
  void initState() {
    super.initState();
    print("\n\n----------\ninit_state, _curr:"+_currentPosition.toString());

    _timerLocation = new Timer.periodic(new Duration(seconds: 3), (Timer t) => _getCurrentLocation());
    _timerCamera = new Timer.periodic(new Duration(milliseconds: 500), (Timer t) => _setCamera());

    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
  }

  _getCurrentLocation() {
    print("getting location");
    Geolocator
        .getCurrentPosition(desiredAccuracy: LocationAccuracy.best)
        .then((Position position) {
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
                zoom:_defaultZoom);
          });
    }).catchError((e) {
      print(e);
    });
  }

  _setCamera() {
    // firstCamUpdate = false;
    if (_currentPosition != null) {
      setState(() async {
        final GoogleMapController controller = await _controller.future;
        controller.animateCamera(
            CameraUpdate.newCameraPosition(
                new CameraPosition(
                  target: _currentPosition,
                  zoom:_defaultZoom)));
        _timerCamera.cancel();
      });
    }
  }
}


