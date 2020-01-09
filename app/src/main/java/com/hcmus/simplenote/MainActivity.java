package com.hcmus.simplenote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> notes = new ArrayList<>();
    EditText noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get data from Shared Preferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.hcmus.simplenote", Context.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("notes", null);

        if (set == null) {
            Toast.makeText(this, "Null pref!", Toast.LENGTH_SHORT).show();
            notes.add("New note");
        } else {
            notes = new ArrayList<>(set);
        }
        ;

        //List notes using array adapter
        final ListView listNote = (ListView) findViewById(R.id.listNote);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.tiltes_layout, R.id.title_id, notes);
        listNote.setAdapter(arrayAdapter);

        //Note's text + input
        noteText = (EditText) findViewById(R.id.editText);

        //Add new note button
        final ImageButton newNoteBtn = (ImageButton) findViewById(R.id.newnoteButton);
        newNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayAdapter.add("New note");
                arrayAdapter.notifyDataSetChanged();

                Save(notes);
            }
        });



        //Delete button
        ImageButton deleteBtn = (ImageButton) findViewById(R.id.deleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = DeleteConfirmation(arrayAdapter);
                diaBox.show();
            }
        });

        //Show-Hide tiltes button
        ImageButton hideTiltes = (ImageButton) findViewById(R.id.tabContent);
        hideTiltes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listNote.getVisibility() == view.VISIBLE) {
                    listNote.setVisibility(view.GONE);

                } else listNote.setVisibility(view.VISIBLE);
            }
        });


        //Using text watcher to edit note
        listNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                noteText.clearFocus(); //To stop text watcher
                noteText.setText(notes.get(position));

                final TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //Add note content to string array
                        notes.set(position, String.valueOf(s));
                        arrayAdapter.notifyDataSetChanged();

                        //Save change
                        Save(notes);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                //Clear text watcher after typing
                noteText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            noteText.addTextChangedListener(textWatcher);
                        } else noteText.removeTextChangedListener(textWatcher);

                    }
                });


            }


        });

        //Search bar
        EditText searchInput = (EditText) findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                arrayAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }


    //Save change to Shared Preferences
    public void Save(ArrayList<String> notes) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.hcmus.simplenote", Context.MODE_PRIVATE);

        HashSet<String> set = new HashSet<>(notes);

        sharedPreferences.edit().putStringSet("notes", set).apply();
    }

    //Delete dialog box
    public AlertDialog DeleteConfirmation(final ArrayAdapter arrayAdapter)
    {
        AlertDialog deleteDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Delete this message?")
                .setIcon(R.drawable.delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        arrayAdapter.remove(noteText.getText().toString());
                        arrayAdapter.notifyDataSetChanged();
                        noteText.setText("");
                        Save(notes);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return deleteDialogBox;
    }


}

