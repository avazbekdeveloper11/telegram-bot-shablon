import Foundation
import BanubaVideoEditorSDK

class VideoEditorModule {
  
  private var videoEditorSDK: BanubaVideoEditor?
  
  func openVideoEditor(
    fromViewController controller: FlutterViewController
  ) {
    let config = createVideoEditorConfiguration()
    videoEditorSDK = BanubaVideoEditor(
      token:"ABdBkNCx0uNOVYy70A3h4jRKfEC8pkAjGbiOTzWJ/EKJo2vM5RwzNSiMUK6fDPR7rf8TORy7AP4vTj3kRA74G/xpiqHmINvaV8AtZ6e7gj4R0el0Yz2uiCvy+0SP0ei7TB6xdswA+sge2FDzWmRGC0L4gVEUB71egtLOz8iH+hw1Iz13GDrjOh7VE0HREvF5W+U+KucssoqCFI63gGjZZAVmaxmpWdW/Gh+VxXnIswWoDe8w24O4YdZA4cVX42vp1FErq/Jakv7G06Ux7M7Zv7Rr8hx4l1NNsaW8qdZXgQJIOzP07aglarNM/zG80+olBxzJTLG79XsEiLKNbnrYIfdfipWKlAUAJS0O0SGsjnM2JRVG0jbuxQFJjuZ+XhMhTJxF+FPo2zTw7JXUcA8cIL0zwrm9GNilBB98+PpiToF4iFJD0RGB2aKTJXIiMdyIeQme4K1BEP/tOIUrglNDt6Ww/xlA5c+pxncJbL9ogRbnoWNCFCuWXSo1g7hM6hRKEQ5tOdcyrHSyBKf4zziFYfnUbw9rYBEge8dRIxf1RxXbF8/UlQTpsDHB2VH+6kNDD+U18lGN8xj0d8b6KDle3GZJDsZ8deGMPG0Le7knmpjtRWVyhZJwQe1jyQ7hbg04GQOBsvc5fFa5sc4781sS1bSBpaJI8ywJOoQnst88mOIPDWwHSWwQYj0FIjbXKWjSXubbEYy19PVCHbIfGOFsOZJJfer3Mtowoix7cO51NV8w/NXdXcPaRBEOxBBX0kdhTcNHT6lg5wD+RW/JvMCEYDYSj9pHtT2R6fz2K7UdxaZK45mGiDaprteWHCWP34pQEsO6NNcCNYqcMRC+D4MzqQuA6GIYtjYuH2J8MLBamktZQk1FJYF0kiW+wcoh+GtlQw+9NgFTr/Z7LKrDvgxoWEV/mvIJoET4HzLhEwtL3xBQbOUwFI5gEiwohKCoP1y7lE5yp8St/r4lqOLQkRUdZQpjtDgBAd+/7euO6RJw48No3NTU3rxWcnXulvShzrQleldoSjKWZiMweibmOIthIsMK3ZBQv582tRoP59g6gcoY2KuBkQQjcd5UUWgFCpP7ebtP++3u+ha309mHUFxwA4Y8mmdTwXPPzw5/UEN3+bNEZE7yoF8Pt2ks+DFrHQubWslsdlBzZoXWu5mdlUWjUxIqLs3qmhFAsgm7hLNh3RNBpUIAoCYVr+mssCURfP6kz5OSJaAL2zRp1ti3vNP5vtBZb+yUzEuX0sGmQqswv7bsViVMLQY0JQRVVfqviSoTHZJvlf5wmdA8Uq5xFFbFHuAW2q/0PpP7HF5QM93dDUFD5up2Qb2no0qjHHrSnAnHoTDnPNj9xZXHOv49kiKoGKASaKpQD+H5KtiLTIyRfi29iVefSvOELvOcJMWn8Mhk1qV5wjRWhseg6Suim5huNZ1wT+zTOaN0JhVnf3WTGdL4yWOVzcLPHfwsCM+Dxl1ubtUCcpipcPG5BTHGoao=",
         configuration: config,
      externalViewControllerFactory: nil
    )
    videoEditorSDK?.delegate = self
    DispatchQueue.main.async {
      self.videoEditorSDK?.presentVideoEditor(
        from: controller,
        animated: true,
        completion: nil
      )
    }
  }
  
  private func createVideoEditorConfiguration() -> VideoEditorConfig {
    let config = VideoEditorConfig()
    // Do customization here
    return config
  }
}

// MARK: - BanubaVideoEditorSDKDelegate
extension VideoEditorModule: BanubaVideoEditorDelegate {
  func videoEditorDidCancel(_ videoEditor: BanubaVideoEditor) {
    videoEditor.dismissVideoEditor(animated: true) {
      // remove strong reference to video editor sdk instance
      self.videoEditorSDK = nil
    }
  }
  
  func videoEditorDone(_ videoEditor: BanubaVideoEditor) {
    // Do export stuff here
  }
}

