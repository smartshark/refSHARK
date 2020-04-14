package de.ugoe.cs.smartshark.refshark.refactoring;

import de.ugoe.cs.smartshark.model.Commit;
import de.ugoe.cs.smartshark.model.RefactoringState;
import org.bson.types.ObjectId;
import refdiff.core.rm2.model.SDAttribute;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * This class represents a concrete implementation of the refactoring Move Attribute.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RSMoveAttribute extends RSRefactoring {
  private final SDAttribute attributeBefore;
  private final SDAttribute attributeAfter;

  public RSMoveAttribute(SDRefactoring refactoring) {
    super(refactoring);
    attributeBefore = (SDAttribute) refactoring.getEntityBefore();
    attributeAfter = (SDAttribute) refactoring.getEntityAfter();
    initStates();
  }

  private String typeBeforeName() {
    return getTypeNameLong(attributeBefore.container());
  }

  private String attributeBeforeName() {
    return attributeBefore.fullName();
  }

  private String typeAfterName() {
    return getTypeNameLong(attributeAfter.container());
  }

  private String attributeAfterName() {
    return attributeAfter.fullName();
  }

  /**
   * The method initializes the code entity states of the refactoring.
   * EntityBefore represents the attribute before moving and EntityAfter represents the
   * attribute after moving. ParentEntities corresponds to the respective classes.
   */
  protected void initStates() {
    // code entity states of this commit
    RefactoringState state = new RefactoringState();
    ObjectId typeBefore = db.findClassEntityState(typeBeforeName());
    state.setParentCeBefore(typeBefore);
    ObjectId typeAfter = db.findClassEntityState(typeAfterName());
    state.setParentCeAfter(typeAfter);
    state.setCeBefore(db.findAttributeEntityState(attributeBeforeName(), typeBefore, db.getCommit()));
    state.setCeAfter(db.findAttributeEntityState(attributeAfterName(), typeAfter, db.getCommit()));
    dbRefactoring.setState(state);

    // code entity states of parent commits
    for (Commit c : db.getParents()) {
      state = new RefactoringState();
      ObjectId typeBeforeParentCommit = db.findClassEntityState(typeBeforeName(), c);
      state.setParentCeBefore(typeBeforeParentCommit);
      ObjectId typeAfterParentCommit = db.findClassEntityState(typeAfterName(), c);
      state.setParentCeAfter(typeAfterParentCommit);
      state.setCeBefore(db.findAttributeEntityState(attributeBeforeName(), typeBeforeParentCommit, c));
      state.setCeAfter(db.findAttributeEntityState(attributeAfterName(), typeAfterParentCommit, c));
      dbRefactoring.getParentStates().add(state);
    }
  }

}
