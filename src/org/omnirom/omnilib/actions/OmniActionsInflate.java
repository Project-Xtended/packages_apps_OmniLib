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
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class OmniActionsInflate {
    public static ArrayList<OmniAction> inflate(Context context, int xmlFileResId) throws Exception {
        int token;
        ArrayList<OmniAction> actions = new ArrayList<OmniAction>();
        XmlResourceParser parser = context.getResources().getXml(xmlFileResId);

        while ((token = parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (token == XmlPullParser.START_TAG) {
                if ("action".equals(parser.getName())) {
                    actions.add(new OmniAction(context, parser));
                }
            }
        }
        return actions;
    }
}
