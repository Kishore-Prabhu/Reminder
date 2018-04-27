package reminder.kishore.com;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notes {

    public static final String TABLE_NAME = "notes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String note;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Notes() {
    }

    public Notes(int id, String note, String timestamp) {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimestamp() {

        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(timestamp);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }
        return "";
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
