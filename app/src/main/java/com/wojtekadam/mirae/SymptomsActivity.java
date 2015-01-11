package com.wojtekadam.mirae;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class SymptomsActivity extends Activity{
    private Button btnPotwierdz;
    private Button btnJeszcze_raz;
    private ImageButton btnSpeak;
    private EditText txtSpeechInput;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String speachResponse;
    private String pesel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnPotwierdz = (Button) findViewById(R.id.btnTak);
        btnJeszcze_raz = (Button) findViewById(R.id.btnJeszcze_raz);
        txtSpeechInput = (EditText) findViewById(R.id.txtSpeechInput);
        Intent i = getIntent();
        pesel = i.getStringExtra("pesel");


    btnSpeak.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            promptSpeechInput();
        }
    });

    btnJeszcze_raz.setOnClickListener(new View.OnClickListener(){
        @Override
    public void onClick(View v) {
            promptSpeechInput();
        }
    });

    btnPotwierdz.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*
            sykrypt do zapisu w bazie o uzycie tego skrytpu!!!!!!!!!!!!!!!!
             */

            Intent i = new Intent(getApplicationContext(), PickADateActivity.class);
            i.putExtra("pesel", pesel);
            startActivity(i);
        }
    });
    }


    private void promptSpeechInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.txtInputQuestion);
        try{
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }   catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supprted), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQ_CODE_SPEECH_INPUT: {
                if(resultCode == RESULT_OK && null !=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speachResponse = result.get(0);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }
        }
    }


}
