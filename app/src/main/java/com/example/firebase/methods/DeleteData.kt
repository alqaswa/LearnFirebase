package com.example.firebase.methods

import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase

object DeleteData
{
    fun deleteData(childId:String):Task<Void>
    {
        val task=FirebaseDatabase.getInstance().reference.child(childId).setValue(null)
        return task
    }
}