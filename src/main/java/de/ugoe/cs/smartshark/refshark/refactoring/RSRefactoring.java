package de.ugoe.cs.smartshark.refshark.refactoring;

import de.ugoe.cs.smartshark.model.Refactoring;
import de.ugoe.cs.smartshark.refshark.util.Common;
import de.ugoe.cs.smartshark.refshark.util.DatabaseContext;
import refdiff.core.rm2.model.SDMethod;
import refdiff.core.rm2.model.SDType;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * The base class for all refactoring types that should be stored in the database.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public abstract class RSRefactoring {
  protected final SDRefactoring refactoring;
  protected final DatabaseContext db;
  protected Refactoring dbRefactoring;

  /**
   * The constructor. Initializes the common refactoring information based on the
   * current commit to be analyzed.
   *
   * @param refactoring The refactoring found by <code>RefDiff</code>.
   */
  public RSRefactoring(SDRefactoring refactoring) {
    this.refactoring = refactoring;
    this.db = DatabaseContext.getInstance();
    dbRefactoring = new Refactoring();
    commonStates();
  }

  /**
   * Getter of the refactoring found by <code>RefDiff</code>.
   *
   * @return The refactoring found by <code>RefDiff</code>.
   */
  public SDRefactoring getRefactoring() {
    return refactoring;
  }

  /**
   * Generates the type name with "$" as separator for subtypes.
   *
   * @param type The entity type provided by <code>RefDiff</code>.
   * @return The string representation of this type with "$" as separator for subtypes.
   */
  protected String getTypeNameLong(SDType type) {
    if (type.nestingLevel() > 0) {
      return Common.formatNestedTypeName(type.fullName(), type.nestingLevel());
    } else {
      return type.fullName();
    }
  }

  /**
   * Generates the simple method name including the signature.
   *
   * @param method The entity representing the method provided by <code>RefDiff</code>.
   * @return The simple method name including the signature.
   */
  protected String getMethodNameSimple(SDMethod method) {
    if (method.isConstructor()) {
      return method.container().simpleName() + method.simpleName();
    } else {
      return method.simpleName();
    }
  }

  /**
   *Generates the long method name including the signature but omits the return type.
   *
   * @param method The entity representing the method provided by <code>RefDiff</code>.
   * @return The long method name including the signature but without the return type.
   *          A constructor is indicated with <code><init></code>.
   */
  protected String getMethodNameLongPart(SDMethod method) {
    String res = getTypeNameLong(method.container()) + ".";
    if (method.isConstructor()) {
      res += "<init>";
    }
    res += method.simpleName();
    return res;
  }

  /**
   * Saves the generated <code>Refactoring</code> instance in the mungodb.
   */
  public void save() {
    db.save(dbRefactoring);
  }

  /**
   * Searches the code entity states of the refactoring according to the specialized type.
   */
  abstract protected void initStates();

  /**
   * Initializes the <code>Refactoring</code> instance with common information
   * according to the current commit.
   */
  protected void commonStates() {
    // general refactoring information
    dbRefactoring.setCommitId(db.getCommit().getId());
    dbRefactoring.setType(Common.refactoringTypeToString(refactoring.getRefactoringType()));
    dbRefactoring.setDescription(refactoring.toString());
    dbRefactoring.setDetectionTool("refDiff");
  }

}
