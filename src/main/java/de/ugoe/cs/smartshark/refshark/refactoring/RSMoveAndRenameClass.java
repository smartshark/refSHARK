package de.ugoe.cs.smartshark.refshark.refactoring;

import refdiff.core.rm2.model.refactoring.SDRefactoring;


/**
 * This class represents a concrete implementation of the refactoring Move and Rename Class.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RSMoveAndRenameClass extends RSMoveClass {
  public RSMoveAndRenameClass(SDRefactoring refactoring) {
    super(refactoring);
  }
}
