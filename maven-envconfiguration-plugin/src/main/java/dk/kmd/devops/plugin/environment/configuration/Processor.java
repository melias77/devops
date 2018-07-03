/**
 * 
 */
package dk.kmd.devops.plugin.environment.configuration;


/**
 * @author mbn (Morten Bertelsen)
 *
 */
public interface Processor<T> {

	public void process(ProcessContext context, T target);

	
}

