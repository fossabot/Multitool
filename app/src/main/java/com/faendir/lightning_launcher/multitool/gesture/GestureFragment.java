package com.faendir.lightning_launcher.multitool.gesture;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faendir.lightning_launcher.multitool.R;
import com.faendir.lightning_launcher.multitool.fastadapter.ExpandableItem;
import com.faendir.lightning_launcher.multitool.util.FileManager;
import com.faendir.lightning_launcher.multitool.util.FileManagerFactory;
import com.faendir.lightning_launcher.multitool.util.Utils;
import com.mikepenz.fastadapter.commons.adapters.GenericFastItemAdapter;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.FileNotFoundException;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import static com.faendir.lightning_launcher.multitool.util.LambdaUtils.exceptionToOptional;

/**
 * A simple {@link Fragment} subclass.
 */
public class GestureFragment extends Fragment {

    private static final int ADD = 1;
    private static final int EDIT = 2;
    private static final int EXPORT = 3;
    private static final int IMPORT = 4;
    private static final String INDEX = "index";

    private FileManager<GestureInfo, FileNotFoundException> fileManager;
    private GenericFastItemAdapter<GestureInfo, ExpandableItem<GestureInfo>> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fileManager = FileManagerFactory.createGestureFileManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(getActivity());
        RecyclerView recyclerView = new RecyclerView(getActivity());
        adapter = new GenericFastItemAdapter<>(ExpandableItem::new);
        adapter.withOnLongClickListener((v, adapter, item, position) -> {
            Intent intent = new Intent(getActivity(), GestureActivity.class);
            intent.putExtra(GestureActivity.GESTURE, item.getModel());
            intent.putExtra(INDEX, adapter.getAdapterPosition(item));
            startActivityForResult(intent, EDIT);
            return true;
        });
        exceptionToOptional(fileManager::read).get().ifPresent(list -> adapter.setModel(StreamSupport.stream(list)
                .filter(gestureInfo -> !gestureInfo.isInvalid()).collect(Collectors.toList())));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        new ItemTouchHelper(new SimpleSwipeCallback((position, direction) -> {
            final ExpandableItem<GestureInfo> item = adapter.getItem(position);
            final Runnable removeRunnable = () -> {
                item.setSwipedAction(null);
                int position1 = adapter.getPosition(item);
                if (position1 != RecyclerView.NO_POSITION) {
                    adapter.getGenericItemAdapter().remove(position1);
                }
                GestureUtils.delete(getActivity(), item.getModel(),
                        adapter.getModels(), fileManager);

            };
            recyclerView.postDelayed(removeRunnable, 5000);

            item.setSwipedAction(() -> {
                recyclerView.removeCallbacks(removeRunnable);
                item.setSwipedAction(null);
                int position2 = adapter.getPosition(item);
                if (position2 != RecyclerView.NO_POSITION) {
                    adapter.notifyItemChanged(position2);
                }
            });

            adapter.notifyItemChanged(position);
        }, null, ItemTouchHelper.RIGHT).withLeaveBehindSwipeRight(getResources().getDrawable(R.drawable.ic_delete_white)).withBackgroundSwipeRight(Color.RED))
                .attachToRecyclerView(recyclerView);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView empty = (TextView) inflater.inflate(R.layout.textview_empty_gestures_list, recyclerView, false);
        layout.addView(recyclerView);
        layout.addView(empty);
        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_gesture, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_gesture:
                startActivityForResult(new Intent(getActivity(), GestureActivity.class), ADD);
                break;
            case R.id.action_help:
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_help)
                        .setMessage(R.string.message_helpGesture)
                        .setPositiveButton(R.string.button_ok, null)
                        .show();
                break;
            case R.id.action_export: {
                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                startActivityForResult(intent, EXPORT);
                break;
            }
            case R.id.action_import: {
                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                startActivityForResult(intent, IMPORT);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ADD: {
                    GestureInfo gestureInfo = data.getParcelableExtra(GestureActivity.GESTURE);
                    adapter.addModel(gestureInfo);
                    GestureUtils.updateSavedGestures(adapter.getModels(), fileManager);
                    break;
                }
                case EDIT: {
                    GestureInfo gestureInfo = data.getParcelableExtra(GestureActivity.GESTURE);
                    int position = data.getIntExtra(INDEX, -1);
                    if (position >= 0) {
                        adapter.getGenericItemAdapter().setModel(position, gestureInfo);
                        GestureUtils.updateSavedGestures(adapter.getModels(), fileManager);
                    }
                }
                case EXPORT:
                    StreamSupport.stream(Utils.getFilePickerActivityResult(data)).findAny()
                            .ifPresent(uri -> GestureUtils.exportGestures(getActivity(), uri));
                    break;
                case IMPORT:
                    StreamSupport.stream(Utils.getFilePickerActivityResult(data)).findAny()
                            .ifPresent(uri -> GestureUtils.importGestures(getActivity(), uri, adapter.getModels(), fileManager));
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
