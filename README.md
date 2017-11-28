# S.E.D.A

SEDA stands for Smart Elegant Driving Assistant. It is a prototype implementation of our design fiction [seda-paper](https://github.com/victorskl/seda-paper). As with prototype, we mimic two mobile phones - one of which act as the [BaseApp](BaseApp) and another is the [ConsoleApp](ConsoleApp). The ConsoleApp makes use of the advanced image processing library OpenCV for Car detection and Lane Departure detection using mono-camera machine visionary. Furthermore, the two Apps make use of Bluetooth for ad-hoc networking for data communication. Then, the BaseApp use mobile data connection to communicate with [SedaBackend](SedaBackend) that run on Azure Cloud for further analysis processing such as driver behaviour classification based on aggregated sensor data samples (throw your Machine Learning techniques @here).

## Demo

- https://www.youtube.com/watch?v=ALhgZJYpjvU

## Build

- BaseApp is just another Android project where you can simple open with Android Studio.

- SedaBackend is just an Azure Cloud Mobile App C# backend; use Visual Studio to open it. It is a combination of [Week7](https://github.com/victorskl/mobile-tute/tree/master/week7) and [Week8](https://github.com/victorskl/mobile-tute/tree/master/week8) of [mobile-tute](https://github.com/victorskl/mobile-tute).

- ConsoleApp is a bit more complex to setup. It is an Android project; like so use Andorid Studio to open it. It has `openCVLibrary330` library as build dependency and, it still requires to include [native JNI wrapper libraries](https://github.com/victorskl/seda/tree/develop/ConsoleApp/openCVLibrary330/src/main/jniLibs) from OpenCV Andorid SDK download. Details of which is further tute in:

  - https://github.com/victorskl/android-opencv-tute
  - https://github.com/victorskl/android-opencv-tute/tree/master/OpenCVLibrary330
  
There still have room to improvement on this Research and Development work. You may wish to cite this work as follow.

LaTeX/BibTeX:
```
@online{seda,
    author    = {San Kho Lin, Bingfeng Liu, Yixin Chen},
    title     = {S.E.D.A - Smart Elegant Driving Assistant Prototype},
    year      = {2017},
    url       = {https://github.com/victorskl/seda},
    urldate   = {yyyy-mm-dd}
}
```
