import 'package:flutter/material.dart';

class Routes {
  static const String splashPage = '/';
  static const String signInPage = '/sign-in-page';
  static const String mainPage = '/main-page';

  static PageRoute<dynamic>? onGenerateRoute({
    required BuildContext context,
    required RouteSettings settings,
    required bool hasNetworkConnection,
  }) {
    if (!hasNetworkConnection) {
      // return getNetworkNotFound();
    }
    return null;
  }

  // static PageRoute getNetworkNotFound() => MaterialPageRoute(
  //       builder: (_) => const NoConnection(),
  //     );
}
