package de.ugoe.cs.smartshark.refshark.refactoring;

import de.ugoe.cs.smartshark.refshark.model.Commit;
import de.ugoe.cs.smartshark.refshark.model.RefactoringState;
import org.bson.types.ObjectId;
import refdiff.core.rm2.model.SDMethod;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * This class represents a concrete implementation of the refactoring Extract Method.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RSExtractMethod extends RSRefactoring {
  private final SDMethod extractedMethod;
  private final SDMethod origin;


  public RSExtractMethod(SDRefactoring refactoring) {
    super(refactoring);
    origin = (SDMethod) refactoring.getEntityBefore();
    extractedMethod = (SDMethod) refactoring.getEntityAfter();
    initStates();
  }

  /**
   * The method initializes the code entity states of the refactoring.
   * EntityBefore represents the origin method and EntityAfter represents the
   * extracted method. ParentEntities corresponds to the respective classes.
   */
  protected void initStates() {
    // code entity states of this commit
    RefactoringState state = new RefactoringState();
    ObjectId originClass = db.findClassEntityState(typeOriginLongName());
    state.setParentCeBefore(originClass);
    ObjectId extractMethodClass = db.findClassEntityState(typeExtractedMethodLongName());
    state.setParentCeAfter(extractMethodClass);
    state.setCeBefore(db.findMethodEntityState(originMethodLongNamePart(), originClass, db.getCommit()));
    state.setCeAfter(db.findMethodEntityState(extractedMethodLongNamePart(), extractMethodClass, db.getCommit()));
    dbRefactoring.setState(state);

    // code entity states of parent commits
    for (Commit c : db.getParents()) {
      state = new RefactoringState();
      ObjectId originClassParentCommit = db.findClassEntityState(typeOriginLongName(), c);
      state.setParentCeBefore(originClassParentCommit);
      ObjectId extractMethodClassParentCommit = db.findClassEntityState(typeExtractedMethodLongName(), c);
      state.setParentCeAfter(extractMethodClassParentCommit);
      state.setCeBefore(db.findMethodEntityState(originMethodLongNamePart(), originClassParentCommit, c));
      state.setCeAfter(db.findMethodEntityState(extractedMethodLongNamePart(), extractMethodClassParentCommit, c));
      dbRefactoring.getParentStates().add(state);
    }
  }

  private String typeOriginLongName() {
    return getTypeNameLong(origin.container());
  }

  private String typeExtractedMethodLongName() {
    return getTypeNameLong(extractedMethod.container());
  }

  private String originMethodLongNamePart() {
    return getMethodNameLongPart(origin);
  }

  private String extractedMethodLongNamePart() {
    return getMethodNameLongPart(extractedMethod);
  }
}
