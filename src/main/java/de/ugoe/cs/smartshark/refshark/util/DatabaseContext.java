package de.ugoe.cs.smartshark.refshark.util;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import de.ugoe.cs.smartshark.refshark.RefShark;
import de.ugoe.cs.smartshark.refshark.model.*;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of the database context. It provides connection to a mungodb as well
 * as all required methods for retrieving, deleting and storing desired entities. To avoid
 * multiple instances the singleton pattern is used.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class DatabaseContext {
  Logger logger = (Logger) LoggerFactory.getLogger("de.ugoe.cs.smartshark.refshark.util.DatabaseContext");
  private static DatabaseContext instance;
  private final Datastore datastore;

  private Project project;
  private VCSystem vcSystem;
  private Commit commit;
  private List<Commit> parents;

  /**
   * The constructor. Creates the instance initialized with a database connection
   * according to the specified program arguments.
   */
  private DatabaseContext() {
    datastore = createDatastore();
    parents = Lists.newArrayList();
    init();
  }

  /**
   * Returns the instance of the database context. If there is no instance one will be instantiated.
   *
   * @return The instance of the database context
   */
  public static synchronized DatabaseContext getInstance() {
    if (instance == null) {
      instance = new DatabaseContext();
    }
    return instance;
  }

  /*
   * Initializes the instance with commit specific information.
   */
  private void init() {
    final List<VCSystem> vcs = datastore.createQuery(VCSystem.class)
        .field("url").equal(Parameter.getInstance().getUrl())
        .asList();
    if (vcs.size() != 1) {
      logger.warn("Found: " + vcs.size() + " instances of VCSystem. Expected 1 instance.");
    }
    vcSystem = vcs.get(0);

    project = datastore.getByKey(Project.class, new Key<Project>(Project.class, "project", vcSystem.getProjectId()));

    commit = findCommit(Parameter.getInstance().getCommit());

    if (commit == null) {
      logger.warn("Could not find commit: " + Parameter.getInstance().getCommit());
    }

    for (String p : commit.getParents()) {
      parents.add(findCommit(p));
    }
  }


  public Project getProject() {
    return project;
  }

  public VCSystem getVcSystem() {
    return vcSystem;
  }

  public Commit getCommit() {
    return commit;
  }

  public List<Commit> getParents() {
    return parents;
  }

  /**
   * Searches for a commit represented by the given revision hash.
   *
   * @param hash The revision hash of the commit.
   * @return The commit represented by the given revision hash.
   */
  public Commit findCommit(String hash) {
    Query<Commit> commitQuery = datastore.find(Commit.class);
    commitQuery.and(
        commitQuery.criteria("revision_hash").equal(hash),
        commitQuery.criteria("vcs_system_id").equal(getVcSystem().getId())
    );
    final List<Commit> commits = commitQuery.asList();

    if (commits.size() == 1) {
      return commits.get(0);
    } else {
      logger.debug("Could not find commit: "  + commit.getMessage());
      return null;
    }
  }

  /**
   * Searches for the code entity state of the given class name in the
   * current commit.
   *
   * @param name The name of the class.
   * @return The code entity state of the class in the current commit.
   */
  public ObjectId findClassEntityState(String name) {
    Query<CodeEntityState> cesq = datastore.find(CodeEntityState.class);
    cesq.and(
        cesq.criteria("commit_id").equal(getCommit().getId()),
        cesq.criteria("ce_type").equal("class"),
        cesq.criteria("long_name").equal(name)
    );
    final List<CodeEntityState> ces = cesq.asList();

    if (ces.size() == 1) {
      return ces.get(0).getId();
    } else {
      logger.debug("Could not find ces for class: " + name + " in commit " + commit.getMessage());
      return null;
    }
  }

  /**
   * Searches for the code entity state of the given class name in the
   * given commit.
   *
   * @param name The name of the class.
   * @param c    The commit to analyze.
   * @return The code entity state of the given class name in the given commit.
   */
  public ObjectId findClassEntityState(String name, Commit c) {
    Query<CodeEntityState> cesq = datastore.find(CodeEntityState.class);
    cesq.and(
        cesq.criteria("commit_id").equal(c.getId()),
        cesq.criteria("ce_type").equal("class"),
        cesq.criteria("long_name").equal(name)
    );
    final List<CodeEntityState> ces = cesq.asList();

    if (ces.size() == 1) {
      return ces.get(0).getId();
    } else {
      logger.debug("Could not find ces for class: " + name + " in commit " + commit.getMessage());
      return null;
    }
  }

  /**
   * Searches for the code entity state of the given method name in the
   * given commit.
   *
   * @param name             The name of the method.
   * @param classEntityState The class where the method is implemented.
   * @param c                The commit to analyze.
   * @return The code entity state of the method according to the given parameter.
   */
  public ObjectId findMethodEntityState(String name, ObjectId classEntityState, Commit c) {
    if (classEntityState == null) {
      return null;
    }

    Query<CodeEntityState> cesq = datastore.find(CodeEntityState.class);
    cesq.and(
        cesq.criteria("commit_id").equal(c.getId()),
        cesq.criteria("ce_type").equal("method"),
        cesq.criteria("ce_parent_id").equal(classEntityState)
    );
    final List<CodeEntityState> methods = cesq.asList();

    CodeEntityState method = null;
    for (CodeEntityState s : methods) {
      if (Common.compareMethods(s.getLongName(), name)) {
        method = s;
      }
    }

    return method == null ? null : method.getId();
  }

  /**
   * Searches for the code entity state of the given attribute name in the
   * given commit.
   *
   * @param name             The name of the attribute.
   * @param classEntityState The class where the attribute is implemented.
   * @param c                The commit to analyze.
   * @return The code entity state of the attribute according to the given parameter.
   */
  public ObjectId findAttributeEntityState(String name, ObjectId classEntityState, Commit c) {
    if (classEntityState == null) {
      return null;
    }

    Query<CodeEntityState> attributeStates = datastore.find(CodeEntityState.class);
    attributeStates.and(
        attributeStates.criteria("commit_id").equal(c.getId()),
        attributeStates.criteria("ce_type").equal("attribute"),
        attributeStates.criteria("ce_parent_id").equal(classEntityState),
        attributeStates.criteria("long_name").startsWith(name.replace("#", "."))

    );
    final List<CodeEntityState> attributes = attributeStates.asList();

    if (attributes.size() == 1) {
      return attributes.get(0).getId();
    } else {
      logger.debug("Could not find ces for attribute: " + name + " in commit " + commit.getMessage());
      return null;
    }
  }

  /**
   * Stores the given refactoring in the database.
   *
   * @param refactoring The refactoring to be stored.
   */
  public void save(Refactoring refactoring) {
    datastore.save(refactoring);
    RefShark.STORED_REFACTORINGS++;
    logger.debug("Stored refactoring: " + refactoring.getDescription());
  }

  /**
   * Removes all refactorings assigned to the current commit.
   */
  public void clear() {
    final Query<Refactoring> refactorings = datastore.createQuery(Refactoring.class)
        .field("commit_id").equal(getCommit().getId());

    datastore.delete(refactorings);
  }

  private Datastore createDatastore() {
    Morphia morphia = new Morphia();
    morphia.mapPackage("de.ugoe.cs.smartshark.refshark.model");
    Datastore datastore = null;

    if (Parameter.getInstance().getUrl().isEmpty() || Parameter.getInstance().getDbPassword().isEmpty()) {
      datastore = morphia.createDatastore(new MongoClient(Parameter.getInstance().getDbHostname(), Parameter.getInstance().getDbPort()), Parameter.getInstance().getDbName());
    } else {
      ServerAddress addr = new ServerAddress(Parameter.getInstance().getDbHostname(), Parameter.getInstance().getDbPort());
      List<MongoCredential> credentialsList = Lists.newArrayList();
      MongoCredential credential = MongoCredential.createCredential(
          Parameter.getInstance().getDbUser(), Parameter.getInstance().getDbAuthentication(), Parameter.getInstance().getDbPassword().toCharArray());
      credentialsList.add(credential);
      MongoClient client = new MongoClient(addr, credentialsList);
      datastore = morphia.createDatastore(client, Parameter.getInstance().getDbName());
    }

    return datastore;
  }

}

