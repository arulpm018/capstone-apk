import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    sealed class AuthState {
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
        object Success : AuthState()
    }

    fun signUp(email: String, password: String, name: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                    )
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }
}