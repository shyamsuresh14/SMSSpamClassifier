package com.shyam.smsspamclassifier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shyam.smsspamclassifier.dummy.DummyContent.DummyItem
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

/*Message List Adapter*/
class MyItemRecyclerViewAdapter(private val mValues: MutableList<Message>) :
    RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_message_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.mSender.text = mValues[position].sender

        if(mValues[position].body.length > 45)
            holder.mBody.text =  mValues[position].body.substring(0, 45) + "...."
        else
            holder.mBody.text =  mValues[position].body

        val date = Date(Timestamp(mValues[position].timestamp.toLong()).time)
        val formatter = SimpleDateFormat("HH:mm")
        holder.mTime.text = formatter.format(date)
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mSender: TextView = mView.findViewById<View>(R.id.sender) as TextView
        val mBody: TextView = mView.findViewById<View>(R.id.body) as TextView
        val mTime: TextView = mView.findViewById<View>(R.id.timestamp) as TextView

        override fun toString(): String {
            return super.toString() + " '" + mBody.text + "'"
        }
    }

}