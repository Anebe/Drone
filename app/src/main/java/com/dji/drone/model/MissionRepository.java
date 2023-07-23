package com.dji.drone.model;

import android.app.Application;

import com.dji.drone.model.room.MissionDAO;
import com.dji.drone.model.room.MissionDatabase;
import com.dji.drone.model.room.MissionEntity;
import com.dji.drone.model.room.Point2D;
import com.dji.drone.model.room.Point2dDAO;
import com.dji.drone.model.room.WaypointActionDAO;
import com.dji.drone.model.room.WaypointDAO;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MissionRepository {
    private final String TAG = getClass().getSimpleName();

    private final MissionDAO missionDAO;
    private final WaypointDAO waypointDAO;
    private final WaypointActionDAO waypointActionDAO;
    private final Point2dDAO point2dDAO;

    public MissionRepository(Application application) {
        MissionDatabase missionDatabase = MissionDatabase.getInstance(application);
        missionDAO = missionDatabase.getMissionDAO();
        waypointDAO = missionDatabase.getWaypointDAO();
        waypointActionDAO = missionDatabase.getWaypointActionDAO();
        point2dDAO = missionDatabase.getPoint2dDAO();

    }

    public Long insertMission(MissionEntity mission){
        return (Long) execute((Callable<Long>) () -> missionDAO.insert(mission));
    }

    public MissionEntity getMission(int id){
        return (MissionEntity) execute((Callable<MissionEntity>) () -> missionDAO.getMission(id));
    }

    public List<MissionEntity> getAllMissions(){
        return (List<MissionEntity>) execute((Callable<List<MissionEntity>>) missionDAO::getAll);
    }


    public Boolean insertUpdatePoint2d(int missionId, List<Point2D> points){
        return (Boolean) execute((Callable<Boolean>) () -> {
            boolean result = true;
            List<Point2D> point2DList = missionDAO.getAllPoint2d(missionId);
            if(point2DList.size() > 0){
                for (Point2D item: point2DList) {
                    point2dDAO.delete(item);
                }
            }
            for (Point2D item : points) {
                item.setMission_id(missionId);
                result = point2dDAO.insert(item) > 0;
            }
            return result;
        });
    }

    public List<Point2D> getPoint2dOfMission(int missionId){
        return (List<Point2D>) execute((Callable<List<Point2D>>) () -> missionDAO.getAllPoint2d(missionId));
    }

    public int updateMission(MissionEntity actualMission) {
        Integer result = (Integer)  execute((Callable<Integer>) () -> missionDAO.update(actualMission));
        return (result != null) ? result : -1;
    }

    private Object execute(Callable<?> callable){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(callable);

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        return null;
    }


}
