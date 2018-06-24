/*
 *  Copyright (C) 2014 The OmniROM Project
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
 */

package org.omnirom.omnilib.actions;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class OmniAction {
    private static final String TAG = "OmniAction";

    public String key = null;
    public String title = null;
    public String broadcast = null;
    public String value = null;
    public String value_type = null;
    public String value_key = null;
    public String type = null;
    public String setting = null;
    public String setting_type = null;
    public String runner = null;

    private Context mContext;

    public OmniAction(Context context, AttributeSet attrs) {
        mContext = context;
        parseAttrs(attrs);
    }

    private void parseAttrs(AttributeSet attrs) {
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attr = attrs.getAttributeName(i);
            switch (attr) {
                case "setting":
                    setting = attrs.getAttributeValue(i);
                    break;
                case "setting_type":
                    setting_type = attrs.getAttributeValue(i);
                    break;
                case "broadcast":
                    broadcast = attrs.getAttributeValue(i);
                    break;
                case "key":
                    key = attrs.getAttributeValue(i);
                    break;
                case "value":
                    value = attrs.getAttributeValue(i);
                    break;
                case "value_type":
                    value_type = attrs.getAttributeValue(i);
                    break;
                case "value_key":
                    value_key = attrs.getAttributeValue(i);
                    break;
                case "title":
                    title = mContext.getString(attrs.getAttributeResourceValue(i, 0));
                    break;
                case "runner":
                    runner = attrs.getAttributeValue(i);
                    break;
            }
        }
    }

    public void execute() {
        if (runner != null) {
            executeRunner();
            return;
        }

        if (broadcast != null) {
            executeBroadcast();
            return;
        }

        if (setting != null && setting_type != null) {
            executeSetting();
            return;
        }
    }

    private void executeBroadcast() {
        Intent i = new Intent(broadcast);

        if (value != null) {
            switch (value_type) {
                case "boolean":
                    i.putExtra(value_key, Boolean.parseBoolean(value));
                    break;
                default:
                    i.putExtra(value_key, value);
            }
        }

        mContext.sendBroadcastAsUser(i, UserHandle.CURRENT);
    }

    private void executeSetting() {
        switch (setting_type) {
            case "Secure":
                switch (value_type) {
                    case "int":
                        Settings.Secure.putInt(mContext.getContentResolver(), setting, Integer.parseInt(value));
                        break;
                    default:
                        Settings.Secure.putString(mContext.getContentResolver(), setting, value);
                        break;
                }
                break;
            case "Global":
                switch (value_type) {
                    case "int":
                        Settings.Global.putInt(mContext.getContentResolver(), setting, Integer.parseInt(value));
                        break;
                    default:
                        Settings.Global.putString(mContext.getContentResolver(), setting, value);
                        break;
                }
                break;
        }
    }

    private void executeRunner() {
        try {
            Class mRunner = Class.forName(runner);
            Constructor<?> constructor = mRunner.getDeclaredConstructor(new Class[]{Context.class});
            Object object = constructor.newInstance(mContext);
            Method run = mRunner.getDeclaredMethod("run", new Class[]{String.class});
            run.invoke(object, value);
        } catch (Exception e) {
            Log.e(TAG, "Runner", e);
        }
    }
}
