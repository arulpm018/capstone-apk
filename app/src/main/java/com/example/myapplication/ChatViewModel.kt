package com.example.myapplication

import ChatRequest
import DocumentRequest
import RelatedDocument
import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.NetworkModule.apiService
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _chatResponse = MutableLiveData<String>() // Holds the raw response text
    val chatResponse: LiveData<String> get() = _chatResponse

    private val _relatedDocuments = MutableLiveData<List<RelatedDocument>?>()
    val relatedDocuments: MutableLiveData<List<RelatedDocument>?> = _relatedDocuments

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun sendMessage(message: String, context: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {

                // Make API call
                val response = apiService.chat(
                    ChatRequest(
                        query = message,
                        context = context,
                        chat_history = emptyList()
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("Chat_Response", "Response Body: $body")
                    Log.d("ChatResponse", "Response Body: $body")
                    if (body != null) {
                        _chatResponse.postValue(body.response) // Assuming `response` contains the text
                    } else {
                        _error.postValue("No response received.")
                    }
                } else {
                    _error.postValue("API Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun searchRelatedDocuments(query: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = apiService.getRelatedDocuments(DocumentRequest(title = query))

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("API_SUCCESS", "Response Body: $body")

                    // Access the 'related_documents' from the response body
                    _relatedDocuments.postValue(body?.related_documents)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Error Body: $errorBody")
                    _error.postValue("API Error: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Exception: ${e.localizedMessage}", e)
                _error.postValue("Exception: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }


}
