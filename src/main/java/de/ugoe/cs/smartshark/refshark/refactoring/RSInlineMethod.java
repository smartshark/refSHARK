package de.ugoe.cs.smartshark.refshark.refactoring;

import de.ugoe.cs.smartshark.refshark.model.Commit;
import de.ugoe.cs.smartshark.refshark.model.RefactoringState;
import org.bson.types.ObjectId;
import refdiff.core.rm2.model.SDMethod;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * This class represents a concrete implementation of the refactoring Inline Method.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RSInlineMethod extends RSRefactoring {
  private final SDMethod inlinedMethod;
  private final SDMethod dest;

  public RSInlineMethod(SDRefactoring refactoring) {
    super(refactoring);
    inlinedMethod = (SDMethod) refactoring.getEntityBefore();
    dest = (SDMethod) refactoring.getEntityAfter();
    initStates();
  }

  /**
   * The method initializes the code entity states of the refactoring.
   * EntityBefore represents the inlined method and EntityAfter represents the
   * destination or caller method. ParentEntities corresponds to the respective classes.
   */
  protected void initStates() {
    // code entity states of this commit
    RefactoringState state = new RefactoringState();
    ObjectId inlinedClass = db.findClassEntityState(typeInlinedMethodLongName());
    state.setParentCeBefore(inlinedClass);
    ObjectId callerMethodClass = db.findClassEntityState(typeDestLongName());
    state.setParentCeAfter(callerMethodClass);
    state.setCeBefore(db.findMethodEntityState(inlinedMethodLongNamePart(), inlinedClass, db.getCommit()));
    state.setCeAfter(db.findMethodEntityState(destMethodLongNamePart(), callerMethodClass, db.getCommit()));
    dbRefactoring.setState(state);

    // code entity states of parent commits
    for (Commit c : db.getParents()) {
      state = new RefactoringState();
      ObjectId inlinedClassParentCommit = db.findClassEntityState(typeInlinedMethodLongName(), c);
      state.setParentCeBefore(inlinedClassParentCommit);
      ObjectId callerMethodClassParentCommit = db.findClassEntityState(typeDestLongName(), c);
      state.setParentCeAfter(callerMethodClassParentCommit);
      state.setCeBefore(db.findMethodEntityState(inlinedMethodLongNamePart(), inlinedClassParentCommit, c));
      state.setCeAfter(db.findMethodEntityState(destMethodLongNamePart(), callerMethodClassParentCommit, c));
      dbRefactoring.getParentStates().add(state);
    }
  }

  private String typeInlinedMethodLongName() {
    return getTypeNameLong(inlinedMethod.container());
  }

  private String typeDestLongName() {
    return getTypeNameLong(dest.container());
  }

  private String inlinedMethodLongNamePart() {
    return getMethodNameLongPart(inlinedMethod);
  }

  private String destMethodLongNamePart() {
    return getMethodNameLongPart(dest);
  }

}
