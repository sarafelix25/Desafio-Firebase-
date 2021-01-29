package com.example.desafiofirebase

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_register_game.*

class RegisterGameFrag : Fragment() {
    private val CODE_IMG: Int = 100
    private lateinit var storageReference: StorageReference
    private var game: Game = Game()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnImg.setOnClickListener {
            loadImage()
        }

        btnSave.setOnClickListener {
            game.name = etGameName.text.toString()
            game.year = etGameDate.text.toString()
            game.description = etDescription.text.toString()

            if (game.img == "") {
                Toast.makeText(context, "Você esqueceu de carregar uma imagem", Toast.LENGTH_SHORT)
                    .show()
            } else {
                saveData()
            }
        }

        tbRegisterGame.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_IMG) {

            val uploadFile = storageReference.putFile(data!!.data!!)
            uploadFile.continueWithTask { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Imagem carregada :)", Toast.LENGTH_SHORT)
                        .show()
                }
                storageReference!!.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val url = downloadUri!!.toString()
                        .substring(0, downloadUri.toString().indexOf("&token"))

                    game.img = url

                    Picasso.get().load(url).into(ivCapa)
                }
            }.addOnFailureListener { task ->
                Log.i(ContentValues.TAG, "onActivityResult: $task")
            }
        }
    }

    fun loadImage() {
        storageReference = FirebaseStorage.getInstance().getReference(getUniqueKey())
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Get Image"), CODE_IMG)
    }

    private fun getUniqueKey() =
        FirebaseFirestore.getInstance().collection("pegando chave").document().id

    fun saveData() {
        val bd = FirebaseFirestore.getInstance().collection("InfoGame")
        val id = getUniqueKey()
        game.id = id
        bd.document(id).set(game)
    }

}