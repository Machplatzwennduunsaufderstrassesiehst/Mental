package de.soeiner.mental.main;

import java.util.ArrayList;

import de.soeiner.mental.exerciseCreators.ExerciseCreator;
import de.soeiner.mental.exerciseCreators.SimpleMultExerciseCreator;

/**
 * Created by Malte on 09.04.2016.
 */
public class Voting {

    private Game game;
    private GameMode[] gameModes;
    private VotingSuggestion[] suggestions;
    private VotingSuggestion revoteSuggestion;
    int voteCounter = 0;

    public Voting(Game game, GameMode[] availableGameModes) {
        this.game = game;
        this.gameModes = availableGameModes;

        createGameModeSuggestions();
    }

    public void createGameModeSuggestions() {
        ArrayList<GameMode> tempGameModes = new ArrayList<GameMode>();
        suggestions = new VotingSuggestion[4];

        for (int i = 0; i < gameModes.length; i++) {
            tempGameModes.add(gameModes[i]);
        }

        for (int i = 0; i < suggestions.length - 1; i++) {
            int gIndex = (int) (Math.random() * tempGameModes.size());
            ArrayList<ExerciseCreator> possibleExerciseCreators = tempGameModes.get(gIndex).getCompatibleExerciseCreators();
            int eIndex = (int) (Math.random() * possibleExerciseCreators.size());
            suggestions[i] = new VotingSuggestion(tempGameModes.get(gIndex), possibleExerciseCreators.get(eIndex), i);
            if (tempGameModes.size() > 1) tempGameModes.remove(gIndex);
            //tempExerciseCreators.remove(eIndex);
        }
        revoteSuggestion = new VotingSuggestion(gameModes[0], new SimpleMultExerciseCreator(), suggestions.length - 1);
        revoteSuggestion.putName("Neue Vorschläge!");
        suggestions[suggestions.length - 1] = revoteSuggestion;
        voteCounter = 0;
        broadcastSuggestions();
    }

    public void broadcastSuggestions() { //Abstimmung für nächsten gamemode
        System.out.println("broadcastSuggestions");
        for (int i = 0; i < game.joinedPlayers.size(); i++) {
            Player p = game.joinedPlayers.get(i);
            p.sendSuggestions(suggestions);
        }
    }

    public void receiveVote(int suggestionID, Player p) {
        for (VotingSuggestion suggestion : suggestions) {
            if (suggestion.getPlayers().contains(p)) {
                suggestion.downvote(p);
                voteCounter--;
            }
        }
        voteCounter++;
        suggestions[suggestionID].upvote(p);
        checkForCompletion();
    }

    public void checkForCompletion() {
        System.out.println("checkForCompletion");
        System.out.println(voteCounter);
        System.out.println(game.joinedPlayers.size());
        System.out.println(revoteSuggestion.getPlayers().size());
        if (game.joinedPlayers.size() != 0) {
            if (revoteSuggestion.getPlayers().size() >= game.joinedPlayers.size()) {
                for (int i = 0; i < suggestions.length; i++) {
                    suggestions[i].reset();
                }
                createGameModeSuggestions();
                voteCounter = 0;
                revoteSuggestion.reset();
            }
            if (voteCounter >= game.joinedPlayers.size()) {
                int maxIndex = 0;
                for (int i = 1; i < suggestions.length; i++) {
                    if (suggestions[i].getVotes() > suggestions[maxIndex].getVotes()) {
                        maxIndex = i;
                    }
                }
                VotingSuggestion votedForSuggestion = suggestions[maxIndex];
                game.gameMode = votedForSuggestion.gameMode;
                game.exerciseCreator = votedForSuggestion.exerciseCreator;
                broadcastSuggestions();
                voteCounter = 0;
                for (int i = 0; i < suggestions.length; i++) {
                    suggestions[i].reset();
                }
                synchronized (game.voteLock) {
                    game.voteLock.notify();
                }
            }
            broadcastSuggestions();
        }
    }
}