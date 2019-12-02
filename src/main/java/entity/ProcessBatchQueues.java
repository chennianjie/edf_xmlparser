package entity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class ProcessBatchQueues {
	public static AtomicInteger parseNum = new AtomicInteger(0);
	public static AtomicInteger insertNum = new AtomicInteger(0);
	public static BlockingQueue<IncrementalStg> IncrementalQueue = new LinkedBlockingQueue<IncrementalStg>();
}
