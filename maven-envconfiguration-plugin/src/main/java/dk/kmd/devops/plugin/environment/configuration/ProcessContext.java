/**
 * 
 */
package dk.kmd.devops.plugin.environment.configuration;

/**
 * @author mnb@kmd.dk (Morten Bertelsen)
 *
 */
public class ProcessContext {

  private final String env;
  private final int batchSize;
  private final long index;


  public ProcessContext(final String env, final long index, final int batchSize) {
    super();
    this.env = env;
    this.index = index;
    this.batchSize = batchSize;
  }

  public String getEnv() {
    return env;
  }
  
  public long getIndex() {
    return index;
  }

  public int getBatchSize() {
    return batchSize;
  }

}
