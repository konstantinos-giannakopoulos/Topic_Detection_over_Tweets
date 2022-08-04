package util.cpu;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import com.sun.management.OperatingSystemMXBean;

public final class CPU {

    private final long MILLISECONDS = 1000000L;
    private final long SECONDS = MILLISECONDS * 1000L;
    private long startCPUTimeNano;    //taskCPUTimeNano;
    private long elapsedCPUTimeNano = 0;
    private long startUserTimeNano;   //taskUserTimeNano;
    private long elapsedUserTimeNano = 0;
    private long startSystemTimeNano; //taskSystemTimeNano;
    private long elapsedSystemTimeNano = 0;

    public void startOrResume() {
	this.startCPUTimeNano = getCpuTime();
	this.startUserTimeNano = getUserTime();
	this.startSystemTimeNano = getSystemTime();
    } // startOrResume()

    public void pause() {
	this.elapsedCPUTimeNano += getCpuTime() - this.startCPUTimeNano;
	this.elapsedUserTimeNano += getUserTime() - this.startUserTimeNano;
	this.elapsedSystemTimeNano += getSystemTime() - this.startSystemTimeNano;
    } // pause()

    public void stop(String message) {
	pause();
	long taskUserTimeNano = this.elapsedUserTimeNano; //getUserTime() - startUserTimeNano;
	long taskSystemTimeNano = this.elapsedSystemTimeNano;//getSystemTime() - startSystemTimeNano;
	long taskCPUTimeNano = this.elapsedCPUTimeNano;//getCpuTime() - startCPUTimeNano;
	System.out.println("[P]");
	System.out.println("[P] ----------------------" 
			   + " CPU execution time " + "for " + message + " "
			   + "---------------------------");
	System.out.println("[P] | User time " 
			   + "(running Application's code):\t"  
			   + (taskUserTimeNano / MILLISECONDS) // MILLISECONDS 
			   + "\t\t\t |");
	System.out.println("[P] | Total System time " 
			   + "(running OS code):\t\t" 
			   + (taskSystemTimeNano / MILLISECONDS) // MILLISECONDS
			   + "\t\t\t |");
	System.out.println("[P] | Total CPU time:\t\t\t\t" 
			   + (taskCPUTimeNano / MILLISECONDS) // MILLISECONDS
			   + "\t\t\t |");
	System.out.println("[P] -----------------------" 
			   + " CPU execution time " 
			   + "---------------------------");
	System.out.println("[P]");
    } // stop()

    
    
    /** Get CPU time in nanoseconds; 
	the total time spent using a CPU for the application */
    private static long getCpuTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ?
            bean.getCurrentThreadCpuTime() : 0L;
    }

    /** Get user time in nanoseconds;
	the time spent running the application's code. */
    private static long getUserTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ?
            bean.getCurrentThreadUserTime() : 0L;
    }

    /** Get system time in nanoseconds;
	the time spent running OS code on behalf of the application 
	(such as for I/O). */
    private static long getSystemTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ?
            (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime()) : 0L;
    }

} // CPU
