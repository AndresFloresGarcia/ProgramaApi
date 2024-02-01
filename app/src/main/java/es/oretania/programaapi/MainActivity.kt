package es.oretania.programaapi

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import es.oretania.programaapi.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var termEditText: EditText
    private lateinit var resultTextView: TextView
    private lateinit var searchButton: Button
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var navigationButtonsLayout: LinearLayout

    private lateinit var binding: ActivityMainBinding

    private lateinit var urbanDictionaryService: UrbanDictionaryService
    private var definitions: List<Definition> = emptyList()
    private var currentDefinitionIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        termEditText = findViewById(R.id.termEditText)
        resultTextView = findViewById(R.id.resultTextView)
        searchButton = findViewById(R.id.searchButton)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        navigationButtonsLayout = findViewById(R.id.navigationButtonsLayout)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://mashape-community-urban-dictionary.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("X-RapidAPI-Key", "29a5afa823msh53a26fd231b51adp183a39jsn1551d487e5ee")
                        .build()
                    chain.proceed(newRequest)
                }.build()
            )
            .build()

        urbanDictionaryService = retrofit.create(UrbanDictionaryService::class.java)

        searchButton.setOnClickListener {
            val term = termEditText.text.toString()
            if (term.isNotEmpty()) {
                getDefinitions(term)
            }
        }

        previousButton.setOnClickListener {
            showDefinitionAtIndex(currentDefinitionIndex - 1)
        }

        nextButton.setOnClickListener {
            showDefinitionAtIndex(currentDefinitionIndex + 1)
        }

        navigationButtonsLayout.visibility = View.GONE
    }

    private fun getDefinitions(term: String) {
        val call: Call<DefinitionResponse> = urbanDictionaryService.getDefinitions(term)
        call.enqueue(object : Callback<DefinitionResponse> {
            override fun onResponse(call: Call<DefinitionResponse>, response: Response<DefinitionResponse>) {
                if (response.isSuccessful && response.body() != null && response.body()!!.definitions.isNotEmpty()) {
                    definitions = response.body()!!.definitions
                    currentDefinitionIndex = 0
                    showDefinitionAtIndex(currentDefinitionIndex)
                    navigationButtonsLayout.visibility = View.VISIBLE
                } else {
                    resultTextView.text = "No definitions found for the term."
                    navigationButtonsLayout.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<DefinitionResponse>, t: Throwable) {
                t.printStackTrace()
                resultTextView.text = "Error fetching data. Please try again."
                navigationButtonsLayout.visibility = View.GONE
            }
        })
    }

    private fun showDefinitionAtIndex(index: Int) {
        if (index in definitions.indices) {
            currentDefinitionIndex = index
            val currentDefinition: Definition = definitions[index]

            val displayText = "Definition: ${currentDefinition.definition}\n\n" +
                    "Author: ${currentDefinition.author}\n\n" +
                    "Example: ${currentDefinition.example}\n\n" +
                    "Permalink: ${currentDefinition.permalink}"

            resultTextView.text = displayText
        }
    }
}

