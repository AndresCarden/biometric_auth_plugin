import 'package:biometric_auth_plugin_example/biometric_auth_plugin.dart';
import 'package:flutter/material.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  Future<void> _authenticate() async {
    bool success = await BiometricAuthPlugin.authenticate();
    print(success);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text("Biometric Plugin")),
        body: Center(
          child: ElevatedButton(
            onPressed: _authenticate,
            child: Text("Autenticar con Biometría"),
          ),
        ),
      ),
    );
  }
}
