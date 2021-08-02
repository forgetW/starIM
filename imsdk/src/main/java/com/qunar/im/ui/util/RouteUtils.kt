//package com.qunar.im.ui.util
//
//import android.util.Log
//import com.hellobike.flutter.thrio.navigator.RouteSettings
//import com.hellobike.flutter.thrio.navigator.ThrioNavigator
//import java.io.Serializable
//
//class RouteUtils {
//    fun onNotify(data: Serializable): RouteSettings? {
//        return RouteSettings.fromArguments(data as Map<String, Any?>)
//    }
//
//    fun startToApp() {
//        ThrioNavigator.push("/flutter/app",
//                params = People(mapOf(
//                        "name" to "foxsofter",
//                        "age" to 100,
//                        "sex" to "x")),
//                result = {
//                    Log.i("Thrio", "push result data $it")
//                },
//                poppedResult = {
//                    Log.i("Thrio", "/biz1/native1 poppedResult call params $it")
//                }
//        )
//    }
//}