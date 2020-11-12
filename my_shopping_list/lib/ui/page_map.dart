import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart' as auth;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import 'package:myshoppinglist/model/element.dart';
import 'package:geolocator/geolocator.dart';



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

    _timerLocationUpdate = new Timer.periodic(new Duration(seconds: 1), (Timer t) => _updateLocation());
    _timerCameraUpdate = new Timer.periodic(new Duration(seconds: 2), (Timer t) => _updateCamera());

    _updateLocation();

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


  _updateCamera() {
    if (_currentPosition != null) {
      setState(() async {
        final GoogleMapController controller = await _controller.future;
        double newZoom = await controller.getZoomLevel();
        controller.animateCamera(
            CameraUpdate.newCameraPosition(
                new CameraPosition(
                    target: _currentPosition,
                    zoom: newZoom)));
      });
    }
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


