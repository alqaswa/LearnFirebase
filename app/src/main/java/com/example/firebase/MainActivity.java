package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
{
    AppCompatButton btnRun,btnShow;
    AppCompatEditText edtText;
    AppCompatTextView txtView;

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewInitialize();
        mDatabase=FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference();

        btnRun.setOnClickListener(this::runCode);


    }

    private void runCode(View view)
    {
        String input=edtText.getText().toString();
        mReference.setValue(input);
    }

    private void viewInitialize()
    {
        btnRun=findViewById(R.id.btnRun);
        btnShow=findViewById(R.id.btnShow);
        edtText=findViewById(R.id.edtText);
        txtView=findViewById(R.id.txtView);
    }
}