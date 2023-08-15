package com.example.killerparty.ui.histories

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.killerparty.R
import com.example.killerparty.databinding.FragmentHistoricBinding
import com.example.killerparty.model.Party
import com.example.killerparty.model.Player
import com.example.killerparty.model.enums.PlayerState
import com.example.killerparty.services.PartyService
import com.example.killerparty.services.PlayerService
import com.example.killerparty.ui.histories.players.HistoricPlayerViewAdapter

class HistoricViewHolder(
    private val fragmentHistoricBinding: FragmentHistoricBinding,
    private val context: Context,
    private val partyService: PartyService,
    private val playerService: PlayerService,
) : RecyclerView.ViewHolder(fragmentHistoricBinding.root) {

    @SuppressLint("NotifyDataSetChanged")
    fun bindHistory(party: Party) {
        // TODO : quand un joueur est killé, un sms est envoyé au killer pour définir sa nouvelle cible.
        val players = partyService.findPlayers(party)
        val resources = context.resources
        var winner = party.winner ?: resources.getString(R.string.no_winner_yet)
        fragmentHistoricBinding.date.text =
            resources.getString(R.string.party_date, party.date.toString())
        fragmentHistoricBinding.partyState.text =
            resources.getString(R.string.party_state, resources.getString(party.state.translate()))
        fragmentHistoricBinding.winner.text = resources.getString(R.string.winner, winner)
        fragmentHistoricBinding.playerList.apply {
            val adapter = HistoricPlayerViewAdapter(players, context)
            adapter.onPlayerKilled = {
                playerService.killPlayer(it)
                it.state = PlayerState.KILLED
                adapter.notifyDataSetChanged()
                val inLifePlayers = players.filter { player -> player.state == PlayerState.IN_LIFE }
                if (inLifePlayers.size == 1) {
                    winner = inLifePlayers[0].name
                    partyService.declareWinner(inLifePlayers[0], party)
                    party.winner = winner
                }
            }
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        println("players : $players")
    }

}