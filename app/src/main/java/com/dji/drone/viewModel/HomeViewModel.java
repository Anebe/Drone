package com.dji.drone.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.dji.drone.model.MissionRepository;
import com.dji.drone.model.room.MissionEntity;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private MissionRepository missionRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        missionRepository = new MissionRepository(application);
    }


    public long insertMission(String name){
        MissionEntity mission = new MissionEntity();
        mission.setName(name);
        return missionRepository.insertMission(mission);
    }


    public List<MissionEntity> getAllMission(){
        return missionRepository.getAllMissions();
    }

}
