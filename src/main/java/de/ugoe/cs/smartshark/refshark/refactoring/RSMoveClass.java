package de.ugoe.cs.smartshark.refshark.refactoring;

import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * This class represents a concrete implementation of the refactoring Move Class.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RSMoveClass extends RSRenameClass {
  public RSMoveClass(SDRefactoring refactoring) {
    super(refactoring);
  }
}
