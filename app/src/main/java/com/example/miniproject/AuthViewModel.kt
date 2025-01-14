package com.example.miniproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(){
    private val auth: FirebaseAuth =FirebaseAuth.getInstance()
    private val _authState= MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState


 init{
     checkAuthStatus()
 }

    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value=AuthState.Unauthenticated
        }
        else{
            _authState.value=AuthState.Authenticated
        }
    }

     fun login(email: String,password:String){
         if(email.isEmpty() || password.isEmpty()){
             _authState.value=AuthState.Error("Email or Password can't be empty.")
             return
         }
         _authState.value=AuthState.Loading
         auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
         if(task.isSuccessful){
             _authState.value=AuthState.Authenticated
         }
             else{
                 _authState.value=AuthState.Error(task.exception?.message?:"Something went wrong!")
         }
         }
              }


    fun signup(email: String,password:String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value=AuthState.Error("Email or Password can't be empty.")
            return
        }
        _authState.value=AuthState.Loading
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if(task.isSuccessful){
                _authState.value=AuthState.Authenticated
            }
            else{
                _authState.value=AuthState.Error(task.exception?.message?:"Something went wrong!")
            }
        }
    }

    fun signout(){
        auth.signOut()
        _authState.value=AuthState.Unauthenticated
    }


    fun resetPassword(email: String) {
        // Add logic to send a password reset email
        viewModelScope.launch {
            try {
                // Simulate sending a password reset email
                // Replace this with actual authentication logic (e.g., Firebase)
                if (email.isNotEmpty()) {
                    // Example: FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    println("Password reset email sent to $email") // Replace with real action
                } else {
                    throw IllegalArgumentException("Email cannot be empty")
                }
            } catch (e: Exception) {
                // Handle errors
                println("Error resetting password: ${e.message}")
            }
        }
    }

}

sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading : AuthState()
    data class Error(val message: String):AuthState()
}