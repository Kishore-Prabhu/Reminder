package reminder.kishore.com;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder>
{


    private Context mContext;
    private List<Notes> notesList;
    DatabaseHelper db ;

    public NoteAdapter() {
    }

    public NoteAdapter(Context mContext, List<Notes> notesList) {
        this.mContext = mContext;
        this.notesList = notesList;
        db = new DatabaseHelper(mContext);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Notes notes = notesList.get(position);

        holder.notes.setText(notes.getNote());
        holder.timestamp.setText(notes.getTimestamp());
        holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showActionsDialog(holder.getAdapterPosition());
                return  true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView notes,timestamp;
        LinearLayout rootLayout;

    public MyViewHolder(View itemView) {
        super(itemView);
        notes = itemView.findViewById(R.id.note);
        timestamp = itemView.findViewById(R.id.timestamp);
        rootLayout = itemView.findViewById(R.id.rootLayout);


    }
}
    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {

        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Notes note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
        View view = layoutInflaterAndroid.inflate(R.layout.notedialog, null,false);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? "New Note" : "Edit Note");

        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(mContext, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(inputNote.getText().toString(), position);
                } else {
                    // create new note
                    createNote(inputNote.getText().toString());
                }
            }
        });
    }
    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        notifyItemRemoved(position);
        toggleEmptyNotes();
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0) {
            //noNotesView.setVisibility(View.GONE);
        } else {
            //noNotesView.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String note, int position) {
        Notes n = notesList.get(position);
        // updating note text
        n.setNote(note);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);
        notifyItemChanged(position);
       // mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note);

        // get the newly inserted note from db
        Notes n = db.getNote(id);

        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n);

            // refreshing the list
            notifyDataSetChanged();
           // mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }
}
