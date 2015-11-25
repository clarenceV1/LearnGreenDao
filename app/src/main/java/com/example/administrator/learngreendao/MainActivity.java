package com.example.administrator.learngreendao;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class MainActivity extends Activity {
    SQLiteDatabase db;
    DaoMaster daoMaster;
    DaoSession daoSession;
    NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSql();
        insertAllEntity();
//        delete();
//        querySql();
//        query("comment2");
//        queryRaw();
//        limit();
//        order();
//        offsetAndLimit();
       // update();
        load();
    }

    public void initSql() {
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

    public Note creatNoteEntity(long id, String comment, String text) {
        Note note = creatNoteEntity(comment, text);
        note.setId(id);
        return note;
    }

    public void insertAllEntity() {
        List<Note> noteList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            noteList.add(creatNoteEntity("commnet" + i, "text" + i));
        }
        noteDao.insertInTx(noteList);
    }

    public void update() {
        long id= insertEntity();
        Note note = creatNoteEntity(id, "888", "888");
        noteDao.update(note);
        querySql();
    }

    public long insertEntity() {
       Note note=creatNoteEntity("999", "999");
       return noteDao.insert(note);
    }
    public void order() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.orderAsc(NoteDao.Properties.Comment);
        queryBuilder.orderDesc(NoteDao.Properties.Text);
        List<Note> noteList = queryBuilder.list();//queryBuilder.list();等价于 Query<Note> query = queryBuilder.build();+query.list()
        print(noteList);
    }

    public void offsetAndLimit() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.limit(3);
        queryBuilder.offset(2);
        List<Note> noteList = queryBuilder.list();//queryBuilder.list();等价于 Query<Note> query = queryBuilder.build();+query.list()
        print(noteList);
    }

    public void listLazy() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.listLazy();
    }

    public List<Note> limit() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.limit(3);
        return queryBuilder.list();
    }

    public void queryRaw() {
        String sql = "WHERE " + NoteDao.Properties.Text.columnName + "=?";
        Query<Note> query = noteDao.queryRawCreate(sql, "text2");
        List<Note> noteList = query.list();//queryBuilder.list();等价于 Query<Note> query = queryBuilder.build();+query.list()
        print(noteList);
    }

    public void querySql() {
        String orderBy = NoteDao.Properties.Text.columnName + " COLLATE LOCALIZED ASC";
        Cursor cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(), null, null, null, null, orderBy);
        List<Note> noteList = new ArrayList<>();
        Note note = null;
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(NoteDao.Properties.Id.columnName));
            String text = cursor.getString(cursor.getColumnIndex(NoteDao.Properties.Text.columnName));
            String comment = cursor.getString(cursor.getColumnIndex(NoteDao.Properties.Comment.columnName));
            note = creatNoteEntity(id, text, comment);
            noteList.add(note);
        }
        print(noteList);
    }

    public void query(String comment) {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.where(NoteDao.Properties.Comment.eq(comment));
        Query<Note> query = queryBuilder.build();
        List<Note> noteList = query.list();//queryBuilder.list();等价于 Query<Note> query = queryBuilder.build();+query.list()
        print(noteList);
    }

    public void delete() {
        QueryBuilder<Note> queryBuilder = noteDao.queryBuilder();
        queryBuilder.where(NoteDao.Properties.Comment.eq("commnet2"));
        DeleteQuery<Note> query = queryBuilder.buildDelete();
        query.executeDeleteWithoutDetachingEntities();
        querySql();
    }

    public void load(){
        List<Note> noteList = noteDao.loadAll();//noteDao.load(1l);
        print(noteList);
    }

    public void print(List<Note> noteList) {
        Log.d("note:-->", noteList.toString());
    }
}
