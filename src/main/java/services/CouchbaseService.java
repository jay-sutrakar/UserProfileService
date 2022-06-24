package services;

import com.couchbase.client.java.*;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;
import exception.CouchbaseException;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.adapter.rxjava.RxJava2Adapter;

import static constants.Constant.*;
import static reactor.adapter.rxjava.RxJava2Adapter.monoToSingle;

public class CouchbaseService implements UserRepositoryClient {

    private static final Logger logger = LoggerFactory.getLogger(CouchbaseService.class);
    private final ReactiveCluster cluster;
    private final ReactiveBucket bucket;
    private final ReactiveScope scope;
    private final ReactiveCollection collection;

    public CouchbaseService(JsonObject cbProperties) {
        this.cluster = Cluster.connect(cbProperties.getString(CONNECTION_STRING), cbProperties.getString(USERNAME), cbProperties.getString(PASSWORD)).reactive();
        this.bucket = cluster.bucket(cbProperties.getString(BUCKET));
        this.scope = bucket.scope(cbProperties.getString(SCOPE));
        this.collection = scope.collection(cbProperties.getString(COLLECTION));
    }

    @Override
    public Single<JsonObject> add(String id, JsonObject content) {
        com.couchbase.client.java.json.JsonObject data = com.couchbase.client.java.json.JsonObject.fromJson(content.encode());
        return monoToSingle(collection.insert(id, data).map(mutationResult -> new JsonObject().put("id", id)).doOnSuccess(mutationResult -> logger.info("logType=tracking | operation=add | context=couchbase | description=\"Successfully added content in couchbase\"")).doOnError(error -> {
            logger.error("logType=error | operation=add | context=couchbase | errorClass={} | errorMessage={}", this.getClass().getName(), error.getMessage());
            throw new CouchbaseException(error.getMessage(), error);
        }));

    }

    @Override
    public Single<JsonObject> delete(String id) {
        return monoToSingle(collection.remove(id).map(mutationResult -> new JsonObject().put("id", id)).doOnSuccess(mutationResult -> logger.info("logType=tracking | operation=delete | context=couchbase | description=\"Successfully deleted document from couchbase\"")).doOnError(error -> {
            logger.error("logType=error | operation=delete | context=couchbase | errorClass={} | errorMessage={}", this.getClass().getName(), error.getMessage());
            throw new CouchbaseException(error.getMessage(), error);
        }));
    }

    @Override
    public Single<JsonObject> update(String id, JsonObject content) {
        return monoToSingle(collection.upsert(id, content).map(mutationResult -> new JsonObject().put("id", id)).doOnSuccess(mutationResult -> logger.info("logType=tracking | operation=update | context=couchbase | description=\"Successfully updated document\"")).doOnError(error -> {
            logger.error("logType=error | operation=delete | context=couchbase | errorClass={} | errorMessage={}", this.getClass().getName(), error.getMessage());
            throw new CouchbaseException(error.getMessage(), error);
        }));
    }

    @Override
    public Single<JsonObject> get(String id) {
        return monoToSingle(collection.get(id).map(GetResult::contentAsObject).map(com.couchbase.client.java.json.JsonObject::toString).map(JsonObject::new).doOnSuccess(doc -> logger.info("logType=tracking | operation=get | context=couchbase | description=\"Successfully retrieved document\"")).doOnError(error -> {
            logger.error("logType=error | operation=get | context=couchbase | errorClass={} | errorMessage={}", this.getClass().getName(), error.getMessage());
            throw new CouchbaseException(error.getMessage(), error);
        }));
    }

    @Override
    public Single<Boolean> exists(String id) {
        return monoToSingle(collection.exists(id).map(ExistsResult::exists));
    }

    @Override
    public Single<Observable<JsonObject>> executeQuery(String query) {
        return monoToSingle(scope.query(query)
                .map(reactiveQueryResult -> reactiveQueryResult.rowsAs(JsonObject.class))
                .map(RxJava2Adapter::fluxToObservable)
                .doOnSuccess(res -> logger.info("logType=tracking | operation=query | context=couchbase | description=\"Successfully executed couchbase query\""))
                .doOnError(error -> {
                    logger.error("logType=error | operation=query | context=couchbase | errorClass={} | errorMessage={}", this.getClass().getName(), error.getMessage());
                    throw new CouchbaseException(error.getMessage(), error);
                }));

    }
}
