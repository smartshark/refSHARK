package de.ugoe.cs.smartshark.refshark.refactoring;

import de.ugoe.cs.smartshark.model.Commit;
import de.ugoe.cs.smartshark.model.RefactoringState;
import org.bson.types.ObjectId;
import refdiff.core.rm2.model.SDMethod;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * This class represents a concrete implementation of the refactoring Rename Method.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RSRenameMethod extends RSRefactoring {
  protected final SDMethod methodBefore;
  protected final SDMethod methodAfter;

  public RSRenameMethod(SDRefactoring refactoring) {
    super(refactoring);
    methodBefore = (SDMethod) refactoring.getEntityBefore();
    methodAfter = (SDMethod) refactoring.getEntityAfter();
    initStates();
  }

  /**
   * The method initializes the code entity states of the refactoring.
   * EntityBefore represents the method before applying the refactoring and
   * EntityAfter represents the method after applying the refactoring.
   * ParentEntities corresponds to the respective classes.
   */
  protected void initStates() {
    // code entity states of this commit
    RefactoringState state = new RefactoringState();
    ObjectId typeBefore = db.findClassEntityState(typeMethodBeforeLongName());
    state.setParentCeBefore(typeBefore);
    ObjectId typeAfter = db.findClassEntityState(typeMethodAfterLongName());
    state.setParentCeAfter(typeAfter);
    state.setCeBefore(db.findMethodEntityState(methodBeforeLongNamePart(), typeBefore, db.getCommit()));
    state.setCeAfter(db.findMethodEntityState(methodAfterLongNamePart(), typeAfter, db.getCommit()));
    dbRefactoring.setState(state);

    // code entity states of parent commits
    for (Commit c : db.getParents()) {
      state = new RefactoringState();
      ObjectId typeBeforeParentCommit = db.findClassEntityState(typeMethodBeforeLongName(), c);
      state.setParentCeBefore(typeBeforeParentCommit);
      ObjectId typeAfterParentCommit = db.findClassEntityState(methodAfterLongNamePart(), c);
      state.setParentCeAfter(typeAfterParentCommit);
      state.setCeBefore(db.findMethodEntityState(methodBeforeLongNamePart(), typeBeforeParentCommit, c));
      state.setCeAfter(db.findMethodEntityState(methodAfterLongNamePart(), typeAfterParentCommit, c));
      dbRefactoring.getParentStates().add(state);
    }
  }

  private String typeMethodBeforeLongName() {
    return getTypeNameLong(methodBefore.container());
  }

  private String typeMethodAfterLongName() {
    return getTypeNameLong(methodAfter.container());
  }

  private String methodBeforeLongNamePart() {
    return getMethodNameLongPart(methodBefore);
  }

  private String methodAfterLongNamePart() {
    return getMethodNameLongPart(methodAfter);
  }

}
