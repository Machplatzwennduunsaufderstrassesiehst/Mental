package de.soeiner.mental.gameFundamentals;

import java.util.ArrayList;

import de.soeiner.mental.exerciseCreators.ExerciseCreator;
import de.soeiner.mental.exerciseCreators.SimpleMultExerciseCreator;
import de.soeiner.mental.gameModes.GameMode;
import de.soeiner.mental.gameModes.arithmetics.MA_ArenaGameMode;
import de.soeiner.mental.gameModes.arithmetics.MA_BeatBobGameMode;
import de.soeiner.mental.gameModes.arithmetics.MA_Classic;
import de.soeiner.mental.gameModes.arithmetics.MA_Knockout;
import de.soeiner.mental.gameModes.arithmetics.MA_Speed;
import de.soeiner.mental.gameModes.traingame.TrainGame;
import de.soeiner.mental.gameModes.traingame.Train_Classic;
import de.soeiner.mental.gameModes.traingame.Train_Dynamic;
import de.soeiner.mental.gameModes.traingame.Train_SuddenDeath;

/**
 * Created by Malte on 09.04.2016.
 */
public class Voting {

    private Game game;
    private GameMode[] gameModes;
    private Suggestion[] suggestions;
    private Suggestion revoteSuggestion;
    int voteCounter = 0;

    public Voting(Game game) {
        this.game = game;

        if(game.type != null){ //wenn ein typ gesetzt ist, werden alle gameModes des Types rausgesuct
            int z = 0;
            GameMode[] all = {new Train_Classic(game)/*, new Train_Dynamic(game)*/, new Train_SuddenDeath(game), new MA_Classic(game), new MA_Knockout(game), new MA_ArenaGameMode(game), new MA_Speed(game), new MA_BeatBobGameMode(game)};
            for(GameMode mode : all){ if(mode.type.equals(game.getType())) z++; }
            gameModes = new GameMode[z];
            z = 0;
            for(GameMode mode : all){
                if(mode.type.equals(game.getType())){
                    gameModes[z] = mode;
                    z++;
                }
            }
         }else { //sonst wähle per hand aus
            gameModes = new GameMode[]{new MA_Classic(game)/*, ... */};
        }
        createGameModeSuggestions();
    }

    public void createGameModeSuggestions() {
        ArrayList<GameMode> tempGameModes = new ArrayList<GameMode>();
        suggestions = new Suggestion[4];

        for (int i = 0; i < gameModes.length; i++) {
            tempGameModes.add(gameModes[i]);
        }

        for (int i = 0; i < suggestions.length - 1; i++) {
            int gIndex = (int) (Math.random() * tempGameModes.size());
            ArrayList<ExerciseCreator> possibleExerciseCreators = tempGameModes.get(gIndex).getCompatibleExerciseCreators();
            int eIndex = (int) (Math.random() * possibleExerciseCreators.size());
            suggestions[i] = new Suggestion(tempGameModes.get(gIndex), possibleExerciseCreators.get(eIndex), i);
            if (tempGameModes.size() > 1) tempGameModes.remove(gIndex);
            //tempExerciseCreators.remove(eIndex);
        }
        revoteSuggestion = new Suggestion(gameModes[0], new SimpleMultExerciseCreator(), suggestions.length - 1);
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
        for (Suggestion suggestion : suggestions) {
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
                Suggestion votedForSuggestion = suggestions[maxIndex];
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