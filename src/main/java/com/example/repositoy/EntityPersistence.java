package com.example.repositoy;

import com.google.apphosting.api.DatastorePb;
import com.google.cloud.datastore.*;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;

import static com.google.cloud.datastore.StructuredQuery.*;


public class EntityPersistence<T> {

    private static final String PROJECT_ID = "project-2-id2-399020";
    private static final Datastore datastore;

    static {
        datastore = DatastoreOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();
    }

    private final KeyFactory keyFactory;

    public EntityPersistence() {
        this.keyFactory = datastore.newKeyFactory().setKind(getParameterClass().getSimpleName());
    }

    protected String getKind() {
        return getParameterClass().getSimpleName();
    }

    protected final KeyFactory getKeyFactory() {
        return keyFactory;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getParameterClass() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Entity save(FullEntity<Key> entity) {
        Transaction transaction = datastore.newTransaction();
        try {
            Entity newEntity = datastore.put(entity);
            if (newEntity == null) {
                throw new RuntimeException("Error happens in saving entity: '" + getParameterClass().getName() + "'");
            }
            transaction.commit();
            return newEntity;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }


    protected boolean update(Entity entity) {
        Transaction transaction = datastore.newTransaction();
        try {
            Entity fetched = get(entity.getKey());

            if (fetched == null) {
                throw new RuntimeException("Entity of type: '" + getParameterClass().getName() + "' with key: '" + entity.getKey().getName() + "' not found");
            }

            if (!fetched.getString("author").equals(entity.getString("author"))) {
                throw new RuntimeException("Only author of the post is allowed to edite");
            }

            datastore.update(entity);
            transaction.commit();
            return true;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }


    protected Entity get(Key key) {
        return datastore.get(key);
    }

    protected Entity get(String id) {
        Entity entity = datastore.get(keyFactory.newKey(id));
        if (entity == null) {
            throw new RuntimeException("There is no post with id: '" + id + "'");
        }
        return entity;
    }

    protected Iterator<Entity> getAll() {
        return getAll(null);
    }

    protected Iterator<Entity> getAll(String orderByColumnName) {
        EntityQuery query = null;
        if (orderByColumnName != null) {
            query = Query.newEntityQueryBuilder()
                    .setKind(this.getKind())
                    .setOrderBy(OrderBy.desc(orderByColumnName))
                    .build();
        } else {
            query = Query.newEntityQueryBuilder()
                    .setKind(this.getKind())
                    .build();
        }
        return datastore.run(query);
    }


    protected Iterator<Entity> getAllCommentsByPostId(String postId) {
        Query<Entity> query =
                Query.newEntityQueryBuilder()
                        .setKind("Comment")
                        .setFilter(
                                CompositeFilter.and(
                                        PropertyFilter.eq("postId", postId)
//                                        , PropertyFilter.ge("priority", 4)
                                )
                        )
                        .setOrderBy(OrderBy.desc("createDate"))
                        .build();

        return datastore.run(query);
    }


    protected boolean delete(Key... keys) {
        Transaction transaction = datastore.newTransaction();
        try {
            datastore.delete(keys);
            transaction.commit();
            return true;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }


}
