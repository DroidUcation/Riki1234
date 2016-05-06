package com.example.riki.myapplication;

import android.app.ActionBar;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.riki.myapplication.OneFragment;
import com.example.riki.myapplication.TwoFragment;
import com.example.riki.myapplication.ThreeFragment;
import com.example.riki.myapplication.FourFragment;
import com.example.riki.myapplication.FiveFragment;
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        ContentValues values = new ContentValues();
    values.put(FlowerProvider.NAME,
            "kalanit");


        values.put(FlowerProvider.COLOR,
                "red, purple or blue");

        values.put(FlowerProvider.NUM_OF_LEAVES,
                "5");
        values.put(FlowerProvider.AT_NIGHT,
                "true");

        Uri uri = getContentResolver().insert(
                FlowerProvider.CONTENT_URI, values);


        String URL = "content://com.example.riki.myapplication/flowers";

        Uri flowers = Uri.parse(URL);
        Cursor c = managedQuery(flowers, null, null, null, "name");
        String atNight= "Doesn't open at night";
        Toast.makeText(this,
                "Values from DataBase:",
                Toast.LENGTH_SHORT).show();
        if (c.moveToFirst()) {
            if(c.getString(c.getColumnIndex( FlowerProvider.AT_NIGHT)).equals("true"))
                atNight= "Opens at night";

            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(FlowerProvider._ID)) +
                                ", " + c.getString(c.getColumnIndex(FlowerProvider.NAME)) +
                                ", " + atNight +
                                ", " + c.getString(c.getColumnIndex(FlowerProvider.COLOR)),

                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");
        adapter.addFragment(new FourFragment(), "FOUR");
        adapter.addFragment(new FiveFragment(), "FIVE");


        viewPager.setAdapter(adapter);
    }

    public void buttonClickColor(View view) {
        Button b=(Button)view;
        String name = view.getResources().getResourceName(view.getId());
        int num=0;
       for (int i=0;i<name.length();i++)
       {
           if(name.charAt(i)=='/')
               num=i+1;
       }
        name=name.substring(num,name.length());
        b.setText(name);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}