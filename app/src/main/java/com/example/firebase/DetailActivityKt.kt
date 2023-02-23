package com.example.firebase

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.example.firebase.fragments.DetailFragment
import com.example.firebase.model.Person

class DetailActivityKt:AppCompatActivity()
{
    private lateinit var fragment:DetailFragment

    override fun onCreate(savedInstanceState:Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kt)

       val bundle=intent.getBundleExtra("bundle")

        fragment=DetailFragment()

        fragment.arguments=bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view,fragment,"details_fragment").commit()
    }
}