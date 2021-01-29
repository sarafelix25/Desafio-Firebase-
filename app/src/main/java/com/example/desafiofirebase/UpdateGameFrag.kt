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
import androidx.navigation.fragment.navArgs
import com.example.desafiofirebase.databinding.FragmentRegisterGameBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_register_game.*

class UpdateGameFrag : Fragment() {
    private val CODE_IMG: Int = 100
    private lateinit var storageReference: StorageReference
    private var game: Game = Game()

    lateinit var binding: FragmentRegisterGameBinding
    private val args: UpdateGameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        game = args.updateGame

        Picasso.get().load(args.updateGame.img).fit().centerCrop().into(binding.ivCapa)

        binding.etGameName.setText(args.updateGame.name)
        binding.etGameDate.setText(args.updateGame.year)
        binding.etDescription.setText(args.updateGame.description)

        btnImg.setOnClickListener {
            loadImage()
        }

        btnSave.setOnClickListener {

            if (game != null) {
                updateData(updatedGame(game))
            } else {
                Toast.makeText(context, "Ocorreu algum erro e não foi possível realizar a edição :(", Toast.LENGTH_SHORT)
                    .show()
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

    fun updatedGame(game: Game): Game {
        val name = binding.etGameName.text.toString()
        val year = binding.etGameDate.text.toString()
        val description = binding.etDescription.text.toString()

        return Game(name, description, year, game.img, game.id)
    }

    fun updateData(gameUpdate: Game) {
        val bd = FirebaseFirestore.getInstance().collection("InfoGame")
        bd.document(gameUpdate.id).set(gameUpdate)
    }
}