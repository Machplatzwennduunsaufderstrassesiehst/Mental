package de.soeiner.mental;

import java.util.ArrayList;

/**
 * Created by Malte on 09.04.2016.
 */
public class Voting {

    Game game;
    GameMode[] gameModes;
    ExerciseCreator[] exerciseCreators = {new MixedExerciseCreator2(), new SimpleMultExerciseCreator(), new MultExerciseCreator()};
    Suggestion[] suggestions;
    int voteCounter = 0;
    Suggestion revoteSuggestion;

    public Voting(Game game){
        this.game = game;
        GameMode[] gTemp = {new ClassicGameMode(game), new KnockoutGameMode(game), new ArenaGameMode(game), new SpeedGameMode(game)};
        gameModes = gTemp;
    }

    public void createGameModeSuggestions(){
        ArrayList<ExerciseCreator> tempExerciseCreators = new ArrayList<ExerciseCreator>();
        ArrayList<GameMode> tempGameModes = new ArrayList<GameMode>();
        suggestions = new Suggestion[4];

        for (int i = 0; i < exerciseCreators.length; i++) {
            tempExerciseCreators.add(exerciseCreators[i]);
        }
        for (int i = 0; i < gameModes.length; i++) {
            tempGameModes.add(gameModes[i]);
        }
        for (int i = 0; i < suggestions.length-1; i++) {
            int eIndex = (int) (Math.random() * tempExerciseCreators.size());
            int gIndex = (int) (Math.random() * tempGameModes.size());
            suggestions[i] = new Suggestion(tempGameModes.get(gIndex), tempExerciseCreators.get(eIndex), i);
            tempGameModes.remove(gIndex);
            //tempExerciseCreators.remove(eIndex);
        }

        revoteSuggestion = new Suggestion(gameModes[0], exerciseCreators[0], suggestions.length-1);
        revoteSuggestion.putName("Neue Vorschl&auml;ge!");
        suggestions[suggestions.length-1] = revoteSuggestion;
        voteCounter = 0;
        broadcastSuggestions();
    }

    public void broadcastSuggestions(){ //Abstimmung für nächsten gamemode
        System.out.println("callvote");
        for(int i = 0;i<game.joinedPlayers.size();i++){
            Player p = game.joinedPlayers.get(i);
            p.sendSuggestions(suggestions);
        }
    }

    public void receiveVote(int suggestionID, Player p) {
        System.out.println("receiveVote()");
            for (int i = 0; i < suggestions.length; i++) {
                if (suggestions[i].getPlayers().contains(p)) {
                    suggestions[i].downvote(p);
                    voteCounter--;
                }
            }
            voteCounter++;
            suggestions[suggestionID].upvote(p);

        broadcastSuggestions(); //unnötig
        checkForCompletion();
        }

    public void checkForCompletion(){
        System.out.println("checkForCompletion");
        if(revoteSuggestion.getPlayers().size() >= game.joinedPlayers.size()) {
            for (int i = 0; i < suggestions.length; i++) {
                suggestions[i].reset();
            }
            createGameModeSuggestions();
            voteCounter = 0;
            revoteSuggestion.reset();
        }
        if(voteCounter >= game.joinedPlayers.size()){
            int maxIndex = 0;
            for(int i = 1; i < suggestions.length; i++){
                if(suggestions[i].getVotes() > suggestions[maxIndex].getVotes()){
                    maxIndex = i;
                }
            }
            Suggestion votedForSuggestion = suggestions[maxIndex];
            game.gameMode = votedForSuggestion.gameMode;
            game.exerciseCreator = votedForSuggestion.exerciseCreator;
            broadcastSuggestions();
            voteCounter = 0;
            for(int i = 0; i < suggestions.length; i++){
                suggestions[i].reset();
            }
            synchronized (game.voteLock) {
                game.voteLock.notify();
            }
        }
        broadcastSuggestions();
    }
}