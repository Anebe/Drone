package com.dji.drone.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dji.drone.databinding.FragmentMediaManagerBinding;
import com.dji.drone.databinding.MediaInfoItemBinding;

import java.util.ArrayList;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJICameraError;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.media.FetchMediaTask;
import dji.sdk.media.FetchMediaTaskContent;
import dji.sdk.media.FetchMediaTaskScheduler;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.DJISDKManager;

public class MediaManagerFragment extends Fragment{

    private static final String TAG = MediaManagerFragment.class.getSimpleName();

    private FragmentMediaManagerBinding binding;

    private FileListAdapter mListAdapter;
    private final List<MediaFile> mediaFileList = new ArrayList<>();
    private MediaManager mMediaManager;
    private MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;
    private FetchMediaTaskScheduler scheduler;
    private SettingsDefinitions.StorageLocation storageLocation;
    private int lastClickViewIndex =-1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initUI();
        initListener();

        getAircraftInstance().getCamera().setStorageStateCallBack(storageState -> {
            if(storageState.isInserted()) {
                storageLocation = SettingsDefinitions.StorageLocation.SDCARD;
                getAircraftInstance().getCamera().setStorageLocation(SettingsDefinitions.StorageLocation.SDCARD, djiError -> {
                });
            } else {
                storageLocation = SettingsDefinitions.StorageLocation.INTERNAL_STORAGE;
                getAircraftInstance().getCamera().setStorageLocation(SettingsDefinitions.StorageLocation.INTERNAL_STORAGE, djiError -> {
                });
            }
        });
        binding = FragmentMediaManagerBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        initMediaManager();
    }

    @Override
    public void onDestroy() {
        if (mMediaManager != null) {
            mMediaManager.stop(null);
            mMediaManager.removeFileListStateCallback(this.updateFileListStateListener);
            mMediaManager.removeMediaUpdatedVideoPlaybackStateListener(updatedVideoPlaybackStateListener);
            mMediaManager.exitMediaDownloading();
            if (scheduler!=null) {
                scheduler.removeAllTasks();
            }
        }


        mediaFileList.clear();
        super.onDestroy();
    }

    void initUI() {

        //Init FileListAdapter

        mListAdapter = new FileListAdapter(mediaFileList);
        binding.filelistView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.filelistView.setHasFixedSize(true);
        binding.filelistView.setAdapter(mListAdapter);
    }

    private void initListener(){
        binding.deleteBtn.setOnClickListener(v -> deleteFileByIndex(lastClickViewIndex));
        binding.reloadBtn.setOnClickListener(v -> getFileList());
    }


    private void getFileList() {
        mMediaManager = getCameraInstance().getMediaManager();
        if (mMediaManager != null) {

            if ((currentFileListState == MediaManager.FileListState.SYNCING) || (currentFileListState == MediaManager.FileListState.DELETING)){
                DJILog.e(TAG, "Media Manager is busy.");
            }
            else{
                mMediaManager.refreshFileListOfStorageLocation(storageLocation, djiError -> {
                    if (null == djiError) {

                        List<MediaFile> tempList;
                        if (storageLocation == SettingsDefinitions.StorageLocation.SDCARD) {
                            tempList = mMediaManager.getSDCardFileListSnapshot();
                        } else {
                            tempList = mMediaManager.getInternalStorageFileListSnapshot();
                        }
                        if(tempList != null){
                            mediaFileList.clear();
                            mediaFileList.addAll(tempList);
                        }

                        //if (mediaFileList != null) {
                        //    Collections.sort(mediaFileList, (lhs, rhs) -> {
                        //        if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                        //            return 1;
                        //        } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                        //            return -1;
                        //        }
                        //        return 0;
                        //    });
                        //}
                        scheduler.resume(error -> {
                            if (error == null) {
                                getThumbnails();
                            }
                        });
                    } else {
                        Log.d(TAG, "Get Media File List Failed:" + djiError.getDescription());
                    }
                });
            }
        }
    }


    private void deleteFileByIndex(final int index) {
        ArrayList<MediaFile> fileToDelete = new ArrayList<>();
        if (mediaFileList.size() > index) {
            fileToDelete.add(mediaFileList.get(index));
            mMediaManager.deleteFiles(fileToDelete, new CommonCallbacks.CompletionCallbackWithTwoParam<List<MediaFile>, DJICameraError>() {
                @Override
                public void onSuccess(List<MediaFile> x, DJICameraError y) {
                    DJILog.e(TAG, "Delete file success");

                    MediaFile file = mediaFileList.remove(index);

                    //Reset select view
                    lastClickViewIndex = -1;

                    //Update recyclerView
                    mListAdapter.notifyItemRemoved(index);
                }

                @Override
                public void onFailure(DJIError error) {
                    Log.d(TAG, "Delete file failed");
                }
            });
        }
    }


    private void initMediaManager() {
        if (getProductInstance() == null) {
            mediaFileList.clear();
            mListAdapter.notifyDataSetChanged();
            DJILog.e(TAG, "Product disconnected");
        } else {
            if (null != getCameraInstance() && getCameraInstance().isMediaDownloadModeSupported()) {
                mMediaManager = getCameraInstance().getMediaManager();
                if (null != mMediaManager) {
                    mMediaManager.addUpdateFileListStateListener(this.updateFileListStateListener);
                    mMediaManager.addMediaUpdatedVideoPlaybackStateListener(this.updatedVideoPlaybackStateListener);

                    getCameraInstance().setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, error -> {
                        if (error == null) {
                            DJILog.e(TAG, "Set cameraMode success");

                            getFileList();
                        } else {
                            Log.d(TAG, "Set cameraMode failed");
                        }
                    });

                    if (mMediaManager.isVideoPlaybackSupported()) {
                        DJILog.e(TAG, "Camera support video playback!");
                    } else {
                        Log.d(TAG, "Camera does not support video playback!");
                    }
                    scheduler = mMediaManager.getScheduler();
                }

            } else if (null != getCameraInstance()
                    && !getCameraInstance().isMediaDownloadModeSupported()) {
                Log.d(TAG, "Media Download Mode not Supported");
            }
        }
    }

    //Listeners
    private final MediaManager.FileListStateListener updateFileListStateListener = state -> currentFileListState = state;

    private final MediaManager.VideoPlaybackStateListener updatedVideoPlaybackStateListener =
            videoPlaybackState -> {
            };

    private void getThumbnails() {
        if (mediaFileList.size() <= 0) {
            Log.d(TAG, "No File info for downloading thumbnails");
            return;
        }
        for (int i = 0; i < mediaFileList.size(); i++) {
            getThumbnailByIndex(i);
        }
    }

    private void getThumbnailByIndex(final int index) {
        FetchMediaTask task = new FetchMediaTask(mediaFileList.get(index), FetchMediaTaskContent.THUMBNAIL, (mediaFile, option, error) -> {
            if (null == error) {
                if (option == FetchMediaTaskContent.PREVIEW) {
                    mListAdapter.notifyDataSetChanged();
                }
                if (option == FetchMediaTaskContent.THUMBNAIL) {
                    mListAdapter.notifyDataSetChanged();
                }
            } else {
                DJILog.e(TAG, "Fetch Media Task Failed" + error.getDescription());
            }
        });
        scheduler.moveTaskToEnd(task);
    }

    private class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ItemHolder> {
        private MediaInfoItemBinding binding;
        private List<MediaFile> mediaFileList;

        public FileListAdapter(List<MediaFile> mediaFileList){
            if(mediaFileList != null){
                this.mediaFileList = mediaFileList;
            }
        }
        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            binding = MediaInfoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ItemHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder mItemHolder, final int index) {
            final MediaFile mediaFile = mediaFileList.get(index);

            if (mediaFile != null) {
                if (mediaFile.getMediaType() != MediaFile.MediaType.MOV && mediaFile.getMediaType() != MediaFile.MediaType.MP4) {
                    mItemHolder.binding.filetime.setVisibility(View.GONE);
                } else {
                    mItemHolder.binding.filetime.setVisibility(View.VISIBLE);
                    mItemHolder.binding.filetime.setText(new String(mediaFile.getDurationInSeconds() + " s"));
                }
                mItemHolder.binding.filename.setText(mediaFile.getFileName());
                mItemHolder.binding.filetime.setText(mediaFile.getMediaType().name());
                mItemHolder.binding.fileSize.setText(new String(mediaFile.getFileSize() + " Bytes"));
                mItemHolder.binding.filethumbnail.setImageBitmap(mediaFile.getThumbnail());
                mItemHolder.binding.filethumbnail.setTag(mediaFile);
                mItemHolder.itemView.setTag(index);

                mItemHolder.itemView.setSelected(lastClickViewIndex == index);

            }
        }

        @Override
        public int getItemCount() {
            if (mediaFileList != null) {
                return mediaFileList.size();
            }
            return 0;
        }

        private class ItemHolder extends RecyclerView.ViewHolder {
            private final MediaInfoItemBinding binding;

            public ItemHolder(MediaInfoItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    //------------------------
    private static synchronized BaseProduct getProductInstance() {
        return DJISDKManager.getInstance().getProduct();
    }
    private static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }
    private static synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected()) return null;
        return (Aircraft) getProductInstance();
    }
    private static synchronized Camera getCameraInstance() {

        if (getProductInstance() == null) return null;

        Camera camera = null;

        if (getProductInstance() instanceof Aircraft){
            camera = getProductInstance().getCamera();

        } else if (getProductInstance() instanceof HandHeld) {
            camera = getProductInstance().getCamera();
        }

        return camera;
    }

}
