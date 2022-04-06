package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class OpenStackServerStateful {
    String teamName ="";
    String expName ="";
    String status="";
    List<InstanceInfo> instanceInfo = new ArrayList<InstanceInfo>();

    public OpenStackServerStateful(){}
    public OpenStackServerStateful(String teamName,String expName,String status,List<InstanceInfo> lstInfo){
        this.teamName = teamName;
        this.expName = expName;
        this.status = status;
        this.instanceInfo = lstInfo;
    }
}
