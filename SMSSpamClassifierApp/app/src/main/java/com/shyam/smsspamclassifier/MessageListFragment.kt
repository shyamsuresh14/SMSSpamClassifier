package com.shyam.smsspamclassifier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shyam.smsspamclassifier.dummy.DummyContent

/*Message ListView Fragment*/
class MessageListFragment : Fragment() {
    private var mColumnCount = 1
    private var mLabel = "Ham"
    private var recyclerView : RecyclerView? = null
    private var messageList = emptyList<Message>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mLabel = arguments!!.getString(ARG_LABEL).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_message_list_list, container, false)

        if (view is RecyclerView) {
            val context = view.getContext()
            recyclerView = view
            if (mColumnCount <= 1) {
                recyclerView!!.layoutManager = LinearLayoutManager(context)
            } else {
                recyclerView!!.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            val dbAccess = DBAccess(requireContext())
            messageList = dbAccess.retrieve(mLabel)
            recyclerView!!.adapter = MyItemRecyclerViewAdapter(messageList)
        }

        return view
    }

    companion object {
        private const val ARG_LABEL = "label"
        fun newInstance(label: String): MessageListFragment {
            val fragment = MessageListFragment()
            val args = Bundle()
            args.putString(ARG_LABEL, label)
            fragment.arguments = args
            return fragment
        }
    }
}