 package com.example.appnavigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appnavigation.loginActivity.Companion.useremail
import com.google.firebase.auth.FirebaseAuth

 class MainActivity : AppCompatActivity() {

     private lateinit var  drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toast.makeText(this,"Hola ${useremail}", Toast.LENGTH_SHORT).show()

        initToolbar()
    }

     private fun initToolbar() {
         val toolBar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
         setSupportActionBar(toolBar)

         drawer = findViewById(R.id.drawer_layout)

         val toogle = ActionBarDrawerToggle(this,drawer,findViewById(R.id.toolbar_main),R.string.bar_title)

         drawer.addDrawerListener(toogle)
         toogle.syncState()
     }

     fun callSignOut(view: View) {
         signOut()
     }

     private fun signOut() {
         useremail = ""
         FirebaseAuth.getInstance().signOut()
         val intent = Intent(this,loginActivity::class.java)
         startActivity(intent)
     }
 }