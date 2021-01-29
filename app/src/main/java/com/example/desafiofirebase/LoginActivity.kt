package com.example.desafiofirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tv_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_login.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)

            login()

            startActivity(intent)
            finish()
        }
    }

    private fun login() {
        val user = getUser()
        if (user != null) {
            firebaseLogin(user)
        } else {
            sendMsg("Por favor, preencha os campos")
        }
    }

    private fun sendMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
            .show()
    }

    private fun firebaseLogin(usuario: com.example.desafiofirebase.User?) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(usuario.email, usuario.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val firebaseUser = it.result?.user!!
                    val userFirebase = User(firebaseUser.email.toString(), "", firebaseUser.uid)
                    sendMsg("Login realizado :)")
                }
            }
    }

    private fun getUser(): com.example.desafiofirebase.User? {
        var email = etEmailLogin.text.toString()
        var password = etPasswordLogin.text.toString()
        return if (!email.isNullOrBlank() and !password.isNullOrBlank())
            User("", email, password)
        else
            null
    }
}