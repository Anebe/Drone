package com.dji.drone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dji.drone.R;
import com.dji.drone.databinding.FragmentMenuBinding;
import com.dji.drone.databinding.RecyclerViewMissionItensBinding;
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