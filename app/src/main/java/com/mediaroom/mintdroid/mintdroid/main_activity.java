package com.mediaroom.mintdroid.mintdroid;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.CompoundButton;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;


public class main_activity extends AppCompatActivity {

    private int lastnavtab = R.id.navigationtab_remote;
    private int lastSelectedItemId = R.id.navigation_remote;
    private List<Integer> buttonRemoteMap = new ArrayList<Integer>();
    private List<Integer> buttonIpMap = new ArrayList<Integer>();
    private List<Integer> buttonSettingMap = new ArrayList<Integer>();
    private List<String> ipSelected = new ArrayList<String>();
    private List<String> ipAdded = new ArrayList<String>();
    private Map<String,View> ip_tr_mapping = new HashMap<String,View>();
    private List<String> ipSaved;
    private boolean newStartApp = true;
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemid = item.getItemId();
            switch (itemid) {
                case R.id.navigation_remote:
                    setContentView(R.layout.activity_remote);
                    registerButtonOnClick(itemid);
                    lastnavtab = R.id.navigationtab_remote;
                    lastSelectedItemId = R.id.navigation_remote;
                    break;
                case R.id.navigation_ip:
                    setContentView(R.layout.activity_ip);
                    registerButtonOnClick(itemid);
                    lastnavtab = R.id.navigationtab_ip;
                    lastSelectedItemId = R.id.navigation_ip;
                    loadIps();
                    break;
                case R.id.navigation_settings:
                    setContentView(R.layout.activity_settings);
                    registerButtonOnClick(itemid);
                    lastnavtab = R.id.navigationtab_settings;
                    lastSelectedItemId = R.id.navigation_settings;
                    break;
            }
            setmOnNavigationItemSelectedListener();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        setmOnNavigationItemSelectedListener();
        InitButtonList();
        if(newStartApp)
            loadIpsFromPreferences();
        registerButtonOnClick(R.layout.activity_remote);
    }

    private void setmOnNavigationItemSelectedListener()
    {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(lastnavtab);
        navigation.setSelectedItemId(lastSelectedItemId);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void addIpToTable()
    {
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.textlayout_ip);
        String ipToAdd = textInputLayout.getEditText().getText().toString();
        if(ipAdded.contains(ipToAdd)||ipToAdd.length() <=0 || !ipToAdd.matches(IPADDRESS_PATTERN))
            return;
        addIp(ipToAdd);
        textInputLayout.getEditText().setText("");
        saveIpsToPreferences();
    }

    private void checkAndAddSavedIp()
    {
        if(ipSaved == null || ipSaved.size() <= 0)
            return;

        for(String ip : ipSaved)
        {
            addIp(ip);
        }
        ipSaved.clear();
    }

    private void addIp(String ipToAdd)
    {
        if(ip_tr_mapping != null && ip_tr_mapping.containsKey(ipToAdd))
            return;
        TableLayout tl = (TableLayout) findViewById(R.id.table_ip);
        TableRow tr = new TableRow(this);
        CheckBox cb = new CheckBox(this);
        cb.setText(ipToAdd);
        if(ipSelected != null && ipSelected.contains(ipToAdd))
        {
            cb.setChecked(true);
        }
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                          @Override
                                          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                              String selectedIp = buttonView.getText().toString();
                                              if (isChecked) {
                                                  ipSelected.add(selectedIp);
                                                  saveIpsToPreferences();
                                              } else {
                                                  ipSelected.remove(selectedIp);
                                                  saveIpsToPreferences();
                                              }
                                          }
                                      }
        );
        tr.addView(cb);
        tl.addView(tr);
        ip_tr_mapping.put(ipToAdd, tr);
        if(ipAdded != null && ipAdded.contains(ipToAdd))
            return;
        ipAdded.add(ipToAdd);
    }

    private void removeIpFromTable()
    {
        if(ipSelected == null || ipSelected.size() == 0)
            return;
        TableLayout tl = (TableLayout) findViewById(R.id.table_ip);

        for(String ip : ipSelected)
        {
            tl.removeView(ip_tr_mapping.get(ip));
            ip_tr_mapping.remove(ip);
            ipAdded.remove(ip);
        }
        ipSelected.clear();
        saveIpsToPreferences();
    }

    private void saveIpsToPreferences()
    {
        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.datasave_ipdata),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(getString(R.string.datasave_ipdatatag), new HashSet<String>(ipAdded));
        editor.putStringSet(getString(R.string.datasave_ipselectedtag), new HashSet<String>(ipSelected));
        editor.commit();
    }

    private void loadIpsFromPreferences()
    {
        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.datasave_ipdata),Context.MODE_PRIVATE);
        if(preferences != null && preferences.contains(getString(R.string.datasave_ipdatatag))) {
            ipSaved = new ArrayList<String>(preferences.getStringSet(getString(R.string.datasave_ipdatatag), null));
            if(preferences.contains(getString(R.string.datasave_ipselectedtag))) {
                ipSelected = new ArrayList<String>(preferences.getStringSet(getString(R.string.datasave_ipselectedtag), null));
            }
        }

    }


    private void loadIps()
    {
        ip_tr_mapping.clear();
        checkAndAddSavedIp();
        if(ipAdded != null && ipAdded.size() > 0) {
            for (String ip : ipAdded) {
                addIp(ip);
            }
        }
    }

    private void registerButtonOnClick(int view)
    {
        if(view == R.id.navigation_ip) {
            for (Integer id : buttonIpMap) {
                Button button = (Button) findViewById(id);
                if(id == R.id.button_add)
                    button.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {
                                                        addIpToTable();
                                                    }
                                                }
                    );

                if(id == R.id.button_remove)
                    button.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    removeIpFromTable();
                                                }
                                            }
                    );
            }
        }

        if(view == R.id.navigation_remote)
        {
            for(Integer id : buttonRemoteMap)
            {
                Button button = (Button) findViewById(id);
                final String remotekey = button.getContentDescription().toString();
                button.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {
                                                  registerRemoteKeyFunction(remotekey);
                                              }
                                          }
                );
            }
        }
    }

    private void registerRemoteKeyFunction(String key)
    {
        if(ipSelected != null && ipSelected.size() > 0) {
            for (String ip : ipSelected) {
                try {
                    (new StbCommunicationThread(ip, key)).start();
                } catch (Exception e) {
                }
            }
        }
    }

    private void InitButtonList()
    {
        buttonIpMap.add(R.id.button_add);
        buttonIpMap.add(R.id.button_remove);
        buttonRemoteMap.add(R.id.button_app1);
        buttonRemoteMap.add(R.id.button_app2);
        buttonRemoteMap.add(R.id.button_app3);
        buttonRemoteMap.add(R.id.button_app4);
        buttonRemoteMap.add(R.id.button_app5);
        buttonRemoteMap.add(R.id.button_app6);
        buttonRemoteMap.add(R.id.button_back);
        buttonRemoteMap.add(R.id.button_chdown);
        buttonRemoteMap.add(R.id.button_chup);
        buttonRemoteMap.add(R.id.button_down);
        buttonRemoteMap.add(R.id.button_exit);
        buttonRemoteMap.add(R.id.button_ff);
        buttonRemoteMap.add(R.id.button_guide);
        buttonRemoteMap.add(R.id.button_info);
        buttonRemoteMap.add(R.id.button_last);
        buttonRemoteMap.add(R.id.button_left);
        buttonRemoteMap.add(R.id.button_right);
        buttonRemoteMap.add(R.id.button_up);
        buttonRemoteMap.add(R.id.button_menu);
        buttonRemoteMap.add(R.id.button_mute);
        buttonRemoteMap.add(R.id.button_num0);
        buttonRemoteMap.add(R.id.button_num1);
        buttonRemoteMap.add(R.id.button_num2);
        buttonRemoteMap.add(R.id.button_num3);
        buttonRemoteMap.add(R.id.button_num4);
        buttonRemoteMap.add(R.id.button_num5);
        buttonRemoteMap.add(R.id.button_num6);
        buttonRemoteMap.add(R.id.button_num7);
        buttonRemoteMap.add(R.id.button_num8);
        buttonRemoteMap.add(R.id.button_num9);
        buttonRemoteMap.add(R.id.button_ok);
        buttonRemoteMap.add(R.id.button_pause);
        buttonRemoteMap.add(R.id.button_play);
        buttonRemoteMap.add(R.id.button_pon);
        buttonRemoteMap.add(R.id.button_star);
        buttonRemoteMap.add(R.id.button_stop);
        buttonRemoteMap.add(R.id.button_power);
        buttonRemoteMap.add(R.id.button_record);
        buttonRemoteMap.add(R.id.button_rew);
        buttonRemoteMap.add(R.id.button_voldown);
        buttonRemoteMap.add(R.id.button_volup);
        buttonRemoteMap.add(R.id.button_mute);
        buttonRemoteMap.add(R.id.button_replay);
        buttonRemoteMap.add(R.id.button_skipforward);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList(getString(R.string.datasave_ipdatatag),new ArrayList<String>(ipAdded));
        savedInstanceState.putStringArrayList(getString(R.string.datasave_ipselectedtag), new ArrayList<String>(ipSelected));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ipSaved = savedInstanceState.getStringArrayList(getString(R.string.datasave_ipdatatag));
        ipSelected = savedInstanceState.getStringArrayList(getString(R.string.datasave_ipselectedtag));
        newStartApp = false;
    }

}
