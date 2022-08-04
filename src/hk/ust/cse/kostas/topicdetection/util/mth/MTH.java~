package util.mth;

import java.lang.management.*;
import java.util.List;
import java.util.ArrayList;

public final class MTH {

    private static final long MEGABYTE = 1024L * 1024L;
    private static Runtime runtime = Runtime.getRuntime(); 

    /* It gives same results as below.
    public static void m() {
	List<MemoryPoolMXBean> memoryPools = 
	    new ArrayList<MemoryPoolMXBean>
	    (ManagementFactory.getMemoryPoolMXBeans()); 
	long usedHeapMemoryAfterLastGC = 0; 
	for (MemoryPoolMXBean memoryPool : memoryPools) { 
	    if (memoryPool.getType().equals(MemoryType.HEAP)) { 
		MemoryUsage poolCollectionMemoryUsage = 
		    memoryPool.getCollectionUsage(); 
		usedHeapMemoryAfterLastGC += 
		    poolCollectionMemoryUsage.getUsed(); 
	    } 
	}
	System.out.println(usedHeapMemoryAfterLastGC / MEGABYTE);
    }
    */
    public static void from() {
	runtime.gc(); 
	long maxMemoryBefore = runtime.maxMemory(); 
	long allocatedMemoryBefore = runtime.totalMemory();
	long freeMemoryBefore = runtime.freeMemory(); 
	long usedMemoryBefore = allocatedMemoryBefore - freeMemoryBefore;
	System.out.println("[M]"); 
	System.out.println("[M] --------------------------" 
			   + " Memory before in MB " 
			   + "--------------------------");
	System.out.println("[M] | [Max] " + maxMemoryBefore / MEGABYTE 
			   + "\t|| [Allocated] " 
			   + allocatedMemoryBefore / MEGABYTE
			   + "\t|| [Used] " + usedMemoryBefore / MEGABYTE
			   + "\t|| [Free] " + freeMemoryBefore / MEGABYTE
			   + "\t|");
	System.out.println("[M] --------------------------" 
			   + " Memory before in MB " 
			   + "--------------------------");
	System.out.println("[M]"); 
    } // from()

    public static void to() {
	runtime.gc(); 
	long maxMemoryAfter = runtime.maxMemory(); 
	long allocatedMemoryAfter = runtime.totalMemory();
	long freeMemoryAfter = runtime.freeMemory(); 
	long usedMemoryAfter = allocatedMemoryAfter - freeMemoryAfter;
	System.out.println("[M]"); 
	System.out.println("[M] -------------------------- " 
			   + "Memory after in MB " 
			   + "---------------------------");
	System.out.println("[M] | [Max] " + maxMemoryAfter / MEGABYTE 
			   + "\t|| [Allocated] " 
			   + allocatedMemoryAfter / MEGABYTE
			   + "\t|| [Used] " + usedMemoryAfter / MEGABYTE
			   + "\t|| [Free] " + freeMemoryAfter / MEGABYTE
			   + "\t|"); 
	System.out.println("[M] -------------------------- " 
			   + "Memory after in MB " 
			   + "---------------------------");
	System.out.println("[M]"); 
    } // to()

} // MTH