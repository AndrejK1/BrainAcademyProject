package com.andrejkgames.brainacademyproject;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.andrejkgames.brainacademyproject.database.RoomDb;
import com.andrejkgames.brainacademyproject.list.Groups;
import com.andrejkgames.brainacademyproject.list.GroupsAdapter;
import com.andrejkgames.brainacademyproject.database.Product;
import com.andrejkgames.brainacademyproject.list.ProductViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductViewHolder.Updatable {

    RecyclerView recycler;
    RoomDb myDb;
    Handler mainHandler;
    List<String> groupNames = new ArrayList<>();

    public static final String GET_GROUPS = "com.andrejkgames.brainacademyproject.GET_GROUPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });

        // my db
        myDb = Room.databaseBuilder(getApplicationContext(),
                RoomDb.class, "globalDatabase").build();

        // recycler
        recycler = findViewById(R.id.recycler_list);

        mainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<Groups> groups = msg.getData().getParcelableArrayList(GET_GROUPS);
                GroupsAdapter adapter = new GroupsAdapter(MainActivity.this, groups, MainActivity.this);
                recycler.setAdapter(adapter);
                recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
        };

        updateRecycler();
    }

    public void updateRecycler(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Groups> groups = new ArrayList<>();
                groupNames = new ArrayList<>();

                next: for (Product product : myDb.getTableDAO().getNotBoughtItems()) {
                    for (Groups group : groups) {
                        if (product.getGroupName().equals(group.getGroupName())) {
                            group.addProduct(product);
                            continue next;
                        }
                    }
                    groups.add(new Groups(product.getGroupName(), product));
                    groupNames.add(product.getGroupName());
                }
                groupNames.add(getResources().getString(R.string.text_add_group));

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(MainActivity.GET_GROUPS, new ArrayList<>(groups));
                message.setData(bundle);
                mainHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public void updateProduct(final Product product) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myDb.getTableDAO().updateItem(product);
                updateRecycler();
            }
        }).start();
    }

    @Deprecated
    @Override
    public void deleteProduct(Product product) {}

    private void showMenu(){
        final View addView = LayoutInflater.from(this).inflate(R.layout.add_product, null);
        addView.findViewById(R.id.inp_groups).setVisibility(View.GONE);
        Spinner spinner = addView.findViewById(R.id.group_list_spinner);
        spinner.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, groupNames));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == parent.getCount() - 1) {
                    addView.findViewById(R.id.inp_groups).setVisibility(View.VISIBLE);
                } else {
                    addView.findViewById(R.id.inp_groups).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        AlertDialog addGroup = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.text_add_element)
                .setView(addView)
                .setPositiveButton(R.string.text_add_element, null)
                .setNegativeButton(R.string.text_cancel, null)
                .create();
            addGroup.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (productAdd(addView))
                                dialog.dismiss();
                        }
                    });
                }
            });
        addGroup.show();
    }

    private boolean productAdd(final View addView) {
        final String title = ((EditText)addView.findViewById(R.id.inp_title)).getText().toString();
        final String group = ((EditText)addView.findViewById(R.id.inp_groups)).getText().toString();
        final String price = ((EditText)addView.findViewById(R.id.inp_price)).getText().toString();
        final String description = ((EditText)addView.findViewById(R.id.inp_description)).getText().toString();
        final Spinner spinner = addView.findViewById(R.id.group_list_spinner);

        if (!title.isEmpty()) {
            if (addView.findViewById(R.id.inp_groups).getVisibility() == View.GONE  || !group.isEmpty()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myDb.getTableDAO().addItems(
                                new Product.ProductBuilder(title,
                                        addView.findViewById(R.id.inp_groups).getVisibility() == View.GONE
                                                ? spinner.getSelectedItem().toString()
                                                : ((EditText)addView.findViewById(R.id.inp_groups)).getText().toString())
                                        .description(description)
                                        .price( !price.isEmpty() ? Double.valueOf(price) : 0 )
                                        .buildProduct()
                        );//addItems
                        updateRecycler();
                    }// run
                }).start(); //Thread
                return true;
            } else
                Toast.makeText(this, getResources().getText(R.string.err_null_group), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, getResources().getText(R.string.err_null_title), Toast.LENGTH_SHORT).show();
        return false;
    } // method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//создаем меню .inflate передаем меню
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//отрабатывае каждый раз когда мы выбираем один из пунктов меню
        if (item.getItemId() == R.id.action_go_to_bought) {
            startActivity(new Intent(this, BoughtItemsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
