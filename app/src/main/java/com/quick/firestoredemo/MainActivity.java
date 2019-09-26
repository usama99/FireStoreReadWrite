package com.quick.firestoredemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaDrm;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_QUOTE = "quote";
    public static final String KEY_AUTHOR = "author";

    EditText quoteText, authorText;
    Button save, retrieve;
    TextView showQuote;
    DocumentReference reference = FirebaseFirestore.getInstance().document("sampleData/inspiration");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quoteText = findViewById(R.id.quote);
        authorText = findViewById(R.id.author);
        save = findViewById(R.id.save);
        showQuote = findViewById(R.id.show);
        retrieve = findViewById(R.id.retrieve);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> saveData = new HashMap<>();
                String quotes = quoteText.getText().toString();
                String authors = authorText.getText().toString();
                saveData.put(KEY_QUOTE, quotes);
                saveData.put(KEY_AUTHOR, authors);

                reference.set(saveData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Document has been saved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String author = documentSnapshot.getString(KEY_AUTHOR);
                            String quote = documentSnapshot.getString(KEY_QUOTE);
                            showQuote.setText("\"" + quote + "-----" + author + "\"" );
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //for quick response from server to get results
        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    String author = documentSnapshot.getString(KEY_AUTHOR);
                    String quote = documentSnapshot.getString(KEY_QUOTE);
                    showQuote.setText("\"" + quote + "-----" + author + "\"" );
                }
                else if (e != null){
                    Toast.makeText(MainActivity.this, "Exception here" +
                            "!", Toast.LENGTH_SHORT).show();
                }
        }
        });
    }
}
