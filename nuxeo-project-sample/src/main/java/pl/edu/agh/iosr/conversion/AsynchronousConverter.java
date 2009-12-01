package pl.edu.agh.iosr.conversion;

import java.util.LinkedList;
import java.util.Queue;

import pl.edu.agh.iosr.model.TranslationOrder;

/**
 * implementuje architekture wykonującą pewne operacje
 * w jednym wątku, zaś otrzymującą asynchroniczne zadania
 * 
 * <br>
 * czopyk to zaimplementuje jak tylko dostanie pendrive od warusa!
 * */
abstract public class AsynchronousConverter {
	
	Queue<ConversionTask> tasks = new LinkedList<ConversionTask>();
	
	private boolean activity = true;
	
	private enum SupportedTasks {
		CONVERT, RECONVERT
	}
	
	/**
	 * task translacji
	 * */
	class ConversionTask {
		public final TranslationOrder translationOrder;
		public final SupportedTasks task;
		public ConversionTask(TranslationOrder translationOrder,
				SupportedTasks task) {
			super();
			this.translationOrder = translationOrder;
			this.task = task;
		}
	}
	
	public void convert(TranslationOrder translationOrder) {
		synchronized(tasks) {
			tasks.add(new ConversionTask(translationOrder, SupportedTasks.CONVERT));
		}
	}
	
	public void reConvert(TranslationOrder translationOrder) {
		synchronized(tasks) {
			tasks.add(new ConversionTask(translationOrder, SupportedTasks.RECONVERT));
		}
	}
	
	public AsynchronousConverter() {
		new Runnable() {

			@Override
			public void run() {
				
				ConversionTask conversionTask;
				
				while(activity) {
				
					try {
						
						synchronized (tasks) {
							conversionTask = tasks.poll();
						}
						
						proceed(conversionTask);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		}.run();
	}
	
	protected void finalize() {
		activity = false;
	}
	
	abstract public void proceed(ConversionTask conversionTask);
}
