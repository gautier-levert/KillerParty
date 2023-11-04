package com.fmartinier.killerparty.ui.party

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fmartinier.killerparty.databinding.FragmentPlayerBinding
import com.fmartinier.killerparty.model.Player
import java.net.URLEncoder

class PlayerViewHolder(
    private val context: Context,
    private val fragmentPlayerBinding: FragmentPlayerBinding,
    private val onPlayerRemoved: (Player) -> Unit = {}
) : RecyclerView.ViewHolder(fragmentPlayerBinding.root) {

    fun bindPlayer(player: Player) {
        fragmentPlayerBinding.playerName.text = player.name
        fragmentPlayerBinding.playerPhone.text = player.phone
        fragmentPlayerBinding.deleteIcon.setOnClickListener { onPlayerRemoved(player) }

        Glide.with(context)
            .load(
                "https://api.dicebear.com/7.x/adventurer-neutral/png?seed=${
                    URLEncoder.encode(
                        player.name,
                        "UTF-8"
                    )
                }"
            )
            .circleCrop()
            .into(fragmentPlayerBinding.playerAvatar)
    }

}