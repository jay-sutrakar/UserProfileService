package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import services.CouchbaseService;
import services.UserService;

public class UserServiceModule extends AbstractModule {
    @Provides
    @Singleton
    public UserService provideUserService(CouchbaseService couchbaseService) {
        return new UserService(couchbaseService);
    }
}
