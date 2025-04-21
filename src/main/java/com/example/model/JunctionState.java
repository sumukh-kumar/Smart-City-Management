package com.example.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class JunctionState {
    // private int id; // Removed id field
    private String junctionId;
    private int lane1Vehicles;
    private int lane2Vehicles;
    private int lane3Vehicles;
    private int lane4Vehicles;
    private int greenLaneId;
    private LocalDateTime lastUpdated;

    // Constructor - Removed id parameter
    public JunctionState(String junctionId, int lane1Vehicles, int lane2Vehicles, int lane3Vehicles, int lane4Vehicles, int greenLaneId, Timestamp lastUpdatedTimestamp) {
        // this.id = id; // Removed assignment
        this.junctionId = junctionId;
        this.lane1Vehicles = lane1Vehicles;
        this.lane2Vehicles = lane2Vehicles;
        this.lane3Vehicles = lane3Vehicles;
        this.lane4Vehicles = lane4Vehicles;
        this.greenLaneId = greenLaneId;
        this.lastUpdated = (lastUpdatedTimestamp != null) ? lastUpdatedTimestamp.toLocalDateTime() : null;
    }

    // Getters - Removed getId()
    // public int getId() { return id; }
    public String getJunctionId() { return junctionId; }
    public int getLane1Vehicles() { return lane1Vehicles; }
    public int getLane2Vehicles() { return lane2Vehicles; }
    public int getLane3Vehicles() { return lane3Vehicles; }
    public int getLane4Vehicles() { return lane4Vehicles; }
    public int getGreenLaneId() { return greenLaneId; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    @Override
    public String toString() {
        return "JunctionState{" +
               // "id=" + id + // Removed id from toString
               "junctionId='" + junctionId + '\'' +
               ", lane1Vehicles=" + lane1Vehicles +
               ", lane2Vehicles=" + lane2Vehicles +
               ", lane3Vehicles=" + lane3Vehicles +
               ", lane4Vehicles=" + lane4Vehicles +
               ", greenLaneId=" + greenLaneId +
               ", lastUpdated=" + lastUpdated +
               '}';
    }
}