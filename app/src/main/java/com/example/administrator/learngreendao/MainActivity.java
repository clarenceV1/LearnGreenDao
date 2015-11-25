package com.example.administrator.learngreendao;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    DaoMaster daoMaster;
    DaoSession daoSession;
    NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSql();
        insertEntity();
     //  delete();
        print(query());
    }

    private void initSql() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "test-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        noteDao = daoSession.getNoteDao();
    }

    public Note creatNoteEntity(String comment, String text) {
        Note note = new Note();
        note.setComment(comment);
        note.setText(text);
        note.setDate(new Date());
        return note;
    }

    public void insertEntity() {
        List<Note> noteList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            noteList.add(creatNoteEntity("commnet" + i, "text" + i));
        }
        noteDao.insertInTx(noteList);
    }

    public void order() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.orderAsc(NoteDao.Properties.Comment);
        queryBuilder.orderDesc(NoteDao.Properties.Text);
        queryBuilder.list();
    }

    public void offsetAndLimit() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.limit(3);
        queryBuilder.offset(2);
        queryBuilder.list();
    }

    public void listLazy() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.listLazy();
    }

    public void limit() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.limit(3);
        queryBuilder.list();
    }

    public List<Note> queryRaw() {
        String sql = "WHERE " + NoteDao.Properties.Text.columnName + "=?";
        Query<Note> query = noteDao.queryRawCreate(sql, "text2");
        return query.list();
    }

    public List<Note> query() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.where(NoteDao.Properties.Comment.eq("commnet2"));
        Query<Note> query = queryBuilder.build();
        return query.list();//或queryBuilder.list();
    }

    public void delete() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.where(NoteDao.Properties.Comment.eq("commnet2"));
        DeleteQuery<Note> query = queryBuilder.buildDelete();
        query.executeDeleteWithoutDetachingEntities();
    }

    public void print(List<Note> noteList) {
        Log.d("note:-->", noteList.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
