package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


//We set kotlin activity class to default class in manifest file and hence MainActivity2 is launcher activity


//IN THIS PROJECT WE WILL IMPLEMENT SAME CODE IN JAVA AS WELL IN KOTLIN FOR PRACTICE BUT MORE FOCUS IS ON KOTLIN
public class MainActivity extends AppCompatActivity
{
    AppCompatButton btnSet, btnGet;
    AppCompatEditText edtTextName;
    AppCompatEditText edtTextAge;
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

        btnSet.setOnClickListener(this::setData);
        btnGet.setOnClickListener(this::getData);


    }

    private void setData(View view)
    {
        String input= edtTextName.getText().toString();
        mReference.setValue(input);

        //We and also set the listeners of success and Failure of data insertion in database and to do this we have to
        // change the rules of our firebase database on firebase site
        mReference.setValue(input)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        Toast.makeText(MainActivity.this, "Successfully Inserted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(MainActivity.this, "Permission denied to write data", Toast.LENGTH_SHORT).show();
                    }
                });

        //In terms of lambda
        mReference.setValue(input)
                .addOnFailureListener(e -> Toast.makeText(this, "Successfully Inserted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Permission denied to write data", Toast.LENGTH_SHORT).show());

    }

    private void setDataUsingPush(View view)
    {
        String name=edtTextName.getText().toString();
        int age=Integer.parseInt(edtTextAge.getText().toString());

        String pushId=mReference.getKey();

        mReference.child(pushId).child("name").setValue(name);
        mReference.child(pushId).child("age").setValue(age);
    }




    private void getData(View view)
    {
        /*This listener is triggered everytime when database is update after the we first call to this listener through this method
        * BUT there is an disadvantage with this listener that everytime when this listener is call explicitly it will add one callback to the reference and hence we have
        * to these callback which we did in onDestroy method*/
        mReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                txtView.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

                    //BUT

        /*But this listener triggered only when we call this method or when we trigger this listener explicitly*/
        mReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                /*Getting result in terms of map if mReference contain more child*/
                Map<String,Object> map= (Map<String, Object>) snapshot.getValue();
                txtView.setText(map.get("name").toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        /*This implementation is just for example to show that how we remove callbacks of addValueEventListener */
        ValueEventListener eventListener=new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        };

        mReference.removeEventListener(eventListener);
    }

    private void viewInitialize()
    {
        btnSet =findViewById(R.id.btnRun);
        btnGet =findViewById(R.id.btnShow);
        edtTextName =findViewById(R.id.edtTextName);
        edtTextAge=findViewById(R.id.edtTextAge);
        txtView=findViewById(R.id.txtView);
    }
}