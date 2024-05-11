package id.slava.nt.cabifymobilechallengeapp.data.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import id.slava.nt.cabifymobilechallengeapp.data.remote.RuntimeTypeAdapterFactory
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.ProductDiscount

// Gson with RuntimeTypeAdapterFactory for handling complex JSON for discounts
fun getGsonWithTypeAdapterFactory(): Gson =
 GsonBuilder()
    .registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory.of(ProductDiscount::class.java, "type")
            .registerSubtype(ProductDiscount.BuyXGetYFree::class.java, "BuyXGetYFree")
            .registerSubtype(ProductDiscount.BulkDiscount::class.java, "BulkDiscount"))
    .create()