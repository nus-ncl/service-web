package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstanceInfo {
    String instanceName ="";
    String status ="";
    String network="";
    String image="";
    String flavor="";

    public InstanceInfo(){}

    public InstanceInfo(String instanceName,String status,String network,String image,String flavor){
        this.instanceName=instanceName;
        this.status=status;
        this.network=network;
        this.image=image;
        this.flavor=flavor;
    }
}
