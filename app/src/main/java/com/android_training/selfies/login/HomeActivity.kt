package com.android_training.selfies.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android_training.selfies.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        updateUI()

        init()
    }

    private fun updateUI() {
        user = auth.currentUser!!
        txt_email.text = user.email
        edt_email.setText("")
        edt_password.setText("")
    }

    private fun init() {
        btn_update_email.setOnClickListener(this)
        btn_update_password.setOnClickListener(this)
        btn_delete_user.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when(view){
            btn_logout -> logout()
            btn_update_email -> updateEmail()
            btn_update_password -> updatePassword()
            btn_delete_user -> deleteUser()
        }
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun deleteUser() {
        user.delete()
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "User Deleted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }else{
                    var message = task.exception!!.message
                    Log.d("myApp", message!!)
                    Toast.makeText(applicationContext, "User Delete failed", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun updatePassword() {
        var password = edt_password.text.toString()
        user.updatePassword(password)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "Password altered successfully", Toast.LENGTH_SHORT).show()
                    updateUI()
                }else{
                    var message = task.exception!!.message
                    Log.d("myApp", message!!)
                    Toast.makeText(applicationContext, "Alteration failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateEmail() {
        var email = edt_email.text.toString()
        user.updateEmail(email)
            .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(applicationContext, "Email altered successfully", Toast.LENGTH_SHORT).show()
                        updateUI()
                    }else{
                        var message = task.exception!!.message
                      Log.d("myApp", message!!)
                        Toast.makeText(applicationContext, "Alteration failed", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}