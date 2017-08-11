package de.ugoe.cs.smartshark.refshark.refactoring;

import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * This class represents a concrete implementation of the refactoring Move Pull up Attribute.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */

public class RSPullUpAttribute extends RSMoveAttribute {
  public RSPullUpAttribute(SDRefactoring refactoring) {
    super(refactoring);
  }
}
