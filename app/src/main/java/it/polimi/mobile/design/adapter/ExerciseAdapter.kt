package it.polimi.mobile.design.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polimi.mobile.design.R
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.Workout

class ExerciseAdapter(private val exerciseList: ArrayList<Exercise>):RecyclerView.Adapter<ExerciseAdapter.MyViewHolder> (){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.exercise_list,parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseAdapter.MyViewHolder, position: Int) {
        val exercise: Exercise =exerciseList[position]


    }
    override fun getItemCount(): Int {
        return exerciseList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


    }
}