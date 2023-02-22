package com.example.firebase

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.example.firebase.model.Person
import com.google.firebase.database.*

class MainActivity2 : AppCompatActivity()
{
    /*Here we use lateinit keyword because in kotlin whenever we create variables or fields globally we have to initialize them
    * at the time of declaration and hence lateinit keyword make sure and tells the compiler that these variables or properties
    * will be initialise after some time at any place or in other words it guaranteed that these properties will initialise and
    * hence if we don't use this keyword then we have to initialise these fields right here*/
    private lateinit var edtTxtName:AppCompatEditText
    private lateinit var edtTxtAge:AppCompatEditText
    private lateinit var txtView:AppCompatTextView
    private lateinit var btnGet:AppCompatButton
    private lateinit var btnSet:AppCompatButton

    private lateinit var mDatabase:FirebaseDatabase
    private lateinit var mRefRoot:DatabaseReference
    private lateinit var mRefChild:DatabaseReference
    private lateinit var mRefChildOfchild:DatabaseReference

    private lateinit var mChildEventListener:ChildEventListener



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        viewInitialize()

        mDatabase=FirebaseDatabase.getInstance()        /*get instance or object of whole firebase database which we use*/
        mRefRoot=mDatabase.reference                    /*get reference of root node*/
        mRefChild=mDatabase.getReference("user")   /*get reference of child node if exists, otherwise create new child node with name "user" */
        mRefChildOfchild=mDatabase.getReference("user/user1")   /*get reference of sub child node if exists, otherwise create new sub child node of child "user" with name "user1" */

        btnSet.setOnClickListener(this::setDataProfApproach)
        btnGet.setOnClickListener(this::getData)


        mChildEventListener=object:ChildEventListener{
            override fun onChildAdded(snapshot:DataSnapshot, previousChildName:String?)
            {
                val map=snapshot.value as Map<String,Any>
                txtView.append("${map["name"]} ${map["age"]}\n")
            }

            override fun onChildChanged(snapshot:DataSnapshot, previousChildName:String?)
            {
                Toast.makeText(this@MainActivity2,"Child updated",Toast.LENGTH_SHORT).show()
            }

            override fun onChildRemoved(snapshot:DataSnapshot)
            {
                Toast.makeText(this@MainActivity2,"Child removed",Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(snapshot:DataSnapshot, previousChildName:String?)
            {

            }

            override fun onCancelled(error:DatabaseError)
            {

            }
        }


    }


    /*Removing childEventListener*/
    override fun onDestroy()
    {
        super.onDestroy()
        mRefRoot.removeEventListener(mChildEventListener)
    }



    private fun setData(view: View)
    {
        val name=edtTxtName.text.toString()
        val age=edtTxtAge.text.toString()

        /*mRefRoot.setValue(input)*/
        /*mRefChild.setValue(input)*/
        /*mRefChildOfchild.setValue(input)*/

        //We can also create a sub child node by using the reference of parent child
        /*And also set the listeners of success and Failure of data insertion in database and to do this we have
        to change the rules of our firebase database on firebase site*/

        mRefChild.child("user2").child("name").setValue(name)
            .addOnSuccessListener{Toast.makeText(this,"Successfully Inserted",Toast.LENGTH_SHORT).show()}
            .addOnFailureListener{Toast.makeText(this,"Permission denied to write data",Toast.LENGTH_SHORT).show()}

        mRefChild.child("user2").child("age").setValue(age)
    }



    private fun setDataProfApproach(view:View)
    {
        val name=edtTxtName.text.toString()
        val age=edtTxtAge.text.toString().toInt()

        val map=HashMap<String,Any>()
        map["name"]=name
        map["age"]=age

        val person=Person(name,age)

        /*PushId is used to distinguish different users which registered at the same time and these id's are very useful*/
        val pushId=mRefRoot.push().key

        /*Instead of calling different setValue() methods we use map and pass this map to one single setValue() method.But it is noted that
        * if the keys we provided in map are exist then it will set value to these keys otherwise it will create new keys which are nothing
        * but child nodes and set values to these child nodes*/
        //mRefRoot.child(pushId.toString()).setValue(map)

        mRefRoot.child(pushId.toString()).setValue(person)

    }


    /*The main difference between updateChildren() and setValue() is that whenever we we call setValue(),it call an Api and
    * hence if a method contains different call to this setValue() method then it will call same number of Api calls but when we
    * use updateChildren() it calls only one Api does not matter how much call of this method is contained within a method*/
    private fun updateData(view:View)
    {
        val name=edtTxtName.text.toString()
        val age=edtTxtAge.text.toString().toInt()

        val map=HashMap<String,Any>()

        /*Adding value to this map*/
        map["/user4/name"]=name
        map["/user4/age"]=age

        mRefRoot.updateChildren(map)
    }


    private fun deleteData(view:View)
    {
        //By using setValue() method
        /*mRefRoot.child("/user/user2/name").setValue(null)
        mRefRoot.child("user").child("user2").child("age").setValue(null)*/

        //By using removeChild() method
        mRefRoot.child("user1").removeValue()
    }


    //Here we call different methods to get values from database
    private fun getData(view: View)
    {
        getDataUsingChildEventListener()
    }



    private fun getDataForRoot()
    {
        /*As we use new keyword in java for the anonymous implementation of Interface,In kotlin we use 'object' keyword for the
        * anonymous implementation of Interface.Here ':' means implementing or sometimes used for extending classes or interface*/
        mRefRoot.addValueEventListener(object:ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                /*txtView.text=snapshot.getValue(String::class.java)*/

                //or
                /*Here we get reference of child node by using reference(snapshot) of root node and fetching each child one by one in forEach loop*/
                for(snaps in snapshot.children)
                {
                    val map=snaps.value as Map<String,Any>
                    txtView.append("${map["name"]} ${map["age"]}\n")
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }

        })
    }



