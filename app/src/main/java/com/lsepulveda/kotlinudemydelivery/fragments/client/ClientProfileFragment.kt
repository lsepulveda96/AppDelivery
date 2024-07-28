package com.lsepulveda.kotlinudemydelivery.fragments.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.MainActivity
import com.lsepulveda.kotlinudemydelivery.activities.SelectRolesActivity
import com.lsepulveda.kotlinudemydelivery.activities.client.update.ClientUpdateActivity
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import de.hdodenhof.circleimageview.CircleImageView

class ClientProfileFragment : Fragment() {
    var myView: View? = null
    var buttonSelectRol : Button? = null
    var buttonUpdateProfile : Button? = null
    var circleImageUser: CircleImageView? = null
    var textViewName: TextView? = null
    var textViewEmail: TextView? = null
    var textViewPhone: TextView? = null
    var imageViewLoguut: ImageView? = null

    var sharedPref: SharedPref? = null
    var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // guarda el layout, el fragment en miView
        myView = inflater.inflate(R.layout.fragment_client_profile, container, false)

        sharedPref = SharedPref(requireActivity())

        buttonSelectRol = myView?.findViewById(R.id.btn_select_rol)
        buttonUpdateProfile = myView?.findViewById(R.id.btn_update_profile)
        textViewName = myView?.findViewById(R.id.textview_name)
        textViewEmail = myView?.findViewById(R.id.textview_email)
        textViewPhone = myView?.findViewById(R.id.textview_phone)
        circleImageUser = myView?.findViewById(R.id.circle_image_user)
        imageViewLoguut = myView?.findViewById(R.id.imageview_logout)

        buttonSelectRol?.setOnClickListener{ goToSelectRol() }
        imageViewLoguut?.setOnClickListener{ logout() }
        buttonUpdateProfile?.setOnClickListener{ goToUpdate() }

        // obtiene los datos del usuario
        getUserFromSession()

        textViewName?.text = "${user?.name} ${user?.lastname}"
        textViewEmail?.text = "${user?.email}"
        textViewPhone?.text = "${user?.phone}"

        if(!user?.image.isNullOrBlank()){
            // into para decirle dentro de donde quiero mostrar la imagen
            Glide.with(requireContext()).load(user?.image).into(circleImageUser!!)
        }

        return myView
    }

    private fun logout(){
        sharedPref?.remove("user")
        val i = Intent(requireContext(), MainActivity::class.java)
        startActivity(i)
    }
    private fun getUserFromSession(){

        val gson = Gson()

        // si el usuario existe en sesion
        if(!sharedPref?.getData("user").isNullOrBlank()){
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun goToUpdate(){
        val i = Intent(requireContext(), ClientUpdateActivity::class.java)
        startActivity(i)
    }

    private fun goToSelectRol(){
        // require context porque estamos dentro de un fragment
        val i = Intent(requireContext(), SelectRolesActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // eliminar historial de pantallas
        startActivity(i)
    }
}