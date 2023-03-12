package com.e444er.shop.viewmodel

import androidx.lifecycle.ViewModel
import com.e444er.shop.data.User
import com.e444er.shop.util.*
import com.e444er.shop.util.Constants.Companion.USERS_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Loading())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    fun registerNewUser(
        user: User,
        password: String
    ) {
        if (checkValidation(user, password)) {
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid, user)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            val register = RegisterFieldsState(
                validationEmail(user.email), validationPassword(password)
            )
            runBlocking {_validation.send(register)  }
        }
    }

    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USERS_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val email = validationEmail(user.email)
        val passwordValidation = validationPassword(password)

        return email is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
    }
}