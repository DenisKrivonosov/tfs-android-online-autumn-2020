package ru.krivonosovdenis.myapp.data_classes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactData(
    val contactName:String,
    val contactNumber: String,
): Parcelable

