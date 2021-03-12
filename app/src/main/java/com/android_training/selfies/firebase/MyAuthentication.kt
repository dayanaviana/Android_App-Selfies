package com.android_training.selfies.firebase

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android_training.selfies.MyApplication
import com.android_training.selfies.activities.GalleryActivity
import com.android_training.selfies.login.HomeActivity
import com.android_training.selfies.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*

class MyAuthentication {
    companion object{
        private var mContext = MyApplication.appContext
        private var mAuth = FirebaseAuth.getInstance()
        var user = mAuth.currentUser

        fun logout() {
            mAuth.signOut()
        }
        fun forgotPassword(email: String) {
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(mContext as Activity){task ->
                    if(task.isSuccessful){
                        MyApplication.log("Reset email sent")
                    }else{
                        var message = task.exception!!.message
                        Log.d("myApp", message!!)
                        MyApplication.toast("Failed to Send reset email")
                    }
                }
        }
        fun login(email: String, password: String, activity: Activity) {
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(activity, object: OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if(task.isSuccessful){
                            startActivity(mContext,Intent(activity, GalleryActivity::class.java),null)
//                            MyApplication.log("Login Successfully")
                        }else{
                            var message = task.exception!!.message
                            Log.d("myApp", message!!)
                            MyApplication.toast("Login Failed")
                        }
                    }
                })
        }
        private fun register(email: String, password: String) {
            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(mContext as Activity, object:OnCompleteListener<AuthResult>{
                    override fun onComplete(task: Task<AuthResult>) {
                        if(task.isSuccessful){
                            MyApplication.toast("User registered successfully")
                        }else{
                            MyApplication.toast("Registration failed")
                        }
                    }
                })
        }
        fun deleteUser() {
            user.delete()
                .addOnCompleteListener(mContext as Activity){ task ->
                    if(task.isSuccessful){
                        MyApplication.toast("User Deleted")
                    }else{
                        var message = task.exception!!.message
                        Log.d("myApp", message!!)
                        MyApplication.toast("User Delete failed")
                    }
                }

        }
        fun updatePassword(password: String) {
            user.updatePassword(password)
                .addOnCompleteListener(mContext as Activity){ task ->
                    if(task.isSuccessful){
                        MyApplication.toast("Password altered successfully")
                    }else{
                        var message = task.exception!!.message
                        Log.d("myApp", message!!)
                        MyApplication.toast("Alteration failed")
                    }
                }
        }
        fun updateEmail(email: String) {
            user.updateEmail(email)
                .addOnCompleteListener(mContext as Activity){ task ->
                    if(task.isSuccessful){
                        MyApplication.toast( "Email altered successfully")
                    }else{
                        var message = task.exception!!.message
                        Log.d("myApp", message!!)
                        MyApplication.toast("Alteration failed")
                    }
                }
        }
    }
}