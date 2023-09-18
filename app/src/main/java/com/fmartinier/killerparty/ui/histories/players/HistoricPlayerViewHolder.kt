package com.fmartinier.killerparty.ui.histories.players

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.fmartinier.killerparty.R
import com.fmartinier.killerparty.databinding.FragmentHistoricPlayerBinding
import com.fmartinier.killerparty.model.Player
import com.fmartinier.killerparty.utils.showConfirmationDialog

class HistoricPlayerViewHolder(
    private val binding: FragmentHistoricPlayerBinding,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {

    lateinit var onPlayerKilled: ((Player) -> Unit)
    private val resources = context.resources

    fun bindPlayer(player: Player, killable: Boolean) {
        binding.playerName.text = player.name
        binding.playerState.text = resources.getString(player.state.translate())
        binding.buttonKill.isEnabled = killable
        if (killable) {
            binding.buttonKill.setImageResource(R.drawable.ic_target_enable)
        } else {
            binding.buttonKill.setImageResource(R.drawable.ic_target_disabled)
        }
        binding.buttonKill.setOnClickListener {
            showConfirmationDialog(
                context = context,
                confirmationMessage = context.resources.getString(
                    R.string.kill_confirmation,
                    player.name
                ),
                function = onPlayerKilled,
                params = player,
            )
        }
    }

}