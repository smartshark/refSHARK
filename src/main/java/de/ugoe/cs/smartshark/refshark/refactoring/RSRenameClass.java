package de.ugoe.cs.smartshark.refshark.refactoring;

import de.ugoe.cs.smartshark.refshark.model.Commit;
import de.ugoe.cs.smartshark.refshark.model.RefactoringState;
import org.bson.types.ObjectId;
import refdiff.core.rm2.model.SDType;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * This class represents a concrete implementation of the refactoring Rename Class.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */

public class RSRenameClass extends RSRefactoring {
  private final SDType typeBefore;
  private final SDType typeAfter;

  public RSRenameClass(SDRefactoring refactoring) {
    super(refactoring);
    typeBefore = (SDType) refactoring.getEntityBefore();
    typeAfter = (SDType) refactoring.getEntityAfter();
    initStates();
  }

  private String typeBeforeLongName() {
    return getTypeNameLong(typeBefore);
  }

  private String typeAfterLongName() {
    return getTypeNameLong(typeAfter);
  }

  /**
   * The method initializes the code entity states of the refactoring.
   * EntityBefore represents the type before applying the refactoring and
   * EntityAfter represents the type after applying the refactoring.
   */
  protected void initStates() {
    // code entity states of this commit
    RefactoringState state = new RefactoringState();
    ObjectId typeBefore = db.findClassEntityState(typeBeforeLongName());
    state.setCeBefore(typeBefore);
    ObjectId typeAfter = db.findClassEntityState(typeAfterLongName());
    state.setCeAfter(typeAfter);
    dbRefactoring.setState(state);

    // code entity states of parent commits
    for (Commit c : db.getParents()) {
      state = new RefactoringState();
      ObjectId typeBeforeParentCommit = db.findClassEntityState(typeBeforeLongName(), c);
      state.setCeBefore(typeBeforeParentCommit);
      ObjectId typeAfterClassParentCommit = db.findClassEntityState(typeAfterLongName(), c);
      state.setCeAfter(typeAfterClassParentCommit);
      dbRefactoring.getParentStates().add(state);
    }
  }
}
