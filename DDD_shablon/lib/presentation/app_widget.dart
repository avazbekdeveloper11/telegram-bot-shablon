import 'package:calendar/presentation/style/style.dart';
import 'package:flutter/material.dart';

class AppWidget extends StatelessWidget {
  const AppWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        body: Center(
          child: Text(
            "Home Page",
            style: Style.normal20(size: 22),
          ),
        ),
      ),
    );
  }
}
