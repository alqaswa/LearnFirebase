package com.example.firebase;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.firebase.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
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

    ChildEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewInitialize();
        mDatabase=FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference();

        btnSet.setOnClickListener(this::setDataProfApproach);
        btnGet.setOnClickListener(this::getData);

        eventListener=new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                User user=snapshot.getValue(User.class);
                txtView.append(user.getName()+"\n"+user.getAge());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        };

        mReference.addChildEventListener(eventListener);




    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

       mReference.removeEventListener(eventListener);
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

    private void setDataProfApproach(View view)
    {
        String name=edtTextName.getText().toString();
        int age=Integer.parseInt(edtTextAge.getText().toString());

        String pushId=mReference.push().getKey();

        User user=new User(name,age);
        mReference.child(pushId).setValue(user);
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

    private void viewInitialize()
    {
        btnSet =findViewById(R.id.btnRun);
        btnGet =findViewById(R.id.btnShow);
        edtTextName =findViewById(R.id.edtTextName);
        edtTextAge=findViewById(R.id.edtTextAge);
        txtView=findViewById(R.id.txtView);
    }
}