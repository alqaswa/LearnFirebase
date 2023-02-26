package com.example.firebase.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.DetailActivityKt
import com.example.firebase.R
import com.example.firebase.methods.DeleteData
import com.example.firebase.model.Person

class MyAdapter(private val personList:List<Person>,private val mContext:Context):RecyclerView.Adapter<MyAdapter.MyViewHolder>()
{

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):MyViewHolder
    {
        val view=LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount():Int
    {
        return personList.size
    }

    override fun onBindViewHolder(holder:MyViewHolder, position:Int)
    {
        val person=personList[position]
        holder.txtName.text=person.name
        holder.txtAge.text=person.age.toString()

        holder.itemView.setOnClickListener {
            val intent=Intent(mContext, DetailActivityKt::class.java)

            val bundle=Bundle()
            bundle.putParcelable("person", person)

            /*Here on clicking of every item we send person object using bundle through intent*/
            intent.putExtra("bundle", bundle)
            mContext.startActivity(intent)
        }

        val childId=person.childId
        holder.itemView.setOnLongClickListener {
            val task=DeleteData.deleteData(childId!!)
            task.addOnSuccessListener {
                Toast.makeText(mContext,"Successfully removed ",Toast.LENGTH_SHORT).show()}
            true}
    }

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        var txtName:AppCompatTextView=itemView.findViewById(R.id.itmNameTxt)
        var txtAge:AppCompatTextView=itemView.findViewById(R.id.itmAgeTxt)
    }
}