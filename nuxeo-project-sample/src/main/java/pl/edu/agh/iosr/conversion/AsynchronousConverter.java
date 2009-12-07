package pl.edu.agh.iosr.conversion;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.model.TranslationOrder;

/**
 * Implementuje architekture otrzymującą asynchroniczne zadania zaś 
 * wykonującą je w innym wątku.
 * <br>
 * Za pomocą abstrakcyjnej metody <code>proceed()</code> 
 * implementuje wzorzec projektowy strategy.
 * 
 * @author Bartłomiej Czopyk, Piotr Pomykała
 * @Copyright Piotr Pomykała
 * */

@Scope(ScopeType.APPLICATION)
abstract public class AsynchronousConverter implements Runnable {

	protected Queue<ConversionTask> tasks = new LinkedList<ConversionTask>();

	// parametryzowana ilosc wątków konwertera
	protected int threadsNo = 1;

	private boolean activity = true;

	// synchronization with lock
	private final ReentrantLock synchronizator = new ReentrantLock(true);
	private final Condition condition = synchronizator.newCondition();

	protected enum SupportedTasks {
		CONVERT, RECONVERT
	}

	/**
	 * task translacji.
	 * <br>
	 * wrapper dla translationOrderu, dekoruje go informacją, 
	 * co należy z nim zrobić (->xliff / xliff->)
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

	/**
	 * Zleca zadanie konwersji.
	 * <br>
	 * Asynchroniczna.
	 * 
	 * @param translationOrder - zlecenie translacji
	 * */
	public void convert(TranslationOrder translationOrder) {
		synchronizator.lock();
		log(this.getClass(), "Adding " + translationOrder + " to convert");
		tasks.add(new ConversionTask(translationOrder, SupportedTasks.CONVERT));
		condition.signal();
		synchronizator.unlock();
	}

	/**
	 * Zleca zadanie konwersji.
	 * <br>
	 * Asynchroniczna.
	 * 
	 * @param translationOrder - zlecenie translacji
	 * */
	public void reConvert(TranslationOrder translationOrder) {
		synchronizator.lock();
		log(this.getClass(), "Adding " + translationOrder + " to the reconvert");
		tasks
				.add(new ConversionTask(translationOrder,
						SupportedTasks.RECONVERT));
		condition.signal();
		synchronizator.unlock();
	}

	private ConversionTask getEvent() {
		synchronizator.lock();
		while (tasks.size() == 0) {
			try {
				condition.await();
			} catch (Exception e) {
				log(this.getClass(), e.getMessage(), Level.WARNING);
			}
		}
		ConversionTask ct = tasks.poll();
		synchronizator.unlock();
		return ct;
	}

	public void run() {
		while (activity) {
			try {
				proceed(getEvent());
			} catch (Exception e) {
				log(this.getClass(), e.getMessage(), Level.SEVERE);
			}
		}
	}

	public AsynchronousConverter() {
	}

	@Create
	public void init() {
		for (int i = 0; i < threadsNo; i++) {
			new Thread(this).start();
			log(this.getClass(), "Thread no: " + i + " STARTING");
		}
	}

	@Destroy
	public void shutdown() {
		activity = false;
		log(this.getClass(), "STOPPING");
	}
	
	/**
	 * Przeprowadza konwersje o parametrach zadanych w <code>conversionTask</code>
	 * 
	 * @author Marysia
	 * */
	abstract public void proceed(ConversionTask conversionTask);
}
