package com.faendir.lightning_launcher.multitool.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.preference.MultiSelectListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.faendir.lightning_launcher.multitool.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * @author F43nd1r
 * @since 08.11.2016
 */

public class PlayersPreference extends MultiSelectListPreference implements SummaryPreference {
    public PlayersPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        PackageManager pm = context.getPackageManager();
        SortedMap<String, String> map = StreamSupport.stream(pm.queryBroadcastReceivers(new Intent(Intent.ACTION_MEDIA_BUTTON), 0))
                .map(info -> info.activityInfo).collect(Collectors.<ActivityInfo, String, String, SortedMap<String, String>>toMap(
                        info -> info.applicationInfo.loadLabel(pm).toString(), info -> info.packageName, (k1, k2) -> k1, TreeMap::new));
        setEntries(map.keySet().toArray(new String[0]));
        setEntryValues(map.values().toArray(new String[0]));
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PlayersPreference);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.PlayersPreference_checked_by_default:
                    if (a.getBoolean(attr, false)) {
                        setDefaultValue(new HashSet<>(map.values()));
                    } else {
                        setDefaultValue(new HashSet<>());
                    }
                    break;
            }
        }
        a.recycle();
    }

    private List<CharSequence> getSelectedEntries() {
        List<CharSequence> entries = Arrays.asList(getEntries());
        List<CharSequence> values = Arrays.asList(getEntryValues());
        return StreamSupport.stream(getValues()).map(values::indexOf).filter(index -> index >= 0)
                .map(entries::get).sorted().collect(Collectors.toList());
    }

    @Override
    public CharSequence getSummaryText() {
        return TextUtils.join(", ", getSelectedEntries());
    }
}
