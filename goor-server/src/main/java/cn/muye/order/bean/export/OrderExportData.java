package cn.muye.order.bean.export;

import java.util.List;

/**
 * Created by Selim on 2018/3/12.
 */
public class OrderExportData {

    private List<DestinationAnalysisVO> destinationAnalysisVOList;

    private List<ElevatorUseAnalysisVO> elevatorUseAnalysisVOList;

    private List<TransferTaskAnalysisVO> transferTaskAnalysisVOList;

    public List<ElevatorUseAnalysisVO> getElevatorUseAnalysisVOList() {
        return elevatorUseAnalysisVOList;
    }

    public void setElevatorUseAnalysisVOList(List<ElevatorUseAnalysisVO> elevatorUseAnalysisVOList) {
        this.elevatorUseAnalysisVOList = elevatorUseAnalysisVOList;
    }

    public List<DestinationAnalysisVO> getDestinationAnalysisVOList() {
        return destinationAnalysisVOList;
    }

    public void setDestinationAnalysisVOList(List<DestinationAnalysisVO> destinationAnalysisVOList) {
        this.destinationAnalysisVOList = destinationAnalysisVOList;
    }

    public List<TransferTaskAnalysisVO> getTransferTaskAnalysisVOList() {
        return transferTaskAnalysisVOList;
    }

    public void setTransferTaskAnalysisVOList(List<TransferTaskAnalysisVO> transferTaskAnalysisVOList) {
        this.transferTaskAnalysisVOList = transferTaskAnalysisVOList;
    }
}
