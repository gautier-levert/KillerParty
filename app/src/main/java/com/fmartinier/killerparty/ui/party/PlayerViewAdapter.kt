package com.fmartinier.killerparty.ui.party

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fmartinier.killerparty.databinding.FragmentPlayerBinding
import com.fmartinier.killerparty.model.Player

class PlayerViewAdapter(
    private val context: Context,
    private val onPlayerRemoved: (Player) -> Unit = {}
) : RecyclerView.Adapter<PlayerViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private var players: List<Player> = emptyList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = FragmentPlayerBinding.inflate(inflater, parent, false)
        return PlayerViewHolder(context, binding, onPlayerRemoved)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bindPlayer(players[position])
    }

    override fun getItemCount() = players.size

    override fun getItemId(position: Int): Long {
        return players[position].id.toLong()
    }

    fun updateContent(players: List<Player>) {
        this.players = players
        notifyDataSetChanged()
    }
}