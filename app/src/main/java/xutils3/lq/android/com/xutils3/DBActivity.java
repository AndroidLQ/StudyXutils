package xutils3.lq.android.com.xutils3;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.User;

public class DBActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = this.getClass().getSimpleName();
    private Button initDB_btn, add_btn, delete_btn, modtify_btn, search_btn, sql_btn;
    private String sql = "select * from user where id>=5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        initDB_btn = (Button) findViewById(R.id.create_db);
        add_btn = (Button) findViewById(R.id.insert_btn);
        delete_btn = (Button) findViewById(R.id.delete_data);
        modtify_btn = (Button) findViewById(R.id.moditfy_data);
        search_btn = (Button) findViewById(R.id.search_btn);
        sql_btn = (Button) findViewById(R.id.sql_btn);
        initDB_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        modtify_btn.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        sql_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_db:
                initDb();
                break;
            case R.id.insert_btn:
                try {
                    dbAdd();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.moditfy_data:
                try {
                    dbUpdate();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.search_btn:
                try {
                    dbFind();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete_data:
                try {
                    dbDelete();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sql_btn:
                try {
                    doSql(sql);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    protected DbManager db;

    protected void initDb() {
        //本地数据的初始化
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("xutils3_db") //设置数据库名
                // 不设置dbDir时,默认为包名下databases目录下
                .setDbDir(new File("/sdcard")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
                .setDbVersion(2) //设置数据库版本,每次启动应用时将会检查该版本号,
                //发现数据库版本低于这里设置的值将进行数据库升级并触发DbUpgradeListener
                .setAllowTransaction(true)//设置是否开启事务,默认为false关闭事务
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                //设置数据库创建时的Listener
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        Log.i(TAG, "setTableCreateListener");
                    }
                })
                //数据库版本更新时回调
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        //balabala...
                        Log.i(TAG, "setDbUpgradeListener");
                    }
                });//设置数据库升级时的Listener,这里可以执行相关数据库表的相关修改,比如alter语句增加字段等
        db = x.getDb(daoConfig);
    }


    protected void dbAdd() throws DbException {
        //User user = new User("Kevingo","caolbmail@gmail.com","13299999999",new Date());
        //db.save(user);//保存成功之后【不会】对user的主键进行赋值绑定
        //db.saveOrUpdate(user);//保存成功之后【会】对user的主键进行赋值绑定
        //db.saveBindingId(user);//保存成功之后【会】对user的主键进行赋值绑定,并返回保存是否成功

        List<User> users = new ArrayList<User>();
        for (int i = 0; i < 10; i++) {
            //User的@Table注解onCreated属性加了name,email联合唯一索引.
            User user = new User("Kevingo" + System.currentTimeMillis() + i, "caolbmail@gmail.com", "13299999999", new Date());
            users.add(user);
        }
        db.saveBindingId(users);
        Toast.makeText(DBActivity.this, "【dbAdd】第一个对象:" + users.get(0).toString(), Toast.LENGTH_LONG).show();//user的主键Id不为0
    }

    protected void dbFind() throws DbException {
        //List<User> users = db.findAll(User.class);
        //showDbMessage("【dbFind#findAll】第一个对象:"+users.get(0).toString());

        //User user = db.findById(User.class, 1);
        //showDbMessage("【dbFind#findById】第一个对象:" + user.toString());

        //long count = db.selector(User.class).where("name","like","%kevin%").and("email","=","caolbmail@gmail.com").count();//返回复合条件的记录数
        //showDbMessage("【dbFind#selector】复合条件数目:" + count);

        List<User> users = db.selector(User.class)
                .where("name", "like", "%kevin%")
                .and("email", "=", "caolbmail@gmail.com")
                .orderBy("regTime", true)
                .limit(2) //只查询两条记录
                .offset(9) //偏移两个,从第三个记录开始返回,limit配合offset达到sqlite的limit m,n的查询
                .findAll();
        if (users == null || users.size() == 0) {
            return;//请先调用dbAdd()方法
        }
        Toast.makeText(DBActivity.this, "【dbFind#selector】复合条件数目:" + users.size(), Toast.LENGTH_LONG).show();//user的主键Id不为0
    }

    protected void dbDelete() throws DbException {
        List<User> users = db.findAll(User.class);
        if (users == null || users.size() == 0) {
            return;//请先调用dbAdd()方法
        }
        //db.delete(users.get(0)); //删除第一个对象
        //db.delete(User.class);//删除表中所有的User对象【慎用】
        //db.delete(users); //删除users对象集合
        //users =  db.findAll(User.class);
        // showDbMessage("【dbDelete#delete】数据库中还有user数目:" + users.size());

        WhereBuilder whereBuilder = WhereBuilder.b();
        whereBuilder.and("id", ">", "5").or("id", "=", "1").expr(" and mobile > '2015-12-29 00:00:01' ");
        db.delete(User.class, whereBuilder);
        users = db.findAll(User.class);
        Toast.makeText(DBActivity.this, "【dbDelete#delete】数据库中还有user数目:" + users.size(), Toast.LENGTH_LONG).show();
    }

    /**
     * 保存或更新实体类或实体类的List到数据库, 根据id和其他唯一索引判断数据是否存在.
     * void replace(Object entity) throws DbException;
     *
     * @throws DbException
     */
    protected void dbUpdate() throws DbException {
        List<User> users = db.findAll(User.class);
        if (users == null || users.size() == 0) {
            return;//请先调用dbAdd()方法
        }
        User user = users.get(0);
        user.setEmail(System.currentTimeMillis() / 1000 + "@email.com");
        //db.replace(user);
        //db.update(user);
        //db.update(user,"email");//指定只对email列进行更新

        WhereBuilder whereBuilder = WhereBuilder.b();
        whereBuilder.and("id", ">", "5").or("id", "=", "1").expr(" and mobile > '2015-12-29 00:00:01' ");
        db.update(User.class, whereBuilder,
                new KeyValue("email", System.currentTimeMillis() / 1000 + "@email.com")
                , new KeyValue("mobile", "18988888888"));//对User表中复合whereBuilder所表达的条件的记录更新email和mobile
    }

    /**
     * 通过org.xutils.DbManager对象中的一系列exec前缀的方法可以实现sql语句查询
     *
     * @param sql
     * @throws DbException
     */
    protected void doSql(String sql) throws DbException {
        Cursor cursor = db.execQuery(sql);
        User user = null;
        List<User> list = new ArrayList<>();
        if(cursor == null){
            Toast.makeText(DBActivity.this, "sql 语句有问题，cursor为null" , Toast.LENGTH_LONG).show();
            return;
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                String email = cursor.getString(cursor.getColumnIndex("EMAIL"));
                String mobile = cursor.getString(cursor.getColumnIndex("MOBILE"));
                String regTime = cursor.getString(cursor.getColumnIndex("REGTIME"));
                user = new User();
                user.setName(name == null ? "0" : name);
                user.setEmail(email == null ? "0" : email);
                user.setMobile(mobile == null ? "0" : mobile);
                user.setRegTime(regTime == null ? new Date() : new Date(Long.parseLong(regTime)));
                list.add(user);
            } while (cursor.moveToNext());
        }
        Toast.makeText(DBActivity.this, "查到的数据有:" + list.size(), Toast.LENGTH_LONG).show();
    }

}
