package com.faendir.lightning_launcher.multitool.music;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.faendir.lightning_launcher.multitool.MultiTool;
import com.faendir.lightning_launcher.multitool.R;
import com.faendir.lightning_launcher.multitool.event.ClickEvent;
import com.faendir.lightning_launcher.multitool.util.LambdaUtils.ExceptionalFunction;
import com.faendir.lightning_launcher.scriptlib.ScriptManager;
import com.faendir.lightning_launcher.scriptlib.executor.DirectScriptExecutor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.faendir.lightning_launcher.multitool.util.LambdaUtils.exceptionToOptional;

/**
 * Created on 03.07.2016.
 *
 * @author F43nd1r
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MusicFragment extends Fragment {
    private ImageView albumArt;
    private TextView title;
    private TextView album;
    private TextView artist;
    private ImageView player;
    private volatile Bitmap bitmap;
    private PackageManager pm;
    private MusicListener musicListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        pm = getActivity().getPackageManager();
        musicListener = new MusicListener(new Handler(), getActivity(), info -> {
            synchronized (MusicFragment.this) {
                if (info.getAlbumArt() == null) {
                    bitmap = null;
                } else if (!info.getAlbumArt().isRecycled()) {
                    bitmap = info.getAlbumArt().copy(Bitmap.Config.ARGB_8888, false);
                }
            }
            getActivity().runOnUiThread(() -> {
                synchronized (MusicFragment.this) {
                    albumArt.setImageBitmap(bitmap);
                }
                title.setText(info.getTitle());
                album.setText(info.getAlbum());
                artist.setText(info.getArtist());
                player.setImageDrawable(exceptionToOptional((ExceptionalFunction<String, Drawable, PackageManager.NameNotFoundException>) pm::getApplicationIcon).apply(info.getPackageName()).orElse(null));
            });
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_music, container, false);
        albumArt = v.findViewById(R.id.image_album);
        title = v.findViewById(R.id.text_title);
        album = v.findViewById(R.id.text_album);
        artist = v.findViewById(R.id.text_artist);
        player = v.findViewById(R.id.image_player);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_music_widget, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_help)
                        .setMessage(R.string.message_helpMusic)
                        .setPositiveButton(R.string.button_ok, null)
                        .show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        musicListener.register();
        musicListener.onChange(false);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        musicListener.unregister();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onClick(ClickEvent event) {
        switch (event.getId()) {
            case R.id.button_updateMusic:
                ScriptManager manager = new ScriptManager(getActivity());
                if (MultiTool.DEBUG) manager.enableDebug();
                manager.getAsyncExecutorService()
                        .add(new DirectScriptExecutor(R.raw.music_update))
                        .start();
                break;
            case R.id.button_play:
                musicListener.sendPlayPause();
                break;
            case R.id.button_next:
                musicListener.sendNext();
                break;
            case R.id.button_prev:
                musicListener.sendPrevious();
                break;
        }
    }
}
