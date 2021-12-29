package com.amt.doineedit_firebase.Users

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface UserRepository {
    fun login(email:String, password:String)
    fun register(email:String, password: String)
}