    private fun getDataForChild()
    {
        /*This listener will not automatically triggered everytime when database is update and it will triggered only when we call this */
        mRefChild.addListenerForSingleValueEvent(object:ValueEventListener
        {
            override fun onDataChange(snapshot:DataSnapshot)
            {
                txtView.text=snapshot.value.toString()
            }

            override fun onCancelled(error:DatabaseError)
            {

            }

        })
    }



    private fun getDataForChildOfChild()
    {
        /*This listener will automatically triggered everytime when database is update after we call it once and give updates everytime */
        mRefChild.addValueEventListener(object:ValueEventListener
        {
            override fun onDataChange(snapshot:DataSnapshot)
            {
                txtView.text=snapshot.value.toString()
            }

            override fun onCancelled(error:DatabaseError)
            {

            }

        })
    }



    private fun getDataOfSubChildByUsingReferenceOfParentChild()
    {
        /*Here we set the listener on the sub child node by using the reference of parent child node Because if we didn't use
        * mRefChild.child("user2").listener and simply use mRefChild.listener then we get the updates about the parent child
        * the values which is return by snapshot is type of Map because we know that in database nodes are stored in key and
        * value pair and also we don't know that how much sub child nodes are present in this parent child node and hence here
        * the output will be the Map of all sub child nodes that's why we use this .child("subChildPath") with parent child
        * reference so that we can get value of that sub child node*/
        mRefChild.child("user2").addListenerForSingleValueEvent(object:ValueEventListener
        {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot:DataSnapshot)
            {
                /*txtView.text=snapshot.value.toString()*/

                //Or if user2 contain further sub child node than output will be a Map type
                val map=snapshot.value as Map<String,Any>
                txtView.text="${map["name"]}\n${map["age"]}"

            }

            override fun onCancelled(error:DatabaseError)
            {
                Toast.makeText(this@MainActivity2,"Permission denied to read data",Toast.LENGTH_SHORT).show()
            }

        })
    }




    private fun getDataUsingChildEventListener()
    {
        /*We implement this listener inside onCreate method which is professional approach because in this approach these ChildEventListener and
        ValueEventListener registered can handled on onDestroy method*/
        mRefRoot.addChildEventListener(mChildEventListener)
    }

    private fun viewInitialize()
    {
        edtTxtName=findViewById(R.id.edtTextNameK)
        edtTxtAge=findViewById(R.id.edtTextAgeK)
        txtView=findViewById(R.id.txtViewK)
        btnGet=findViewById(R.id.btnGetK)
        btnSet=findViewById(R.id.btnSetK)

    }
}
