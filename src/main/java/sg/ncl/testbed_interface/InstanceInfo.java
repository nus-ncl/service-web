package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstanceInfo {
    String instance_name ="";
    String status ="";
    String network="";
    String image="";
    String flavor="";

    public InstanceInfo(){}

    public InstanceInfo(String instance_name,String status,String network,String image,String flavor){
        this.instance_name=instance_name;
        this.status=status;
        this.network=network;
        this.image=image;
        this.flavor=flavor;
    }
}
