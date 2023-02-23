package com.example.firebase.model;

public class User
{
    private String name;
    private int age;

    /*Here we use a default constructor because while getting data from firebase we use generic class in getValue() method and for this
    our class must have a default or no argument constructor*/
    public User()
    {

    }

    public User(String name, int age)
    {
        this.name = name;
        this.age = age;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }
}
