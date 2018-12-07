package com.example.vitalii.myapplication


import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController
import com.example.vitalii.myapplication.api.GitHubPOJO
import com.example.vitalii.myapplication.api.GitHubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    var response2:List<GitHubPOJO> = ArrayList()
    var posts: MutableList<GitHubPOJO> = ArrayList()
    lateinit var btn:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        btn = view.findViewById(R.id.button)
        btn.setOnClickListener {
            posts = ArrayList()

            val name:String = view!!.findViewById<EditText>(R.id.editText).text.toString()
            recyclerView = view!!.findViewById(R.id.posts_recycle_view)
            val layoutManager = LinearLayoutManager(this.activity!!)
            recyclerView.layoutManager = layoutManager

            val adapter = PostsAdapter(posts)
            recyclerView.adapter = adapter
            //HIDE KEYBOARD
            val inputMethodManager = this.activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(this.activity!!.currentFocus!!.windowToken, 0)

            val service = Retrofit.Builder()
                .baseUrl("https://api.github.com/") // CHANGE API
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitHubService::class.java)
            service.retrieveRepositories(name)
                .enqueue(object : Callback<List<GitHubPOJO>> {
                    override fun onResponse(call: Call<List<GitHubPOJO>>, response: Response<List<GitHubPOJO>>) {
                        response2 = response.body()!!
                        posts.addAll(response.body()!!)
                        response.body()?.forEach { println ("TAG_: $it")}
                        recyclerView.adapter?.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<List<GitHubPOJO>>, t: Throwable) {
                        //Toast.makeText(this@MainFragment, "Error occured while networking", Toast.LENGTH_SHORT).show()
                    }
                })
            recyclerView.addOnItemTouchListener(
                ClickListener(this.activity!!, recyclerView, object : ClickListener.OnItemClickListener {
                    override fun onLongItemClick(view: View?, position: Int) {
                    }

                    override fun onItemClick(view: View, position: Int) {
                        val url = posts!![position].htmlUrl
                        println("URL =  $url")
                        view.findNavController().navigate(MainFragmentDirections.actionMainFragmentToWebFragment(url))

                        //OPEN LINK IN SYSTEM DEFAULT BROWSER
//                  val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                  startActivity(browserIntent)
                    }
                })
            )
        }
        return view
    }
}
