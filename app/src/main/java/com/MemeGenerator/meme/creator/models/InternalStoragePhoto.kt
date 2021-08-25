package com.MemeGenerator.meme.creator.models

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class InternalStoragePhoto(
    val name: String,
    val bitmap: Bitmap
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Bitmap::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeParcelable(bitmap, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InternalStoragePhoto> {
        override fun createFromParcel(parcel: Parcel): InternalStoragePhoto {
            return InternalStoragePhoto(parcel)
        }

        override fun newArray(size: Int): Array<InternalStoragePhoto?> {
            return arrayOfNulls(size)
        }
    }
}
