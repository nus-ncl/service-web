package sg.ncl;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

import sg.ncl.testbed_interface.Node;

public class NodeManager {
	
	private static NodeManager NODE_MANAGER_SINGLETON = null;
	private HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>(); /* nodeId - Node */
	private final String DISK_HEALTH_GOOD = "Good";
	private static final Random RANDOM = new SecureRandom();
	
	private NodeManager() {
		Node node1 = new Node();
		node1.setNodeType("Win7-64-aries");
		node1.setIsVirtNode(false);
		node1.setIsSubNode(false);
		node1.setAdminMfsOSID("emulab-ops-FREEBSD");
		node1.setBiosWaitTime("60");
		node1.setNetworkInterface("eth1");
		node1.setDefaultOS("Windows7-64");
		node1.setDiskSize("250.00");
		node1.setCpuFreq("2400");
		node1.setMemory("2048");
		node1.setProcessor("Intel Xeon E3");
		node1.setVirtMachinesPerNode("10");
		node1.setNodeId(generateRandomId());
		node1.setDiskHealth(DISK_HEALTH_GOOD);
		node1.setAllocatedCount(1);
		
		Node node2 = new Node();
		node2.setNodeType("Ubuntu12-32-taurus");
		node2.setIsVirtNode(false);
		node2.setIsSubNode(false);
		node2.setAdminMfsOSID("emulab-ops-FREEBSD");
		node2.setBiosWaitTime("60");
		node2.setNetworkInterface("eth1");
		node2.setDefaultOS("Ubuntu1204-32-STD");
		node2.setDiskSize("250.00");
		node2.setCpuFreq("2400");
		node2.setMemory("2048");
		node2.setProcessor("Intel Xeon E5");
		node2.setVirtMachinesPerNode("25");
		node2.setNodeId(generateRandomId());
		node2.setDiskHealth(DISK_HEALTH_GOOD);
		node2.setAllocatedCount(10);
		
		Node node3 = new Node();
		node3.setNodeType("Ubuntu14-64-gemini");
		node3.setIsVirtNode(false);
		node3.setIsSubNode(false);
		node3.setAdminMfsOSID("emulab-ops-FREEBSD");
		node3.setBiosWaitTime("60");
		node3.setNetworkInterface("eth1");
		node3.setDefaultOS("Ubuntu1404-64-STD");
		node3.setDiskSize("3000.00");
		node3.setCpuFreq("3200");
		node3.setMemory("4096");
		node3.setProcessor("Intel Xeon E7");
		node3.setVirtMachinesPerNode("25");
		node3.setNodeId(generateRandomId());
		node3.setDiskHealth(DISK_HEALTH_GOOD);
		node3.setAllocatedCount(20);
		
		nodeMap.put(node1.getNodeId(), node1);
		nodeMap.put(node2.getNodeId(), node2);
		nodeMap.put(node3.getNodeId(), node3);
	}
	
    public static NodeManager getInstance() {
        if (NODE_MANAGER_SINGLETON == null) {
        	NODE_MANAGER_SINGLETON = new NodeManager();
        }
        return NODE_MANAGER_SINGLETON;
    }

	public HashMap<Integer, Node> getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(HashMap<Integer, Node> nodeMap) {
		this.nodeMap = nodeMap;
	}
	
    public int generateRandomId() {
    	int nodeId = RANDOM.nextInt(Integer.MAX_VALUE) + 1;
    	while (nodeMap.containsKey(nodeId)) {
    		nodeId = RANDOM.nextInt();
    	}
    	return nodeId;
    }
    
    public void addNode(Node node) {
    	int nodeId = generateRandomId();
    	node.setNodeId(nodeId);
    	node.setAllocatedCount(0);
    	node.setDiskHealth(DISK_HEALTH_GOOD);
    	nodeMap.put(nodeId, node);
    }

}
