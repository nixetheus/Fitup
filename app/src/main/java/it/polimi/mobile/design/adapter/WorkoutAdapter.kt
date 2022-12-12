package it.polimi.mobile.design.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polimi.mobile.design.R
import it.polimi.mobile.design.databinding.ActivityWorkoutListBinding
import it.polimi.mobile.design.databinding.WorkoutListBinding
import it.polimi.mobile.design.entities.Workout

class WorkoutAdapter (private val workoutList: ArrayList<Workout>):RecyclerView.Adapter<WorkoutAdapter.MyViewHolder> (){





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.workout_list,parent, false)
    return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutAdapter.MyViewHolder, position: Int) {
        val workout:Workout =workoutList[position]
        holder.name.text=workout.name
        holder.type.text=workout.type
        holder.playlist.text=workout.spotifyPlaylistLink

    }
    override fun getItemCount(): Int {
        return workoutList.size
    }

    class MyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        /*private lateinit var binding: WorkoutListBinding
        val name : TextView=  binding.workoutName
        val type: TextView=  binding.workoutType
        val playList: TextView=  binding.workoutPlaylist
*/
        val name : TextView= itemView.findViewById(R.id.workoutName)
        val type: TextView= itemView.findViewById(R.id.workoutType)
        val playlist : TextView= itemView.findViewById(R.id.workoutPlaylist)

    }
}