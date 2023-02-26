package com.example.firebase

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.adapter.MyAdapter
import com.example.firebase.model.Person
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.*

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
    private lateinit var imgView:AppCompatImageView

    private lateinit var progressTxt:AppCompatTextView
    private lateinit var progressBar:ProgressBar

    private lateinit var mDatabase:FirebaseDatabase
    private lateinit var mRefRoot:DatabaseReference
    private lateinit var mRefChild:DatabaseReference
    private lateinit var mRefChildOfchild:DatabaseReference

    private lateinit var mStorageReference:StorageReference

    private lateinit var mChildEventListener:ChildEventListener

    private lateinit var mRecyclerView:RecyclerView
    private lateinit var myAdapter:MyAdapter
    private lateinit var mPersonList:ArrayList<Person>

    private var mPermission:Boolean=false
    private val STORAGE_PERMISSION_CODE=10
    private lateinit var registerForResult:ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        viewInitialize()

        mPersonList=ArrayList()
        mRecyclerView.layoutManager=LinearLayoutManager(this)
        myAdapter=MyAdapter(mPersonList,this)
        mRecyclerView.adapter=myAdapter

        //Realtime database
        mDatabase=FirebaseDatabase.getInstance()        /*get instance or object of whole firebase database which we use*/
        mRefRoot=mDatabase.reference                    /*get reference of root node*/
        mRefChild=mDatabase.getReference("user")   /*get reference of child node if exists, otherwise create new child node with name "user" */
        mRefChildOfchild=mDatabase.getReference("user/user1")   /*get reference of sub child node if exists, otherwise create new sub child node of child "user" with name "user1" */


        //Firebase Storage
        mStorageReference=Firebase.storage.getReference("docs")
        /*exactly same as mStorageReference=FirebaseStorage.getInstance().getReference("docs") But for java storage library dependency*/


        btnSet.setOnClickListener(this::uploadFromExternalStorage)
        btnGet.setOnClickListener(this::downloadFromFirebase)


        mChildEventListener=object:ChildEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot:DataSnapshot, previousChildName:String?)
            {
                val person=snapshot.getValue(Person::class.java)
                person?.childId=snapshot.key

                mPersonList.add(person!!)
                myAdapter.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildChanged(snapshot:DataSnapshot, previousChildName:String?)
            {

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildRemoved(snapshot:DataSnapshot)
            {
                val person=snapshot.getValue(Person::class.java)
                person?.childId=snapshot.key

                mPersonList.remove(person)
                myAdapter.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildMoved(snapshot:DataSnapshot, previousChildName:String?)
            {
                myAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error:DatabaseError)
            {

            }
        }

        registerForResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback<ActivityResult>
                {
                    result->

                        val intent=result.data
                        val imageUri=intent?.data
                        val task=mStorageReference.child("image/"+imageUri!!.lastPathSegment).putFile(imageUri)

                        progressBar.visibility=View.VISIBLE

                        task.addOnProgressListener {
                            takeSnapshot->
                            val progress=(100*takeSnapshot.bytesTransferred)/takeSnapshot.totalByteCount
                            progressBar.setProgress(progress.toInt(),true)
                            progressTxt.text="$progress%"
                        }

                        task.addOnSuccessListener {Toast.makeText(this,"file uploaded successfully...",Toast.LENGTH_SHORT).show()}


                })

        if(!mPermission)
        {
            getPermissions()
        }

    }

    //Uploading files to firebase from internal storage
    private fun storage(view:View)
    {
        /*Uploading files Using putStream() method*/
        //From assets
        val inputStream=assets.open("multan.jpg")

        val task=mStorageReference.child("image/multan.jpg").putStream(inputStream)
        task.addOnSuccessListener {Toast.makeText(this,"Successfully uploaded1",Toast.LENGTH_SHORT).show()}

        //From device memory
        val inputStream2=FileInputStream(File(filesDir,"karachi.jpg"))

        val task2=mStorageReference.child("image/karachi.jpg").putStream(inputStream2)
        task2.addOnSuccessListener {Toast.makeText(this,"Successfully uploaded2",Toast.LENGTH_SHORT).show()}


        /*Uploading files Using putByte() method
        * in this approach we convert bitmap image or a file into byte array because ByteArrayOutputStream() class creates an Output Stream for
        * writing data into byte array*/
        val bitmap=readImage()
        val boas=ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,boas)
        val task3=mStorageReference.child("image/lahore.jpeg").putBytes(boas.toByteArray())
        task3.addOnSuccessListener {Toast.makeText(this,"Successfully uploaded2",Toast.LENGTH_SHORT).show()}
    }


    //Uploading files to firebase from external storage (permission needed)
    private fun uploadFromExternalStorage(view:View)
    {
            val intent=Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            registerForResult.launch(intent)
    }


    //Downloading files from firebase database or cloud storage using getBytes(),getFile()
    private fun downloadFromFirebase(view:View)
    {
        //getByte()
        /*In this method we have to provide the max limit of bytes to download And here first our file will download and then we save this file to
        * our desired location explicitly */
        val filePath1=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"image3.jpg")
        val maxByteForDownl=4*1024*1024
        val task=mStorageReference.child("images/img4.jpg").getBytes(maxByteForDownl.toLong())

        task.addOnSuccessListener {
            bytes->imgView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.size))
            val fos=FileOutputStream(filePath1)
            fos.write(bytes)
            fos.close()
            Toast.makeText(this,"successfully download",Toast.LENGTH_SHORT).show()
        }

        //getFile()
        /*In this method we pass that directory to the method where we want to save our download and our file will be download directly to this pasth*/
        val filPath2=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"image.jpg")
        val task2=mStorageReference.child("images/img9.jpg").getFile(filPath2)

        task2.addOnSuccessListener {
            /*Setting image from the file directory which has path in terms of Uri*/
            taskSnapshot->imgView.setImageURI(Uri.fromFile(filPath2))
            Toast.makeText(this,"successfully download 2",Toast.LENGTH_SHORT).show()
        }

        //getDownloadUri()
        /*In this method we get uri of that child or file from the data which we want to download and then use this uri to download files*/
        val task3=mStorageReference.child("images/img4.jpg").downloadUrl
        task3.addOnSuccessListener {
            uri->downloadManagers(uri)
        }


    }

    private fun downloadManagers(uri:Uri)
    {
        val filePath=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"photo.jpg")

        val downloadManager=getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request=DownloadManager.Request(uri)
            .setTitle("File Download")
            .setDescription("your file is downloading...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"arshad.jpg")
                //or
            /*.setDestinationInExternalFilesDir(this,"photos","photo1")

            .setDestinationUri(Uri.fromFile(filePath))*/

        /*This is add our download request in queue and whenever our download manager free it will start our downloading*/
        downloadManager.enqueue(request)
        Toast.makeText(this,"successfully download 3",Toast.LENGTH_SHORT).show()
    }

    private fun getPermissions()
    {
        mPermission=true

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),STORAGE_PERMISSION_CODE)
        }
    }

    /*This method will automatically triggered or call when requestPermissions() is called*/
    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<out String>, grantResults:IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==STORAGE_PERMISSION_CODE && grantResults.isNotEmpty())
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"permission granted",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this,"please allow to read the data",Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Return image bitmap
    private fun readImage():Bitmap
    {
        val inputStream=assets.open("lahore.jpg")/*return InputStream*/
        val bitmap=BitmapFactory.decodeStream(inputStream)/*return Bitmap*/
        return bitmap
                        //OR

        /*val bitmapDrawable:BitmapDrawable=Drawable.createFromStream(inputStream,"null") as BitmapDrawable
        return bitmapDrawable.bitmap*/
    }

    //Removing childEventListener
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
        /*PushId is used to distinguish different users which registered at the same time and these id's are very useful*/
        val pushId=mRefRoot.push().key

        val name=edtTxtName.text.toString()
        val age=edtTxtAge.text.toString().toInt()


        val map=HashMap<String,Any>()
        map["name"]=name
        map["age"]=age

        val person=Person(name,age)

        /*Instead of calling different setValue() methods we use map and pass this map to one single setValue() method.But it is noted that
        * if the keys we provided in map are exist then it will set value to these keys otherwise it will create new keys which are nothing
        * but child nodes and set values to these child nodes*/
        //mRefRoot.child(pushId.toString()).setValue(map)

        mRefRoot.child(pushId.toString()).setValue(person)

    }


    private fun myQuery(view:View)
    {
        /*select * from table or simple getting all childs of this reference*/
        mRefRoot.addChildEventListener(mChildEventListener)

        /*select * from table sort by alphabetical order of name*/
        //mRefRoot.orderByChild("name").addChildEventListener(mChildEventListener)

        /*select * from table where name=*/
        //mRefRoot.orderByChild("name").equalTo("Arshad").addChildEventListener(mChildEventListener)

        /*select * from table where value is > */
        //mRefRoot.orderByChild("age").startAt(25.0).addChildEventListener(mChildEventListener)

        /*select * from table where value is < */
        //mRefRoot.orderByChild("age").endAt(25.0).addChildEventListener(mChildEventListener)
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
        txtView=findViewById(R.id.txtViewAgeK)
        btnGet=findViewById(R.id.btnGetK)
        btnSet=findViewById(R.id.btnSetK)
        imgView=findViewById(R.id.imageView)

        mRecyclerView=findViewById(R.id.recyclerView)

        progressTxt=findViewById(R.id.progressTxt)
        progressBar=findViewById(R.id.progressBar)

    }
}

