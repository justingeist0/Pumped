package com.fantasmaplasma.beta.utilities

import android.util.Log
import com.fantasmaplasma.beta.data.Route
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

object Cloud {
    private const val ROUTE = "route"
    private const val NAME = "name"
    private const val BETA_SCALE = "betaScale"
    private const val HEIGHT = "height"
    private const val COMMENT_DATA = "commentData"
    private const val USER_ID = "userID"
    private const val LATITUDE = "latitude"
    private const val LONGITUDE = "longitude"
    private const val CATEGORY_ID = "categoryID"
    private const val TEXT = "text"
    private const val POINTS = "points"

    fun downloadRouteClusterItems(onComplete: (MutableList<Route>) -> Unit) {
        FirebaseFirestore.getInstance().collection("route")
            .get().addOnSuccessListener { querySnapshot ->
                val routeInfo = mutableListOf<Route>()
                querySnapshot.forEach { routeJson ->
                    try {
                        routeInfo.add(Route(
                            latLng = LatLng(
                                routeJson.getDouble(LATITUDE)!!,
                                routeJson.getDouble(LONGITUDE)!!
                            ),
                            name = routeJson.getString(NAME)!!,
                            height = routeJson.getLong(HEIGHT)!!.toInt(),
                            betaScale = routeJson.getLong(BETA_SCALE)!!.toInt(),
                            userID = routeJson.getString(USER_ID)!!,
                            type = routeJson.getLong(CATEGORY_ID)!!.toInt()
                        ))
                    } catch (e: Exception) {}
                }
                onComplete(routeInfo)
            }
    }

    fun uploadRoute(routeStandbyData: HashMap<String, Any>, nameData: HashMap<String, Any>,
                            heightData: HashMap<String, Any>, betaScaleData: HashMap<String, Any>, commentData: HashMap<String, Any>) {
        with(Cloud) {
            val routesRef = FirebaseFirestore.getInstance().collection(ROUTE)
            routesRef.document().apply {
                set(routeStandbyData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded Standby Info") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }

                routesRef.document(id)
                    .collection(NAME)
                    .add(nameData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded ID") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }

                routesRef.document(id)
                    .collection(HEIGHT)
                    .add(heightData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded Height") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }

                routesRef.document(id)
                    .collection(BETA_SCALE)
                    .add(betaScaleData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded Scale") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }

                routesRef.document(id)
                    .collection(COMMENT_DATA)
                    .add(commentData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded Comment") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }
            }
        }
    }

    fun createRouteStandByHashMap(
        userID: String,
        name: String,
        latitude: Double,
        longitude: Double,
        height: Int,
        categoryID: Int,
        betaScale: Int
    )
            : HashMap<String, Any> =
        hashMapOf(
            USER_ID to userID,
            NAME to name,
            LATITUDE to latitude,
            LONGITUDE to longitude,
            HEIGHT to height,
            CATEGORY_ID to categoryID,
            BETA_SCALE to betaScale
        )

    fun createCommentDataHashMap(
        userID: String,
        text: Any
    )
            : HashMap<String, Any> =
        hashMapOf (
            USER_ID to userID,
            TEXT to text,
            POINTS to 1
        )

    fun createUserDataHashMap(
        userID: String
    )
            : HashMap<String, Any> =
        hashMapOf (
            USER_ID to userID
        )

    fun createActionDataHashMap(
        collectionID: String,
        subCollectionID: String,
        commentID: String,
        text: String,
        timeStamp: Long
    )
            : HashMap<String, Any> =
        hashMapOf (
            "collectionID" to collectionID,
            "subCollectionID" to subCollectionID,
            "postID" to commentID,
            "text" to text,
            "timestamp" to timeStamp
        )

}