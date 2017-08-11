package de.ugoe.cs.smartshark.refshark;

import de.ugoe.cs.smartshark.refshark.refactoring.RSRefactoring;
import de.ugoe.cs.smartshark.refshark.util.DatabaseContext;
import de.ugoe.cs.smartshark.refshark.util.Parameter;
import de.ugoe.cs.smartshark.refshark.util.RefactoringFinder;

import java.util.List;

/**
 * The main class of the application refSHARK.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RefShark {

  public static void main(String[] args) {
    Parameter param = Parameter.getInstance();
    param.init(args);

    RefactoringFinder rf = new RefactoringFinder();
    List<RSRefactoring> refactorings = rf.findRefactorings();


    // clear all stored refactorings assigned to this commit
    DatabaseContext.getInstance().clear();

    // save found refactorings
    for (RSRefactoring r : refactorings) {
      r.save();
    }

  }
}


