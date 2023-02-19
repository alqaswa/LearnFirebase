package com.example.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity2 : AppCompatActivity()
{
    private lateinit var edtTxt:AppCompatEditText
    private lateinit var txtView:AppCompatTextView
    private lateinit var btnShow:AppCompatButton
    private lateinit var btnRun:AppCompatButton

    private lateinit var mDatabase:FirebaseDatabase
    private lateinit var mReference:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        viewInitialize()

        mDatabase=FirebaseDatabase.getInstance()
        mReference=mDatabase.reference

        btnRun.setOnClickListener(View.OnClickListener { mReference.setValue("yes") })
    }

    private fun runCode(view: View)
    {
        val input=edtTxt.text.toString()
        mReference.setValue(input)
    }

    private fun viewInitialize()
    {
        edtTxt=findViewById(R.id.edtTextK)
        txtView=findViewById(R.id.txtViewK)
        btnShow=findViewById(R.id.btnShowK)
        btnRun=findViewById(R.id.btnRunK)

    }
}