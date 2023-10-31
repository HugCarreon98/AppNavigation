package com.example.appnavigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

class loginActivity : AppCompatActivity() {

    companion object{
        lateinit var useremail: String
        lateinit var providerSession: String
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var lyTerms: LinearLayout

    private lateinit var mAuth: FirebaseAuth
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        lyTerms = findViewById(R.id.lyTerms)
        lyTerms.visibility = View.INVISIBLE

        etEmail = findViewById(R.id.Email)
        etPassword = findViewById(R.id.Password)

        mAuth = FirebaseAuth.getInstance()

        manageButtonLogin()
        etEmail.doOnTextChanged { text, start, before, count -> manageButtonLogin() }
        etPassword.doOnTextChanged { text, start, before, count -> manageButtonLogin() }
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser

        if(currentUser != null){
            goHome(currentUser.email.toString(),currentUser.providerId)
        }
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun manageButtonLogin() {
        var btnLogin = findViewById<TextView>(R.id.btnlogin)
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        if(TextUtils.isEmpty(password) || ValidateEmail.isEmail(email) == false){
            btnLogin.isEnabled = false
            btnLogin.setBackgroundColor(ContextCompat.getColor(this,R.color.gray))
        }else{
            btnLogin.isEnabled = true
            btnLogin.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
        }
    }

    fun login(view: View) {

        loginUser()
    }

    private fun loginUser() {
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if(it.isSuccessful) goHome(email,"email")
                else{
                    if(lyTerms.visibility == View.INVISIBLE) lyTerms.visibility = View.VISIBLE
                    else{
                        var cbAcept = findViewById<CheckBox>(R.id.termsAccept)
                        if(cbAcept.isChecked) register()
                    }
                }
            }

    }


    private fun goHome(email: String, provider: String) {

        useremail = email
        providerSession = provider

        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    private fun register() {
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful) {

                    var dateRegister = SimpleDateFormat("dd/MM/yy").format(java.util.Date())
                    var dbRegister = FirebaseFirestore.getInstance()
                    dbRegister.collection("users").document(email).set(hashMapOf(
                        "user" to email,
                        "dateRegister" to dateRegister
                    ))
                    goHome(email, "email")
                }else{
                    Toast.makeText(this,"Error algo ha salido mal :(", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun goTerms(view: View) {
        val intent = Intent(this,termsActivity::class.java)
        startActivity(intent)
    }

    fun forgotPassword(view: View) {
        resetPassword()
    }

    private fun resetPassword() {
        var e = etEmail.text.toString()
        if (!TextUtils.isEmpty(e)){
            mAuth.sendPasswordResetEmail(e)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this,"Email enviado", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"No se ha encontrado usuario con el correo", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this,"Indica un email", Toast.LENGTH_SHORT).show()
        }
    }

     fun callSignInGoogle(view: View){
         signInGoogle()
     }

    private fun signInGoogle() {
    }


}