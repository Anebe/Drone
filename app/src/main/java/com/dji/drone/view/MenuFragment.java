package com.dji.drone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dji.drone.R;
import com.dji.drone.model.room.MissionEntity;
import com.dji.drone.viewModel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    private String TAG = getClass().getSimpleName();

    private View view;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private MenuMissionAdapter menuMissionAdapter;
    private Button btn_new;
    //---------
    private Button btn_teste;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        initUI();
        initData();
        initListener();
        return view;
    }


    private void initUI() {
        recyclerView = view.findViewById(R.id.testeRecyclerMission);
        btn_new = view.findViewById(R.id.buttonNew);

        btn_teste = view.findViewById(R.id.btn_teste);
    }

    private void initData() {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        menuMissionAdapter = new MenuMissionAdapter(homeViewModel.getAllMission(), clickItemListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(menuMissionAdapter);
    }

    private void initListener() {
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) homeViewModel.insertMission("Mission " + menuMissionAdapter.getItemCount());
                MenuFragmentDirections.MenuToMission action = MenuFragmentDirections.menuToMission();
                action.setMissionId(id);
                Navigation.findNavController(view).navigate(action);
            }
        });
        btn_teste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections directions = MenuFragmentDirections.menuToTeste();
                Navigation.findNavController(view).navigate(directions);
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
        private final String TAG = getClass().getSimpleName();
        private List<MissionEntity> missionList  = new ArrayList<>();
        private final IRecyclerViewClickItemListener onItemClickListener;

        public MenuMissionAdapter(List<MissionEntity> missionList, IRecyclerViewClickItemListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            if(missionList != null){
                this.missionList = missionList;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_mission_itens, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.nameTextView.setText(missionList.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(missionList.get(holder.getAdapterPosition()).getId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return missionList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView nameTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                nameTextView = itemView.findViewById(R.id.textViewName);
            }
        }

    }

}