package com.wysong.wordcount.model;

import javax.persistence.*;

/**
 * Root parent class for all persistent object entities.
 * <p/>
 * <b>NOTE:</b>  Subclasses should <b>always</b> provide a unique {@link #onEquals(PersistentEntity) onEquals} and
 * {@link #hashCode hasCode} implementation and these should <em>not</em> use the {@link #getEntityId id}
 * property during their execution.  Always keep in mind the entity's 'business keys' aka 'natural keys'
 * when implementing these two methods to prevent duplicate data in the system.
 */
@MappedSuperclass
public abstract class PersistentEntity {

    public static final long DEFAULT_ENTITY_ID = -1l;
    public static final int DEFAULT_ENTITY_VERSION = -1;


    /**
     * RDBMS Primary key, aka 'surrogate key'.  See the {@link #getEntityId() getId()} JavaDoc for more.
     */
    @Id
    @Column(name = "entity_id", nullable = false)
    @GeneratedValue
    protected long entityId = DEFAULT_ENTITY_ID;
    /**
     * Used for optimistic locking strategies.  See the {@link #getEntityVersion() entityVersion} JavaDoc for more.
     */
    @Column(name = "entity_version", nullable = false)
    @Version
    protected int entityVersion = DEFAULT_ENTITY_VERSION;


    /**
     * If children classes override this method they must always call super.clone() to get the object
     * with which they manipulate further to clone remaining attributes.  Never acquire
     * the cloned object directly via 'new' operator (this is true in Java for any class - it is not special to
     * this Entity class).
     */
    @Override
    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    public Object clone() {
        PersistentEntity e;
        try {
            e = (PersistentEntity) super.clone();
        } catch (CloneNotSupportedException neverHappens) {
            // Should _never_ happen since this class is Cloneable and
            // a direct subclass of Object
            throw new InternalError("Unable to clone object of type [" + getClass().getName() + "]");
        }
        e.setEntityId(DEFAULT_ENTITY_ID);
        e.setEntityVersion(DEFAULT_ENTITY_VERSION);

        return e;
    }

