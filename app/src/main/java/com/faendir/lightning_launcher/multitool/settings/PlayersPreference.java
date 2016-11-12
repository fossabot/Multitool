package com.faendir.lightning_launcher.multitool.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.preference.MultiSelectListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.faendir.lightning_launcher.multitool.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Lukas on 08.11.2016.
 */

public class PlayersPreference extends MultiSelectListPreference implements SummaryPreference {
    public PlayersPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryBroadcastReceivers(new Intent(Intent.ACTION_MEDIA_BUTTON), 0);
        SortedMap<String, String> map = new TreeMap<>();
        for (ResolveInfo info : infos) {
            ApplicationInfo applicationInfo = info.activityInfo.applicationInfo;
            map.put(applicationInfo.loadLabel(pm).toString(), applicationInfo.packageName);
        }
        setEntries(map.keySet().toArray(new String[0]));
        setEntryValues(map.values().toArray(new String[0]));
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PlayersPreference);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.PlayersPreference_checkedByDefault:
                    if(a.getBoolean(attr, false)){
                        setDefaultValue(new HashSet<>(map.values()));
                    } else {
                        setDefaultValue(new HashSet<>());
                    }
                    break;
            }
        }
        a.recycle();
    }

    private List<String> getSelectedEntries() {
        List<String> selected = new ArrayList<>();
        CharSequence[] entries = getEntries();
        List<CharSequence> values = Arrays.asList(getEntryValues());
        for (String s : getValues()) {
            selected.add(entries[values.indexOf(s)].toString());
        }
        Collections.sort(selected);
        return selected;
    }

    @Override
    public CharSequence getSummaryText() {
        return TextUtils.join(", ", getSelectedEntries());
    }
}
