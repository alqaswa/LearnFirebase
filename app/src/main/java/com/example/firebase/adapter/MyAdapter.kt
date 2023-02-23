package com.example.firebase.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.DetailActivityKt
import com.example.firebase.R
import com.example.firebase.model.Person

class MyAdapter(private val personList:List<Person>):RecyclerView.Adapter<MyAdapter.MyViewHolder>()
{

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):MyViewHolder
    {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
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

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent=Intent(it.context,DetailActivityKt::class.java)

            val bundle=Bundle()
            bundle.putParcelable("person",person)

            intent.putExtra("bundle",bundle)
            it.context.startActivity(intent)
        })
    }

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        var txtName:AppCompatTextView=itemView.findViewById(R.id.itmNameTxt)
        var txtAge:AppCompatTextView=itemView.findViewById(R.id.itmAgeTxt)
    }
}