package de.ugoe.cs.smartshark.refshark;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.ugoe.cs.smartshark.refshark.refactoring.RSRefactoring;
import de.ugoe.cs.smartshark.refshark.util.DatabaseContext;
import de.ugoe.cs.smartshark.refshark.util.Parameter;
import de.ugoe.cs.smartshark.refshark.util.RefactoringFinder;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The main class of the application refSHARK.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RefShark {
  public static int STORED_REFACTORINGS;

  public static void main(String[] args) {
    Parameter param = Parameter.getInstance();
    param.init(args);

    setLogLevel();

    Logger logger = (Logger) LoggerFactory.getLogger("de.ugoe.cs.smartshark.refshark.RefShark");

    RefactoringFinder rf = new RefactoringFinder();
    List<RSRefactoring> refactorings = rf.findRefactorings();

    // clear all stored refactorings assigned to this commit
    DatabaseContext.getInstance().clear();

    // save found refactorings
    for (RSRefactoring r : refactorings) {
      r.save();
    }

    // some additional output
    logger.info("Analyzed commit: " + param.getCommit());
    logger.info("Found: " + refactorings.size() + " refactorings.");
    logger.info(("Stored " + STORED_REFACTORINGS + " in datastore."));
  }

  private static void setLogLevel() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    String level = Parameter.getInstance().getDebugLevel();

    switch (level) {
      case "INFO":
        root.setLevel(Level.INFO);
        break;
      case "DEBUG":
        root.setLevel(Level.DEBUG);
        break;
      case "WARNING":
        root.setLevel(Level.WARN);
        break;
      case "ERROR":
        root.setLevel(Level.ERROR);
        break;
      default:
        root.setLevel(Level.DEBUG);
        break;
    }
  }

}


