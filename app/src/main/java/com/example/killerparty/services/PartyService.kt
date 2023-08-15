package com.example.killerparty.services

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.widget.Toast
import com.example.killerparty.R
import com.example.killerparty.db.COLUMN_ID
import com.example.killerparty.db.COLUMN_WINNER
import com.example.killerparty.db.TABLE_PARTIES
import com.example.killerparty.db.executeUpdateQuery
import com.example.killerparty.db.repository.ChallengeRepository
import com.example.killerparty.db.repository.PartyRepository
import com.example.killerparty.db.repository.PlayerRepository
import com.example.killerparty.db.repository.PlayerToChallengeRepository
import com.example.killerparty.model.Challenge
import com.example.killerparty.model.Party
import com.example.killerparty.model.Player
import com.example.killerparty.model.enums.PartyState
import com.example.killerparty.utils.navigateTo
import kotlin.random.Random


class PartyService(val context: Context) {
    private val partyRepository = PartyRepository(context)
    private val challengeRepository = ChallengeRepository(context)
    private val playerRepository = PlayerRepository(context)
    private val playerToChallengeRepository = PlayerToChallengeRepository(context)
    private val smsService = SmsService(context)

    fun findOrCreate(): Party {
        return partyRepository.findOrCreate()
    }

    fun findAllBegan(): List<Party> {
        return partyRepository.findAll().filter { it.state != PartyState.NOT_STARTED }
    }

    fun beginParty(party: Party, players: List<Player>) {
        giveChallengeToPlayers(players)
        partyRepository.modifyStateById(party.id, PartyState.IN_PROGRESS)

        navigateTo(context, R.id.navigation_historic)
    }

    fun canBeginParty(context: Context, party: Party): Boolean {
        val players = findPlayers(party)
        val challenges = challengeRepository.findAll()
        return if (players.size < 3) {
            Toast.makeText(context, R.string.not_enought_players, Toast.LENGTH_SHORT).show()
            false
        } else if (challenges.size < players.size) {
            Toast.makeText(context, R.string.not_enought_challenges, Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    fun findPlayers(history: Party): List<Player> {
        return playerRepository.findAllFromParty(history)
    }

    fun declareWinner(player: Player, party: Party) {
        partyRepository.declareWinner(player, party)
    }

    /**
     * Give random challenge to each player, and insert them in DB
     */
    private fun giveChallengeToPlayers(players: List<Player>) {
        val availableChallenges: MutableList<Challenge> = mutableListOf()
        val availableTargets: MutableList<Player> = mutableListOf()
        availableChallenges.addAll(challengeRepository.findAll())
        availableTargets.addAll(players)
        players.forEach {

            // Select random challenge
            val randomChallengeIndex = Random.nextInt(availableChallenges.size)
            val randomChallenge = availableChallenges[randomChallengeIndex]
            availableChallenges.remove(randomChallenge)

            // Select random target
            var randomTargetIndex = Random.nextInt(availableTargets.size)
            var randomTarget = availableTargets[randomTargetIndex]
            while (randomTarget == it || playerToChallengeRepository.existByKillerAndTarget(
                    killer = randomTarget,
                    target = it
                )
            ) {
                randomTargetIndex = Random.nextInt(availableTargets.size)
                randomTarget = availableTargets[randomTargetIndex]
            }
            availableTargets.remove(randomTarget)

            smsService.sendSMS(
                it.phone,
                context.resources.getString(
                    R.string.sms_challenge_init,
                    randomTarget.name,
                    randomChallenge.description
                )
            )

            playerToChallengeRepository.insert(it.id, randomTarget.id, randomChallenge.id)
        }
    }
}