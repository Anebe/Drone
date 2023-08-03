package com.dji.drone.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dji.drone.databinding.FragmentResultMissionBinding;
import com.dji.drone.databinding.RecyclerViewCrackItensBinding;
import com.dji.drone.model.DetectionResult;
import com.dji.drone.model.ImageSenderThread;
import com.dji.drone.viewModel.MissionViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ResultMissionFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private MissionViewModel missionViewModel;
    private FragmentResultMissionBinding binding;
    private MenuCrackAdapter menuCrackAdapter;
    private List<Bitmap> crackImages;
    private  List<DetectionResult> detectionResults;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResultMissionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initData();
        initListener();
        return view;
    }

    private void initData() {
        missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);
        //menuCrackAdapter = new MenuCrackAdapter(missionViewModel.getCrackImage());
        crackImages = new ArrayList<>();
        detectionResults = new ArrayList<>();
        menuCrackAdapter = new MenuCrackAdapter(crackImages, detectionResults);
        binding.recyclerViewCrack.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCrack.setHasFixedSize(true);
        binding.recyclerViewCrack.setAdapter(menuCrackAdapter);
    }

    private void initListener() {
        String host = "192.168.0.154";
        int port = 55912;

        /*
        binding.bntGetImg.setOnClickListener(v ->{
            crackImages = missionViewModel.getCrackImages();


            ImageSenderThread imageSenderThread = new ImageSenderThread(host, port);
            imageSenderThread.openConection();
            for (int i = 0; i < crackImages.size(); i++) {
                imageSenderThread.sendImage(i, crackImages.get(i));
                detectionResults.add(imageSenderThread.getResult());
            }
            imageSenderThread.closeConnection();
            menuCrackAdapter.notifyItemRangeChanged(0, crackImages.size());
        });
        */
        binding.btnTeste.setOnClickListener(v ->{
            String[] filePath = {//"/storage/emulated/0/Pictures/Screenshots/garagem.JPG",
                    "/storage/emulated/0/Pictures/Screenshots/ifmaparede.JPG",
                    "/storage/emulated/0/Pictures/Screenshots/otaota.JPG",
                    "/storage/emulated/0/Pictures/Screenshots/paraderachada.jpg",
                    "/storage/emulated/0/Pictures/Screenshots/otaparedeifma.JPG"};

            ExecutorService executor = Executors.newSingleThreadExecutor();

            for (int i = 0; i < 2; i++) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap imagem = BitmapFactory.decodeFile(filePath[i], options);

                crackImages.add(imagem);
                Future<DetectionResult> future = executor.submit(new ImageSenderThread(host, port, i, imagem));

                try {
                    DetectionResult resultado = future.get();
                    if(resultado != null){
                        detectionResults.add(resultado);
                    }
                    System.out.println("Resultado: " + resultado);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //new ImageSenderThread(host, port, i, imagem).start();
                //detectionResults.add(imageSenderThread.getResult());
            }
            //imageSenderThread.closeConnection();
            Log.d(TAG, "size: " + crackImages.size());
            menuCrackAdapter.notifyDataSetChanged();
        });
    }

    public static class MenuCrackAdapter extends RecyclerView.Adapter<ResultMissionFragment.MenuCrackAdapter.ViewHolder> {
        private final List<Bitmap> crackClassificationList;
        private final List<DetectionResult> detectionResultList;
        private RecyclerViewCrackItensBinding binding;

        public MenuCrackAdapter(@NonNull List<Bitmap> crackClassificationList, @NonNull List<DetectionResult> detectionResultList) {
            this.crackClassificationList = crackClassificationList;
            this.detectionResultList = detectionResultList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            binding = RecyclerViewCrackItensBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Bitmap image = crackClassificationList.get(position);
            holder.binding.imgCrack.setImageBitmap(image);

            //int w = holder.binding.imgCrack.getWidth();
            //int h = holder.binding.imgCrack.getHeight();


            for (int i = 0; i < detectionResultList.size(); i++) {
                if(detectionResultList.get(i).id == position){
                    holder.binding.viewBox.setBox(detectionResultList.get(i).detection);
                    break;
                }
            }
            holder.binding.viewBox.setVisibility(View.INVISIBLE);

            holder.binding.chkShowBox.setOnClickListener(v -> {
                Log.d("MenuCrackAdapter", "hg: "+ holder.binding.viewBox.getHeight() + " wg: " + holder.binding.viewBox.getWidth());
                if(holder.binding.chkShowBox.isChecked()){
                    //holder.binding.viewBox.setShowBox(true);
                    int w = holder.binding.imgCrack.getWidth();
                    int h  = holder.binding.imgCrack.getHeight();

                    holder.binding.viewBox.setHeightWidth(w, h);
                    holder.binding.viewBox.setVisibility(View.VISIBLE);
                }else{
                    holder.binding.viewBox.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return crackClassificationList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            private final RecyclerViewCrackItensBinding binding;

            public ViewHolder(@NonNull RecyclerViewCrackItensBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }
        }
    }

}