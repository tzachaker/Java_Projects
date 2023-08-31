package com.example.icebreaker.gameZone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class receiver extends AppCompatActivity {

    private ConstraintLayout player1Layout, player2Layout;
    private ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9;
    private TextView player1TV, player2TV, score;

    private final List<int[]> combinationsList = new ArrayList<>();
    private final List<String> doneBoxes = new ArrayList<>();
    private final List<ImageView> board = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;

    private boolean opponentFound = false;

    private String myName;
    private String myUid;
    private String opponentName;
    private String opponentUid;
    private String waitingMsg = "you don't have invites yet, try to invite yourself maybe";


    private int winning = 0;
    private int loses = 0;

    private String playerTurn = "";

    ValueEventListener turnsEventListener, wonEventListener;

    private final String[] boxesSelectedBy = {"", "", "", "", "", "", "", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_game);
        initFields();
        setGame();
    }

    private void initFields() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myUid = firebaseAuth.getUid();
        player1Layout = findViewById(R.id.player1Layout);
        player2Layout = findViewById(R.id.player2Layout);
        score = findViewById(R.id.score);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);
        image7 = findViewById(R.id.image7);
        image8 = findViewById(R.id.image8);
        image9 = findViewById(R.id.image9);
        myName = getIntent().getStringExtra("myName");
        player1TV = findViewById(R.id.player1TV);
        player2TV = findViewById(R.id.player2TV);

        combinationsList.add(new int[]{0, 1, 2});
        combinationsList.add(new int[]{3, 4, 5});
        combinationsList.add(new int[]{6, 7, 8});
        combinationsList.add(new int[]{0, 3, 6});
        combinationsList.add(new int[]{1, 4, 7});
        combinationsList.add(new int[]{2, 5, 8});
        combinationsList.add(new int[]{2, 4, 6});
        combinationsList.add(new int[]{0, 4, 8});
    }

    private void setGame() {
        player2TV.setText(myName);
        waitForOpponent();
        turnsListener();
        wonListener();
        board.add(image1);
        board.add(image2);
        board.add(image3);
        board.add(image4);
        board.add(image5);
        board.add(image6);
        board.add(image7);
        board.add(image8);
        board.add(image9);
        for (int i = 0; i < 9; i++) {
            boardListener(board.get(i),i+1);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void waitForOpponent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(receiver.this);
        final View PopUp = getLayoutInflater().inflate(R.layout.waitingpopout, null);
        builder.setView(PopUp);
        builder.setCancelable(false);
        Button exit = PopUp.findViewById(R.id.exit);
        exit.setOnClickListener(v -> {
            firebaseDatabase.getReference().child("connections").child(myUid).removeValue();
            firebaseDatabase.getReference().child("turns").child(myUid).removeValue();
            firebaseDatabase.getReference().child("won").child(myUid).removeValue();
            Intent intent = new Intent(receiver.this, Home.class);
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        firebaseDatabase.getReference().child("connections").child(myUid).child("receiverName").setValue(myName);
        firebaseDatabase.getReference().child("connections").child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!opponentFound) {
                    if (snapshot.hasChildren()) {
                        if(snapshot.getChildrenCount() == 1){
                            Toast.makeText(receiver.this, waitingMsg, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            int count = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if (count == 0){
                                    opponentName = dataSnapshot.getValue(String.class);
                                    player1TV.setText(opponentName);

                                    count++;
                                } else if (count == 1){
                                    opponentUid = dataSnapshot.getValue(String.class);
                                    break;
                                }
                            }
                        }
                        if (snapshot.getChildrenCount() == 3) {
                            playerTurn = opponentUid;
                            applyPlayerTurn(playerTurn);
                            opponentFound = true;
                            firebaseDatabase.getReference().child("turns").child(myUid).addValueEventListener(turnsEventListener);
                            firebaseDatabase.getReference().child("won").child(myUid).addValueEventListener(wonEventListener);
                            firebaseDatabase.getReference().child("connections").removeEventListener(this);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        if(!opponentFound){
            new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (int i = 0; i < 10; i++) {
                    if (opponentFound) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (opponentFound) {
                    dialog.dismiss();
                    // show an error message
                }
            }
        }.execute();
        }
    }

    private void turnsListener() {
        turnsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getChildrenCount() == 2) {
                        final int getBoxPosition = Integer.parseInt(dataSnapshot.child("box_position").getValue(String.class));
                        final String getPlayerId = dataSnapshot.child("player_id").getValue(String.class);
                        if (!doneBoxes.contains(String.valueOf(getBoxPosition))) {
                            doneBoxes.add(String.valueOf(getBoxPosition));
                            selectBox(board.get(getBoxPosition-1), getBoxPosition, getPlayerId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void wonListener() {
        wonEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("player_id")) {
                    String getWinPlayerId = snapshot.child("player_id").getValue(String.class);

                    AlertDialog.Builder builder = new AlertDialog.Builder(receiver.this);
                    final View PopUp = getLayoutInflater().inflate(R.layout.activity_win_dialog, null);
                    builder.setView(PopUp);
                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();

                    firebaseDatabase.getReference().child("connections").child(myUid).removeValue();
                    firebaseDatabase.getReference().child("turns").child(myUid).removeValue();
                    firebaseDatabase.getReference().child("won").child(myUid).removeValue();

                    final TextView messageTV = PopUp.findViewById(R.id.messageTV);
                    if (getWinPlayerId.equals(myUid)) {
                        messageTV.setText("You won the game!");
                        winning++;
                        String newScore = winning + ":" + loses;
                        score.setText(newScore);
                    } else {
                        messageTV.setText(opponentName + " won the game!");
                        loses++;
                        String newScore = winning + ":" + loses;
                        score.setText(newScore);
                    }
                    firebaseDatabase.getReference().child("turns").child(myUid).removeEventListener(turnsEventListener);
                    firebaseDatabase.getReference().child("won").child(myUid).removeEventListener(wonEventListener);
                    final Button startBtn = PopUp.findViewById(R.id.startNewMatch);
                    startBtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        resetGameData();
                    });

                    final Button quitBtn = PopUp.findViewById(R.id.QuitBtn);
                    quitBtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        firebaseDatabase.getReference().child("connections").child(myUid).removeValue();
                        firebaseDatabase.getReference().child("turns").child(myUid).removeValue();
                        firebaseDatabase.getReference().child("won").child(myUid).removeValue();
                        Intent intent = new Intent(receiver.this, Home.class);
                        startActivity(intent);
                    });
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void boardListener(ImageView imageView, int i) {
        String position = Integer.toString(i);
        imageView.setOnClickListener(v ->{
            if (!doneBoxes.contains(position) && playerTurn.equals(myUid)) {
                ((ImageView) v).setImageResource(R.drawable.x);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue(position);
                firebaseDatabase.getReference().child("turns").child(myUid).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(myUid);
                playerTurn = opponentUid;
            }
        });
    }

    private void applyPlayerTurn(String playerUniqueId2) {
        if (playerUniqueId2.equals(myUid)) {
            player1Layout.setBackgroundResource(R.drawable.round_back_dark_blue_stroke);
            player2Layout.setBackgroundResource(R.drawable.round_back_dark_blue_20);
        } else {
            player1Layout.setBackgroundResource(R.drawable.round_back_dark_blue_20);
            player2Layout.setBackgroundResource(R.drawable.round_back_dark_blue_stroke);
        }
    }

    private void selectBox(ImageView imageView, int selectedBoxPosition, String selectedByPlayer) {
        boxesSelectedBy[selectedBoxPosition - 1] = selectedByPlayer;
        if (selectedByPlayer.equals(myUid)) {
            imageView.setImageResource(R.drawable.x);
            playerTurn = opponentUid;
        } else {
            imageView.setImageResource(R.drawable.o);
            playerTurn = myUid;
        }
        applyPlayerTurn(playerTurn);
        if (doneBoxes.size() == 9) {
            AlertDialog.Builder builder = new AlertDialog.Builder(receiver.this);
            final View PopUp = getLayoutInflater().inflate(R.layout.activity_win_dialog, null);
            builder.setView(PopUp);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();

            firebaseDatabase.getReference().child("connections").child(myUid).removeValue();
            firebaseDatabase.getReference().child("turns").child(myUid).removeValue();
            firebaseDatabase.getReference().child("won").child(myUid).removeValue();

            final TextView messageTV = findViewById(R.id.messageTV);
            messageTV.setText("It's a draw!");
            firebaseDatabase.getReference().child("turns").child(myUid).removeEventListener(turnsEventListener);
            firebaseDatabase.getReference().child("won").child(myUid).removeEventListener(wonEventListener);
            final Button startBtn = PopUp.findViewById(R.id.startNewMatch);
            startBtn.setOnClickListener(v -> {
                dialog.dismiss();
                resetGameData();
            });

            final Button quitBtn = PopUp.findViewById(R.id.QuitBtn);
            quitBtn.setOnClickListener(v -> {
                dialog.dismiss();
                firebaseDatabase.getReference().child("connections").child(myUid).removeValue();
                firebaseDatabase.getReference().child("turns").child(myUid).removeValue();
                firebaseDatabase.getReference().child("won").child(myUid).removeValue();
                Intent intent = new Intent(receiver.this, Home.class);
                startActivity(intent);
            });            dialog.show();
        }
    }

    private boolean checkPlayerWin(String playerId) {
        boolean isPlayerWon = false;
        for (int i = 0; i < combinationsList.size(); i++) {
            final int[] combination = combinationsList.get(i);
            if (boxesSelectedBy[combination[0]].equals(playerId) &&
                    boxesSelectedBy[combination[1]].equals(playerId) &&
                    boxesSelectedBy[combination[2]].equals(playerId)) {
                isPlayerWon = true;
            }
        }
        return isPlayerWon;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseDatabase.getReference().child("connections").child(myUid).removeValue();
        firebaseDatabase.getReference().child("turns").child(myUid).removeValue();
        firebaseDatabase.getReference().child("won").child(myUid).removeValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            firebaseDatabase.getReference().child("connections").child(myUid).removeValue();
            firebaseDatabase.getReference().child("turns").child(myUid).removeValue();
            firebaseDatabase.getReference().child("won").child(myUid).removeValue();
        }
    }

    private void resetGameData() {
        for (int i = 0; i < 9; i++) {
            boxesSelectedBy[i] = "";
            board.get(i).setImageResource(R.drawable.transparent_back);
        }
        doneBoxes.clear();
        opponentFound = false;
        waitingMsg = "waiting for opponent...";
        setGame();
    }
}