package com.dji.drone.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dji.drone.R;
import com.dji.drone.databinding.FragmentMenuBinding;
import com.dji.drone.databinding.RecyclerViewMissionItensBinding;
import com.dji.drone.model.ImageSenderThread;
import com.dji.drone.model.room.MissionEntity;
import com.dji.drone.viewModel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private View view;
    private FragmentMenuBinding binding;
    private HomeViewModel homeViewModel;
    private MenuMissionAdapter menuMissionAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        
        initData();
        initListener();
        return view;
    }



    private void initData() {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        menuMissionAdapter = new MenuMissionAdapter(homeViewModel.getAllMission(), clickItemListener);
        binding.recyclerMission.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerMission.setHasFixedSize(true);
        binding.recyclerMission.setAdapter(menuMissionAdapter);
    }

    private void initListener() {
        binding.buttonNew.setOnClickListener(v -> {
            int id = (int) homeViewModel.insertMission("Mission " + menuMissionAdapter.getItemCount());
            MenuFragmentDirections.MenuToMission action = MenuFragmentDirections.menuToMission();
            action.setMissionId(id);
            Navigation.findNavController(view).navigate(action);
        });

        binding.button2.setOnClickListener(v ->{

            int vectorResId = R.drawable.ic_round_add_circle_24;
            Drawable vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId);
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap imageBitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(imageBitmap);
            vectorDrawable.draw(canvas);
            //return BitmapDescriptorFactory.fromBitmap(bitmap);

            String host = "192.168.0.154";
            int port = 55912;

            if(imageBitmap != null){
                //ImageSenderThread imageSenderThread = new ImageSenderThread(host, port, imageBitmap);
                //imageSenderThread.start();

                //new ImageSenderThread(host, port);
            }
            else{
                Log.d(TAG, "Not Image");
            }

        });
    }

    private final IRecyclerViewClickItemListener clickItemListener = new IRecyclerViewClickItemListener() {
        @Override
        public void onItemClick(int id) {
            MenuFragmentDirections.MenuToMission action = MenuFragmentDirections.menuToMission();
            action.setMissionId(id);
            Navigation.findNavController(view).navigate(action);
        }
    };

    public static class MenuMissionAdapter extends RecyclerView.Adapter<MenuMissionAdapter.ViewHolder> {
        private List<MissionEntity> missionList  = new ArrayList<>();
        private final IRecyclerViewClickItemListener onItemClickListener;
        private RecyclerViewMissionItensBinding binding;

        public MenuMissionAdapter(List<MissionEntity> missionList, IRecyclerViewClickItemListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            if(missionList != null){
                this.missionList = missionList;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            binding = RecyclerViewMissionItensBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.binding.textViewName.setText(missionList.get(position).getName());
            holder.itemView.setOnClickListener(v -> {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(missionList.get(holder.getAdapterPosition()).getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return missionList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            private final RecyclerViewMissionItensBinding binding;

            public ViewHolder(@NonNull RecyclerViewMissionItensBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }
        }

    }

}