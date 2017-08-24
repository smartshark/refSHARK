package de.ugoe.cs.smartshark.refshark.refactoring;

import de.ugoe.cs.smartshark.refshark.model.Commit;
import de.ugoe.cs.smartshark.refshark.model.RefactoringState;
import org.bson.types.ObjectId;
import refdiff.core.rm2.model.SDType;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * This class represents a concrete implementation of the refactoring Extract Superclass.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RSExtractSuperclass extends RSRefactoring {
  private final SDType extractedType;
  private final SDType typeOriginAfter;

  public RSExtractSuperclass(SDRefactoring refactoring) {
    super(refactoring);
    extractedType = (SDType) refactoring.getEntityAfter();
    typeOriginAfter = (SDType) refactoring.getEntityBefore();
    initStates();
  }

  private String extractedTypeLongName() {
    return getTypeNameLong(extractedType);
  }

  private String typeOriginAfterLongName() {
    return getTypeNameLong(typeOriginAfter);
  }

  /**
   * The method initializes the code entity states of the refactoring.
   * EntityBefore represents the extracted type and EntityAfter represents the
   * origin type - the new base type.
   */
  protected void initStates() {
    // code entity states of this commit
    RefactoringState state = new RefactoringState();
    ObjectId extractedType = db.findClassEntityState(extractedTypeLongName());
    state.setCeBefore(extractedType);
    ObjectId typeOriginAfter = db.findClassEntityState(typeOriginAfterLongName());
    state.setCeAfter(typeOriginAfter);
    dbRefactoring.setState(state);

    // code entity states of parent commits
    for (Commit c : db.getParents()) {
      state = new RefactoringState();
      ObjectId extractedTypeParentCommit = db.findClassEntityState(extractedTypeLongName(), c);
      state.setCeBefore(extractedTypeParentCommit);
      ObjectId typeOriginAfterParentCommit = db.findClassEntityState(typeOriginAfterLongName(), c);
      state.setCeAfter(typeOriginAfterParentCommit);
      dbRefactoring.getParentStates().add(state);
    }
  }
}
