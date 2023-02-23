package com.example.firebase.model

import android.os.Parcel
import android.os.Parcelable


/*Here we set default values to the arguments to behave like parameterised as well no argument or default constructor because while getting data
* from firebase we use generic class in getValue() method and for this our class must have a default or no argument constructor*/

data class Person(var name:String?=null,var age:Int?=null):Parcelable
{
    constructor(parcel:Parcel):this(
            parcel.readString(), parcel.readValue(Int::class.java.classLoader) as? Int
                                   )
    {
    }

    override fun writeToParcel(parcel:Parcel, flags:Int)
    {
        parcel.writeString(name)
        parcel.writeValue(age)
    }

    override fun describeContents():Int
    {
        return 0
    }

    companion object CREATOR:Parcelable.Creator<Person>
    {
        override fun createFromParcel(parcel:Parcel):Person
        {
            return Person(parcel)
        }

        override fun newArray(size:Int):Array<Person?>
        {
            return arrayOfNulls(size)
        }
    }

}