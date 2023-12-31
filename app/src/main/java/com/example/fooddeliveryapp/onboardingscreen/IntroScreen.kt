package com.example.fooddeliveryapp.onboardingscreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.ViewPager
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.loginpage.LoginActivity
import com.example.fooddeliveryapp.models.IntroContent
import com.google.android.material.tabs.TabLayout


class IntroScreen : AppCompatActivity() {

    private lateinit var viewPager:ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var skipBtn:Button
    private lateinit var nextBtn:Button
    private lateinit var getStartedBtn:AppCompatButton
    private lateinit var btnAnimation:Animation          // delay in animation of button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)

        val contentList = arrayListOf(
            IntroContent(R.drawable.img1, R.string.first_slide_title, R.string.first_slide_desc),
            IntroContent(R.drawable.img2, R.string.second_slide_title, R.string.second_slide_desc),
            IntroContent(R.drawable.img3, R.string.third_slide_title, R.string.third_slide_desc)
        )


        viewPager=findViewById(R.id.view_pager)
        tabLayout=findViewById(R.id.intro_indicator)
        skipBtn=findViewById(R.id.skip_btn)
        nextBtn=findViewById(R.id.next_btn)
        getStartedBtn=findViewById(R.id.get_started_btn)
        btnAnimation=AnimationUtils.loadAnimation(this,R.anim.btn_anim)


        viewPager.adapter=IntroAdapter(this,contentList)
        tabLayout.setupWithViewPager(viewPager,true)


        nextBtn.setOnClickListener {
            viewPager.currentItem=viewPager.currentItem+1
        }

        skipBtn.setOnClickListener {
            viewPager.currentItem=viewPager.currentItem + (contentList.size-1)
        }

        getStartedBtn.setOnClickListener {
            savePrefsData()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }


        tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

                if(tab?.position == contentList.size-1) {
                    nextBtn.visibility=View.GONE
                    skipBtn.visibility=View.GONE
                    getStartedBtn.visibility = View.VISIBLE
                    tabLayout.visibility= View.GONE
                    getStartedBtn.startAnimation(btnAnimation)

                }else{
                    getStartedBtn.visibility = View.GONE
                    tabLayout.visibility= View.VISIBLE
                    nextBtn.visibility=View.VISIBLE
                    skipBtn.visibility=View.VISIBLE

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

    }

    @Deprecated("Deprecated in Java", ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()

    }




}