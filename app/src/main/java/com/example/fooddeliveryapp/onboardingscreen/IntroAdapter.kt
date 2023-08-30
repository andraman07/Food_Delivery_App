package com.example.fooddeliveryapp.onboardingscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.fooddeliveryapp.R

class IntroAdapter(private val context: Context,private val introList:ArrayList<Content>) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any{
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.intro_layout, container, false)

        val introImage:ImageView=view.findViewById(R.id.intro_image)
        val introTitle:TextView=view.findViewById(R.id.intro_title)
        val introDescription:TextView=view.findViewById(R.id.intro_description)

        introImage.setImageResource(introList[position].imageId)
        introTitle.text=introList[position].title
        introDescription.text=introList[position].description

        container.addView(view)
        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


    override fun getCount(): Int = introList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }


}