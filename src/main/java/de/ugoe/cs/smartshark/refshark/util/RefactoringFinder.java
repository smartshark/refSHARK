package de.ugoe.cs.smartshark.refshark.util;

import com.google.common.collect.Lists;
import de.ugoe.cs.smartshark.refshark.refactoring.*;
import org.eclipse.jgit.lib.Repository;
import refdiff.core.RefDiff;
import refdiff.core.api.GitService;
import refdiff.core.rm2.model.refactoring.SDRefactoring;
import refdiff.core.util.GitServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * This class uses RefDiff to search refactorings in a given commit and creates
 * wrapper instances for each found refactoring.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RefactoringFinder {
  private final String repoPath;
  private final String commit;

  public RefactoringFinder() {
    this.repoPath = Parameter.getInstance().getRepoPath();
    this.commit = Parameter.getInstance().getCommit();
  }

  /**
   * Analyzes the current commit using RefDiff.
   *
   * @return All found refactorings in the current commit.
   */
  public List<RSRefactoring> findRefactorings() {
    GitService gitService = new GitServiceImpl();
    Repository repository;
    List<RSRefactoring> foundRefactorings = Lists.newArrayList();

    try {
      repository = gitService.openRepository(repoPath);
      foundRefactorings = checkCommit(repository, commit);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.err.println(e.getStackTrace());
    }

    return foundRefactorings;
  }


  private List<RSRefactoring> checkCommit(Repository repository, String commit) {
    RefDiff refDiff = new RefDiff();
    List<SDRefactoring> refactorings = refDiff.detectAtCommit(repository, commit);
    List<RSRefactoring> foundRefactorings = Lists.newArrayList();

    for (SDRefactoring refactoring : refactorings) {

      switch (refactoring.getRefactoringType()) {
        case EXTRACT_OPERATION:
          RSExtractMethod em = new RSExtractMethod(refactoring);
          foundRefactorings.add(em);
          break;
        case RENAME_CLASS:
          RSRenameClass rc = new RSRenameClass(refactoring);
          foundRefactorings.add(rc);
          break;
        case MOVE_ATTRIBUTE:
          RSMoveAttribute ma = new RSMoveAttribute(refactoring);
          foundRefactorings.add(ma);
          break;
        case RENAME_METHOD:
          RSRenameMethod rm = new RSRenameMethod(refactoring);
          foundRefactorings.add(rm);
          break;
        case INLINE_OPERATION:
          RSInlineMethod im = new RSInlineMethod(refactoring);
          foundRefactorings.add(im);
          break;
        case MOVE_OPERATION:
          RSMoveMethod mm = new RSMoveMethod(refactoring);
          foundRefactorings.add(mm);
          break;
        case PULL_UP_OPERATION:
          RSPullUpMethod pm = new RSPullUpMethod(refactoring);
          foundRefactorings.add(pm);
          break;
        case MOVE_CLASS:
          RSMoveClass mc = new RSMoveClass(refactoring);
          foundRefactorings.add(mc);
          break;
        case MOVE_RENAME_CLASS:
          RSMoveAndRenameClass mrc = new RSMoveAndRenameClass(refactoring);
          foundRefactorings.add(mrc);
          break;
        case MOVE_CLASS_FOLDER:
          // Not implemented
          break;
        case PULL_UP_ATTRIBUTE:
          RSPullUpAttribute pua = new RSPullUpAttribute(refactoring);
          foundRefactorings.add(pua);
          break;
        case PUSH_DOWN_ATTRIBUTE:
          RSPushDownAttribute pda = new RSPushDownAttribute(refactoring);
          foundRefactorings.add(pda);
          break;
        case PUSH_DOWN_OPERATION:
          RSPushDownMethod pdm = new RSPushDownMethod(refactoring);
          foundRefactorings.add(pdm);
          break;
        case EXTRACT_INTERFACE:
          // TODO: find occurrences (junit) and implement class for this type.
          break;
        case EXTRACT_SUPERCLASS:
          RSExtractSuperclass es = new RSExtractSuperclass(refactoring);
          foundRefactorings.add(es);
          break;
        case MERGE_OPERATION:
          // not implemented.
          break;
        case EXTRACT_AND_MOVE_OPERATION:
          // not implemented.
          break;
        case CONVERT_ANONYMOUS_CLASS_TO_TYPE:
          // not implemented.
          break;
        case INTRODUCE_POLYMORPHISM:
          // not implemented.
          break;
        case RENAME_PACKAGE:
          // not implemented.
          break;
        case CHANGE_METHOD_SIGNATURE:
          // not implemented.
          break;
        default:
          break;
      }
    }
    return foundRefactorings;
  }
}
