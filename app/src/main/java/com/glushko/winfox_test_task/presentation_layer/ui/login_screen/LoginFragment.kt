package com.glushko.winfox_test_task.presentation_layer.ui.login_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.glushko.winfox_test_task.R
import com.glushko.winfox_test_task.databinding.FragmentLoginBinding
import com.glushko.winfox_test_task.presentation_layer.vm.ViewModelLogin
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class LoginFragment : Fragment() {

    companion object {

        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private lateinit var binding: FragmentLoginBinding
    private lateinit var callbackFireBase: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var codeSent: Boolean = false
    private var _verificationId: String = ""

    private lateinit var model: ViewModelLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        model = ViewModelProvider(requireActivity()).get(ViewModelLogin::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setActionBar(binding.inclToolbar.toolbar)
        initEditTextPhone()
        binding.btnGetCode.setOnClickListener {
            if(codeSent){
                val code = binding.editCode.text.toString()
                if(code.isEmpty()){
                    binding.editCodeLayout.error = getString(R.string.empty_sent_code)
                }else{
                    if(_verificationId.isEmpty()){
                        binding.editCodeLayout.error = getString(R.string.fast_send_code)
                    }else{
                        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(_verificationId, code))
                    }
                }
            }else{
                val clearPhoneNumber = clearNumber(binding.editPhone.text.toString())
                if(validNumber(clearPhoneNumber)){
                    binding.editPhoneLayout.error = null
                    requestFirebaseCode(clearPhoneNumber)
                }else{
                    binding.editPhoneLayout.error = getString(R.string.bad_valid_number)
                }
            }
        }

        callbackFireBase = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                println( "onVerificationCompleted:$credential")
                binding.inclToolbar.toolbarProgressBar.visibility = View.INVISIBLE
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                println("onVerificationFailed: $e")
                binding.inclToolbar.toolbarProgressBar.visibility = View.INVISIBLE
                val text = if(e is FirebaseNetworkException){
                    getString(R.string.network_err)
                }else{
                    getString(R.string.firebase_err)
                }
                binding.editPhoneLayout.error = text
                binding.btnGetCode.text = getString(R.string.get_code_again)
                codeSent = false
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                println("onCodeSent:id = $verificationId \n token = $token")
                binding.inclToolbar.toolbarProgressBar.visibility = View.INVISIBLE
                _verificationId = verificationId
                codeSent = true
                binding.editCodeLayout.visibility = View.VISIBLE
                binding.btnGetCode.text = getString(R.string.send_code)
                model.startTimer(ViewModelLogin.TIMEOUT)
            }
        }

        model.liveDataFirebaseTimeOut.observe(viewLifecycleOwner, Observer {
            if (!it.first){
                binding.editCodeLayout.helperText = "${getString(R.string.timer_remained)} ${it.second}"
            }else{
                binding.editCodeLayout.visibility = View.INVISIBLE
                binding.btnGetCode.text = getString(R.string.get_code_again)
                codeSent = false
                binding.btnGetCode.isEnabled = true
            }
        })
        model.liveDataCheckUser.observe(viewLifecycleOwner, Observer {
            binding.inclToolbar.toolbarProgressBar.visibility = View.INVISIBLE
            binding.btnGetCode.isEnabled = true
            if(it.isSuccess){
                binding.editCodeLayout.error = null
                model.stopTimer()
                Toast.makeText(requireContext(), "SUCCESS!!", Toast.LENGTH_LONG).show()
            }else{
                binding.editCodeLayout.error = getString(R.string.err_check)
            }
        })
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("signInWithCredential:success")
                    binding.btnGetCode.isEnabled = false
                    binding.inclToolbar.toolbarProgressBar.visibility = View.VISIBLE
                    model.sendToServer(_verificationId, task.result?.user?.phoneNumber?:"")
                } else {
                    println( "signInWithCredential:failure" + task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        println("bad code")
                    }
                    binding.editCodeLayout.error = getString(R.string.bad_code)
                }
            }
    }

    private fun requestFirebaseCode(clearPhoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(clearPhoneNumber)       // Phone number to verify
            .setTimeout(ViewModelLogin.TIMEOUT, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbackFireBase)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        binding.inclToolbar.toolbarProgressBar.visibility = View.VISIBLE
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