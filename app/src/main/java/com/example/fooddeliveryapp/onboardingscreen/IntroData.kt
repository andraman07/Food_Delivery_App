package com.example.fooddeliveryapp.onboardingscreen

import com.example.fooddeliveryapp.R

class IntroData{

    fun dataList():ArrayList<Content>{

        val dataList=ArrayList<Content>()

        dataList.add(Content(R.drawable.img1,"Fresh Food","Straight from our kitchen to your doorstep, enjoy the taste of fresh food."))
        dataList.add(Content(R.drawable.img2,"Fast Delivery","No more waiting for your food – we'll have it at your doorstep in a flash."))
        dataList.add(Content(R.drawable.img3,"Easy Payment","Pay for your meals the way you want – our easy payment options offer flexibility and convenience."))

        return dataList

    }

}