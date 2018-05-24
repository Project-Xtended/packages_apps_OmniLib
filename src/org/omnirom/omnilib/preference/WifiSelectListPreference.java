/*
 *  Copyright (C) 2016 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.omnirom.omnilib.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.net.wifi.WifiManager;
import android.icu.text.Collator;

import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiSavedConfigUtils;

import org.omnirom.omnilib.R;
import com.android.settingslib.CustomDialogPreference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;

public class WifiSelectListPreference extends CustomDialogPreference {
    private List<AccessPoint> mAPInfoList;
    private ActionListAdapter mAdapter;
    private List<String> mValues = new ArrayList<String>();
    private WifiManager mWifiManager;
    private static final Comparator<AccessPoint> SAVED_NETWORK_COMPARATOR =
            new Comparator<AccessPoint>() {
                final Collator mCollator = Collator.getInstance();

                @Override
                public int compare(AccessPoint ap1, AccessPoint ap2) {
                    return mCollator.compare(
                            nullToEmpty(ap1.getSsidStr()), nullToEmpty(ap2.getSsidStr()));
                }

                private String nullToEmpty(String string) {
                    return (string == null) ? "" : string;
                }
            };

    public WifiSelectListPreference(Context context) {
        this(context, null);
    }

    public WifiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_wifi_ap_list);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mAPInfoList = WifiSavedConfigUtils.getAllConfigs(context, mWifiManager);
        Collections.sort(mAPInfoList, SAVED_NETWORK_COMPARATOR);

        setPositiveButtonText(R.string.action_save);
        setNegativeButtonText(android.R.string.cancel);
    }

    public void setValues(Collection<String> values) {
        mValues.clear();
        mValues.addAll(values);
    }

    public Collection<String> getValues() {
        return mValues;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mAdapter = new ActionListAdapter(getContext());
        final ListView listView = (ListView) view.findViewById(R.id.wifi_ap_list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AppViewHolder holder = (AppViewHolder) view.getTag();
                final boolean isChecked = !holder.checkBox.isChecked();

                holder.checkBox.setChecked(isChecked);
                AccessPoint info = mAdapter.getItem(position);

                if (isChecked) {
                    mValues.add(info.getSsidStr());
                } else {
                    mValues.remove(info.getSsidStr());
                }
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            callChangeListener(mValues.size() > 0 ? mValues : null);
        }
    }

    public class ActionListAdapter extends ArrayAdapter<AccessPoint> {
        private final LayoutInflater mInflater;

        public ActionListAdapter(Context context) {
            super(context, 0);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            addAll(mAPInfoList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppViewHolder holder = AppViewHolder.createOrRecycle(mInflater, convertView);
            convertView = holder.rootView;
            AccessPoint info = getItem(position);
            holder.title.setText(info.getSsidStr());
            holder.checkBox.setChecked(mValues.contains(info.getSsidStr()));
            return convertView;
        }

        @Override
        public AccessPoint getItem(int position) {
            return mAPInfoList.get(position);
        }
    }

    public static class AppViewHolder {
        public View rootView;
        public TextView title;
        public CheckBox checkBox;

        public static AppViewHolder createOrRecycle(LayoutInflater inflater, View convertView) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.wifi_ap_item, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                AppViewHolder holder = new AppViewHolder();
                holder.rootView = convertView;
                holder.title = (TextView) convertView.findViewById(R.id.ap_name);
                holder.checkBox = (CheckBox) convertView.findViewById(android.R.id.checkbox);
                convertView.setTag(holder);
                return holder;
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                return (AppViewHolder) convertView.getTag();
            }
        }
    }
}

