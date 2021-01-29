package com.example.desafiofirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)

            registerUser()

            startActivity(intent)
            finish()
        }

        tbRegister.setNavigationOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
            finish()
        }
    }

    private fun registerUser() {
        val user = getUser()
        if (user != null) {
            firebaseReg(user)
        } else {
            sendMsg("Por favor, preencha os campos")
        }

    }

    private fun firebaseReg(user: User) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val firebaseUser = it.result?.user!!
                    val userFirebase = User(firebaseUser.email.toString(), "", firebaseUser.uid)
                    sendMsg("Cadastro realizado :)")
                }

            }.addOnFailureListener {
                sendMsg("Não foi possível fazer o seu cadastro :(")
            }
    }

    private fun sendMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
            .show()
    }

    private fun getUser(): User? {
        var name = etName.text.toString()
        var email = etEmail.text.toString()
        var password = etPassword.text.toString()
        return if (!email.isNullOrEmpty() and !password.isNullOrEmpty())
            User(name, email, password)
        else
            null
    }
}