    /**
     * This method is declared final and does a lot of performance optimization:
     * <p/>
     * It delegates the actual "equals" check to subclasses via the onEquals method, but
     * it will only do so if the object for equality comparison is
     * <ol>
     * <li>not the same memory location as the current object (fast sanity check)</li>
     * <li>is <code>instanceof</code> Entity</li>
     * <li>Does not have the same id() property</li>
     * </ol>
     * #3 is important:  this is because if two different entities have the ID property
     * already populated, then they have already been inserted in the database, and
     * because of unique constraints on the database (i.e. your 'business key'), you
     * can <em>guarantee</em> that the objects are not the same and there is no need
     * to incur attribute-based comparisons for equals() checks.
     * <p/>
     * This little technique is a massive performance improvement given the number of times
     * equals checks happen in most applications.
     * <p/>
     * <b>IMPLEMENTATION NOTE:</b>.  When writing your {@code onEquals} implementation, <em>never</em>
     * perform equals comparisons of class attributes by referencing them directly, i.e.:
     * <pre>       *bad code*
     * this.someAttribute != null ? this.someAttribute.equals( other.someAttribute ) : other.someAttribute == null;</pre>
     * <p/>
     * This is because direct property access will bypass any Hibernate proxy entirely.  Instead, the accessor methods
     * must be used to allow the proxy to initialize itself if necessary to load the data being checked for equality:
     * <pre>       *good code*
     * getSomeAttribute() != null ? getSomeAttribute().equals(other.getSomeAttribute()) : other.getSomeAttribute() == null;</pre>
     */
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof PersistentEntity) {
            PersistentEntity e = (PersistentEntity) o;
            long thisId = getEntityId();
            long otherId = e.getEntityId();
            if (thisId > DEFAULT_ENTITY_ID && otherId > DEFAULT_ENTITY_ID) {
                return thisId == otherId &&
                        //the 'isAssignableFrom' checks exist due to proxy objects.  The classes of proxies won't
                        //equal the non-proxied class, but they will be assignable:
                        (getClass().isAssignableFrom(e.getClass()) || e.getClass().isAssignableFrom(getClass()));
            } else {
                return onEquals(e);
            }
        }

        return false;
    }

    /**
     * Subclasses must do an equals comparison based on business keys, aka 'natural keys' here.  Do <em>NOT</em> use
     * the {@link #getEntityId id} property in these checks.
     *
     * @param e the entity with which to perform attribute-based equality.
     * @return true if the current object is semantically equal to the specified object
     */
    protected abstract boolean onEquals(PersistentEntity e);

    /**
     * Standard hashCode implementation, but because of the requirements of {@link #onEquals(PersistentEntity) onEquals} this
     * method must be semantically correct with <code>onEquals</code> - its calculation should be based on the same
     * <em>business key</em> that is used to calculate <code>onEquals</code>.
     *
     * @return the object's hash code, based on the <code>onEquals</code> business key fields.
     */
    public abstract int hashCode();

    /**
     * Returns <code>toStringBuilder().toString()</code>.  Declared as 'final' to require subclasses to override
     * the {@link #toStringBuilder()} method, a cleaner and better performing mechanism for toString();
     *
     * @return toStringBuilder().toString()
     */
    public final String toString() {
        return toStringBuilder().toString();
    }

    /**
     * Returns a StringBuilder representing the toString function of the class implementation. This
     * should be overridden by all children classes to represent the object in a meaningful String format.
     *
     * @return a <tt>StringBuilder</tt> representing the <tt>toString</tt> value of this object.
     */
    public abstract StringBuilder toStringBuilder();

    /**
     * Returns the RDBMS Primary Key, aka 'surrogate key'.  <code>Long</code> surrogate keys are best
     * for RDBMS performance (for many reasons that can't be expanded on here).  But in addition to a surrogate
     * key, every single table should <em>always</em> have a 'business key' or 'natural key' - a unique constraint
     * across one or more columns that guarantees row duplicates will never occur.
     * <p/>
     * A negative return value means the object has not yet been persisted to the RDBMS.
     *
     * @return the RDBMS Primary Key, aka 'surrogate key'.
     */
    public long getEntityId() {
        return entityId;
    }

    /**
     * Should <em>never</em> be called directly.  Only via JPA or Hibernate or other EIS framework, since
     * they get the ID from the RDBMS directly.
     * <p/>
     * This method can be removed entirely if the EIS framework supports setting the ID property
     * directly (e.g. through reflection).  Hibernate does support this, it is called 'property access'.
     *
     * @param entityId the id to set
     */
    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    /**
     * Returns the entity's persistent version number which is used for optimistic locking strategies to ensure
     * two threads (even across different machines) don't simultaneously overwrite entity state.  Its value is for
     * framework support and should rarely, if ever, be referenced by the application.
     * <p/>
     * This property is not necessarily used by all subclasses, but it is pretty much required if in a
     * high-concurrency environment and/or if using distributed caching in a cluster.   It (and its corresponding
     * mutator methods) is not called 'version' to prevent eliminating that name from subclasses should the
     * business domain require a property of that name.  Also <em>entityVersion</em> is self-documenting and leaves
     * little room for incorrect interpretation.
     *
     * @return the entity's persistent version number used in optimistic locking strategies - rarely if ever referenced
     *         by the application.
     */
    public int getEntityVersion() {
        return entityVersion;
    }

    /**
     * For the same reasons as the setEntityId() method, this should only be called by a
     * framework and never directly.  Can be removed if the framework supports property access.
     *
     * @param entityVersion the entity version to set for optimistic locking strategies
     */
    public void setEntityVersion(int entityVersion) {
        this.entityVersion = entityVersion;
    }


}
