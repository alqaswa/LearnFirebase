package com.example.firebase.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import com.example.firebase.R
import com.example.firebase.model.Person

class DetailFragment:Fragment()
{

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, savedInstanceState:Bundle?):View?
    {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view:View, savedInstanceState:Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val txtView:AppCompatTextView=view.findViewById(R.id.fragTxtView)

        val bundle=arguments
        val person:Person?

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
        {
            person=bundle?.getParcelable("person",Person::class.java)
            txtView.text="${person?.name} and ${person?.age}"
        }
        else
        {
            person=bundle?.getParcelable("person")
            txtView.text="${person?.name} and ${person?.age}"
        }
    }
}