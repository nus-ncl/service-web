package sg.ncl.testbed_interface;

public class Node {
	
	private int nodeId;
	private String nodeType;
	private String nodeClass;
	private boolean isVirtNode;
	private boolean isSubNode;
	private String adminMfsOSID;
	private String biosWaitTime;
	private String networkInterface;
	private String defaultOS;
	private String diskSize; // GB
	private String cpuFreq; // mHz
	private String memory; // MB
	private String processor;
	private String virtMachinesPerNode;
	private int allocatedCount;
	private String diskHealth;

	public Node() {
		//Not using this method for now
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getNodeClass() {
		return nodeClass;
	}

	public void setNodeClass(String nodeClass) {
		this.nodeClass = nodeClass;
	}

	public boolean getIsVirtNode() {
		return isVirtNode;
	}

	public void setIsVirtNode(boolean isVirtNode) {
		this.isVirtNode = isVirtNode;
	}

	public boolean getIsSubNode() {
		return isSubNode;
	}

	public void setIsSubNode(boolean isSubNode) {
		this.isSubNode = isSubNode;
	}

	public String getAdminMfsOSID() {
		return adminMfsOSID;
	}

	public void setAdminMfsOSID(String adminMfsOSID) {
		this.adminMfsOSID = adminMfsOSID;
	}

	public String getBiosWaitTime() {
		return biosWaitTime;
	}

	public void setBiosWaitTime(String biosWaitTime) {
		this.biosWaitTime = biosWaitTime;
	}

	public String getNetworkInterface() {
		return networkInterface;
	}

	public void setNetworkInterface(String networkInterface) {
		this.networkInterface = networkInterface;
	}

	public String getDefaultOS() {
		return defaultOS;
	}

	public void setDefaultOS(String defaultOS) {
		this.defaultOS = defaultOS;
	}

	public String getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(String diskSize) {
		this.diskSize = diskSize;
	}

	public String getCpuFreq() {
		return cpuFreq;
	}

	public void setCpuFreq(String cpuFreq) {
		this.cpuFreq = cpuFreq;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getVirtMachinesPerNode() {
		return virtMachinesPerNode;
	}

	public void setVirtMachinesPerNode(String virtMachinesPerNode) {
		this.virtMachinesPerNode = virtMachinesPerNode;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getDiskHealth() {
		return diskHealth;
	}

	public void setDiskHealth(String diskHealth) {
		this.diskHealth = diskHealth;
	}
	
	public int getAllocatedCount() {
		return allocatedCount;
	}

	public void setAllocatedCount(int allocatedCount) {
		this.allocatedCount = allocatedCount;
	}
}
