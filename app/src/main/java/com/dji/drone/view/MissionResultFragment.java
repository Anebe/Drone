package com.dji.drone.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleOwnerKt;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dji.drone.databinding.FragmentMissionResultBinding;
import com.dji.drone.databinding.ItemCrackBinding;
import com.dji.drone.model.Detection;
import com.dji.drone.model.ImageSender;
import com.dji.drone.viewModel.MissionViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MissionResultFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private MissionViewModel missionViewModel;
    private FragmentMissionResultBinding binding;
    private MenuCrackAdapter menuCrackAdapter;
    private List<Bitmap> crackImages;
    private int[][] crack = {{1,0},{0,1}};

    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMissionResultBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initData();
        initListener();
        return view;
    }

    private void initData() {
        missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);
        crackImages = new ArrayList<>();
        menuCrackAdapter = new MenuCrackAdapter(crackImages, crack);
        binding.recyclerViewCrack.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCrack.setHasFixedSize(true);
        binding.recyclerViewCrack.setAdapter(menuCrackAdapter);
    }

    private void initListener() {
        String[] filePath = {//"/storage/emulated/0/Pictures/Screenshots/garagem.JPG",
                "/storage/emulated/0/Pictures/Screenshots/ifmaparede.JPG",
                "/storage/emulated/0/Pictures/Screenshots/otaota.JPG",
                "/storage/emulated/0/Pictures/Screenshots/paraderachada.jpg",
                "/storage/emulated/0/Pictures/Screenshots/otaparedeifma.JPG"};
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap imagem = BitmapFactory.decodeFile(filePath[0], options);
        crackImages.add(imagem);

        final Detection[] teste = new Detection[1];
        //binding.btnApi.setOnClickListener(view ->{
        //    executor.execute(new Runnable() {
        //        @Override
        //        public void run() {
        //            Log.d(TAG, Objects.requireNonNull(ImageSender.scanImage(crackImages.get(0))).toString());
        //        }
        //    });
//
        //    //menuCrackAdapter.notifyDataSetChanged();
        //});

    }

    public static class MenuCrackAdapter extends RecyclerView.Adapter<MissionResultFragment.MenuCrackAdapter.ViewHolder> {
        private final String TAG = getClass().getSimpleName();

        private final List<Bitmap> crackClassificationList;
        private ItemCrackBinding binding;
        private int[][] crack;

        public MenuCrackAdapter(@NonNull List<Bitmap> crackClassificationList, int[][] crack) {
            this.crackClassificationList = crackClassificationList;
            this.crack = crack;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            binding = ItemCrackBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        public void setCrack(int[][] crack){
            this.crack = crack;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Bitmap image = crackClassificationList.get(position);
            holder.binding.imgCrack.setImageBitmap(image);
            //int[][] teste = {{1,1,1},{1,0,0},{1,1,0}};
            //holder.binding.viewBox.setBox(teste);
            //int h = holder.binding.imgCrack.getHeight();
            //int w = holder.binding.imgCrack.getWidth();
            //holder.binding.viewBox.setWH(w, h);
            Log.d(TAG, "box: " + Arrays.deepToString(crack));
            holder.binding.viewBox.setBox(crack);
        }
        @Override
        public int getItemCount() {
            return crackClassificationList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            private final ItemCrackBinding binding;

            public ViewHolder(@NonNull ItemCrackBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }
        }
    }

}