package de.ugoe.cs.smartshark.refshark.refactoring;

import refdiff.core.rm2.model.refactoring.SDRefactoring;

/**
 * This class represents a concrete implementation of the refactoring Pull up Method.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */

public class RSPullUpMethod extends RSRenameMethod {
  public RSPullUpMethod(SDRefactoring refactoring) {
    super(refactoring);
  }
}
