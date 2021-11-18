package com.glushko.winfox_test_task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.glushko.winfox_test_task.databinding.ActivityMainBinding

import androidx.core.widget.doBeforeTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var callbackFireBase: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var codeSent: Boolean = false
    private var _verificationId: String = ""

    private lateinit var model: ViewModelLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = ViewModelProvider(this).get(ViewModelLogin::class.java)
        initEditTextPhone()
        binding.btnGetCode.setOnClickListener {
            if(codeSent){
                val code = binding.editCode.text.toString()
                signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(_verificationId, code))
            }else{
                val clearPhoneNumber = clearNumber(binding.editPhone.text.toString())
                if(validNumber(clearPhoneNumber)){
                    binding.editPhoneLayout.error = null
                    requestFirebaseCode(clearPhoneNumber)
                }else{
                    binding.editPhoneLayout.error = "Номер содержит недопустимые символы"
                }
            }
        }

        callbackFireBase = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                println( "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                println("onVerificationFailed: $e")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                println("onCodeSent:$verificationId \n$token")
                _verificationId = verificationId
                codeSent = true
                binding.editCodeLayout.visibility = View.VISIBLE
                binding.btnGetCode.text = "Отправить код"
                model.startTimer(30000L)
            }
        }

        model.liveDataFirebaseTimeOut.observe(this, Observer {
            println(it)
            if (!it.first){
                binding.editCodeLayout.helperText = "Осталось ${it.second}"
            }else{
                binding.editCodeLayout.visibility = View.INVISIBLE
                binding.btnGetCode.text = "Получить код повторно"
                codeSent = false
            }
        })

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("signInWithCredential:success")
                    binding.editCodeLayout.error = null
                    val user = task.result?.user
                    println("Sing user = $user")
                    model.stopTimer()
                    Toast.makeText(this, "SUCCESS!!", Toast.LENGTH_LONG).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    println( "signInWithCredential:failure" + task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        println("bad code")
                    }
                    binding.editCodeLayout.error = "Не правильный код"
                }
            }
    }

    private fun requestFirebaseCode(clearPhoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(clearPhoneNumber)       // Phone number to verify
            .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbackFireBase)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


    }

    private fun clearNumber(number: String): String {
        var newPhone = number
        val chDeleting = charArrayOf('(', ')', '-')
        for(ch in chDeleting) {
            newPhone = newPhone.replace("$ch", "")
        }
        return newPhone
    }


    private fun validNumber(number: String): Boolean {
        return if(number.isNotEmpty()){
            val regex = Regex("""^((\+7|7|8)+([0-9]){10})$""")
            regex.find(number)!=null
        }else{
            false
        }
    }

    private fun initEditTextPhone(){
        binding.editPhone.setText("+7(")
        binding.editPhone.setSelection(binding.editPhone.length())
        var isback = true
        binding.editPhone.doBeforeTextChanged { text, start, count, after ->
            isback = count > after
        }
        binding.editPhone.doAfterTextChanged {
            if(!isback){
                when(it?.length){
                    6 -> {
                        val test = "$it)"
                        binding.editPhone.setText(test)
                        binding.editPhone.setSelection(binding.editPhone.length())
                        isback = false
                    }
                    7 -> {
                        if(!it.contains(")")){
                            binding.editPhone.setText("${it.subSequence(0, it.length - 1)})${it.subSequence(it.length - 1, it.length)}")
                            binding.editPhone.setSelection(binding.editPhone.length())
                            isback = false

                        }
                    }
                    10 -> {
                        binding.editPhone.setText("$it-")
                        binding.editPhone.setSelection(binding.editPhone.length())
                        isback = false

                    }
                    11 -> {
                        if(!it.contains("-")){
                            binding.editPhone.setText("${it.subSequence(0, it.length - 1)}-${it.subSequence(it.length - 1, it.length)}")
                            binding.editPhone.setSelection(binding.editPhone.length())
                            isback = false
                        }
                    }
                    13 -> {
                        binding.editPhone.setText("$it-")
                        binding.editPhone.setSelection(binding.editPhone.length())
                        isback = false
                    }
                    14 -> {
                        if(!it.subSequence(12, it.length).contains("-")){
                            binding.editPhone.setText("${it.subSequence(0, it.length - 1)}-${it.subSequence(it.length - 1, it.length)}")
                            binding.editPhone.setSelection(binding.editPhone.length())
                            isback = false
                        }
                    }
                }
            }else{
                if(it?.length == 2){
                    binding.editPhone.setText("$it(")
                    binding.editPhone.setSelection(binding.editPhone.length())
                    isback = false
                }
            }
        }
    }
}